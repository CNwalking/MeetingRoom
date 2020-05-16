package com.walking.meeting.controller;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.*;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.DepartmentDTO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.dataobject.query.DepartmentQuery;
import com.walking.meeting.dataobject.vo.MeetingRoomVO;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.ResponseUtils;
import com.walking.meeting.utils.SnowFlakeIdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static com.walking.meeting.utils.DateUtils.*;

@CrossOrigin
@Slf4j
@Api(tags = "MeetingController", description = "Meeting module")
@RestController("MeetingController")
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private ManagerService managerService;

    @UserLogin
    @ApiOperation(value = "View a list of all meeting reservations", notes = "View a list of all meeting reservations")
    @PostMapping(value = "/list")
    public Response<PageInfo<MeetingReturnDTO>> meetingList(
            @ApiParam(name = "username", value = "booker")
            @RequestParam(value = "username",required = false) String username,
            @ApiParam(name = "room_id", value = "roomId")
            @RequestParam(value = "room_id",required = false) String roomId,
            @ApiParam(name = "room_name", value = "room name")
            @RequestParam(value = "room_name",required = false) String roomName,
            @ApiParam(name = "required_time", value = "required time for example 1.5")
            @RequestParam(value = "required_time",required = false) String requiredTime,
            @ApiParam(name = "booking_date", value = "yyyy-MM-dd")
            @RequestParam(value = "booking_date",required = false) String bookingDate,
            @ApiParam(name = "department_name", value = "booking department")
            @RequestParam(value = "department_name",required = false) String departmentName,
            @ApiParam(name = "page_num", value = "page number") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "page size,0 means all") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        log.info("View meeting reservation list, username:{}, roomId:{}, roomName:{}, requiredTime:{}, departmentName:{}, pageNum:{}, pageSize:{}",
                username, roomId, roomName, requiredTime, departmentName, pageNum, pageSize);
        ListMeetingDTO listMeetingDTO = new ListMeetingDTO();
        listMeetingDTO.setUsername(Optional.ofNullable(username).orElse(""));
        listMeetingDTO.setRoomId(Optional.ofNullable(roomId).orElse(""));
        listMeetingDTO.setRoomName(Optional.ofNullable(roomName).orElse(""));
        if (StringUtils.isNotEmpty(bookingDate)) {
            listMeetingDTO.setBookingDate(Optional.ofNullable(parseDateFormatToSQLNeed(bookingDate)).orElse(""));
        }
        if (StringUtils.isNotBlank(requiredTime) && requiredTime != "") {
            listMeetingDTO.setRequiredTime(new BigDecimal(requiredTime.trim()));
        }
        listMeetingDTO.setDepartmentName(Optional.ofNullable(departmentName).orElse(""));
        listMeetingDTO.setPageNum(pageNum);
        listMeetingDTO.setPageSize(pageSize);
        PageInfo<MeetingReturnDTO> meetingDOPageInfo = meetingService.listMeeting(listMeetingDTO);

        return ResponseUtils.returnSuccess(meetingDOPageInfo);
    }

    @UserLogin
    @ApiOperation(value = "book meeting", notes = "book meeting")
    @PostMapping(value = "/booking")
    public Response meetingBooking(
            @ApiParam(name = "meeting_name", value = "meeting name") @RequestParam(value = "meeting_name") String meetingName,
            @ApiParam(name = "room_id", value = "meeting room id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "username", value = "room booker") @RequestParam(value = "username") String username,
            @ApiParam(name = "booking_date", value = "booking date yyyy-MM-dd") @RequestParam(value = "booking_date") String bookingDate,
            @ApiParam(name = "start_time", value = "start time yyyy-MM-dd HH:mm") @RequestParam(value = "start_time") String startTime,
            @ApiParam(name = "end_time", value = "end time yyyy-MM-dd HH:mm") @RequestParam(value = "end_time") String endTime,
            @ApiParam(name = "required_time", value = "meeting duration")
            @RequestParam(value = "required_time") BigDecimal requiredTime,
            @ApiParam(name = "department_name", value = "meeting room booking department")
            @RequestParam(value = "department_name") String departmentName,
            @ApiParam(name = "meeting_level", value = "0Interview 1Regular Meeting 2High Level 3EMERGENCY")
            @RequestParam(value = "meeting_level") Integer meetingLevel){
        if (StringUtils.isBlank(meetingName) || StringUtils.isBlank(roomId) || StringUtils.isBlank(username) ||
            StringUtils.isBlank(bookingDate) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime) ||
            StringUtils.isBlank(departmentName) || meetingLevel == null || requiredTime == null
        ) {
            throw new ResponseException(StatusCodeEnu.MEETING_PARAMETER_ERROR);
        }
        // First determine whether the selected date of the conference room is available, and then whether the time is available.
        // After these two judgments are completed, add Meeting to the meeting table
        String isTimeFree = meetingService.selectTimeByDateAndRoomID(parseDateFormatToSQLNeed(bookingDate),roomId);
        if (isTimeFree.equals(Const.ROOM_FULL_TIME)){
            log.info("Meeting room: {}, on date: {} is fully booked", roomId, bookingDate);
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_FULL);
        }
        if (isTimeFree.equals(Const.ROOM_CROWDED)) {
            log.info("Meeting room: {}, on date: {} need to enter the waiting queue", roomId, bookingDate);
        }
        if (isTimeFree.equals(Const.ROOM_AVAILABLE)) {
            log.info("Meeting room: {}, can be booked directly at the date: {}", roomId, bookingDate);
        }
        // whether the time you want to book is legal.
        Boolean isMeetingTimeAvailable = meetingService.isTimeAvailable(startTime, endTime,
                parseDateFormatToSQLNeed(bookingDate), roomId);
        // false, the planned meeting time is unreasonable
        if (!isMeetingTimeAvailable){
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
        }
        MeetingDTO meetingDTO = new MeetingDTO();
        String meetingId = String.valueOf(SnowFlakeIdGenerator.getIdGenerator());
        meetingDTO.setMeetingId(meetingId);
        // Construct a conference name
        String meetingLevelName = "";
        if (meetingLevel == 0){
            meetingLevelName = "Interview";
        } else if (meetingLevel == 1) {
            meetingLevelName = "Regular Meeting";
        } else if (meetingLevel == 2) {
            meetingLevelName = "High Level";
        } else if (meetingLevel == 3) {
            meetingLevelName = "EMERGENCY";
        }
        StringBuilder meetingNameBuilder = new StringBuilder();
        String defaultMeetingName = meetingNameBuilder.append(departmentName).append(" ")
                .append(meetingLevelName).append(" ").append(meetingName).toString();
        meetingDTO.setMeetingName(defaultMeetingName);
        meetingDTO.setUsername(username);
        meetingDTO.setMeetingLevel(meetingLevel);
        meetingDTO.setRoomId(roomId);
        meetingDTO.setDepartmentName(departmentName);
        // If start time - end time != duration time or start time> end time, then throw exception
        Date start = DateUtils.parse(startTime, FORMAT_YYYY_MM_DD_HH_MM);
        Date end = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM);
        log.info("startTime:{},endTime:{}", start, end);
        if (!new BigDecimal(DateUtils.getMeetingRequiredTime(startTime, endTime)).equals(requiredTime)
            || start.getTime() > end.getTime()) {
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_ERROR);
        }
        meetingDTO.setRequiredTime(requiredTime);
        meetingDTO.setBookingDate(DateUtils.parse(bookingDate,FORMAT_YYYY_MM_DD));
        meetingDTO.setBookingStartTime(start);
        meetingDTO.setBookingEndTime(end);
        meetingService.addMeeting(meetingDTO);

        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "cancel meeting", notes = "cancel meeting")
    @PostMapping(value = "/cancel")
    public Response meetingCancel(
            @ApiParam(name = "meeting_id", value = "会议id") @RequestParam(value = "meeting_id") String meetingId){
        log.info("cancel meeting, meetingId:{}", meetingId);
        if (Objects.isNull(meetingId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        MeetingDO meetingDO =  meetingService.searchMeetingByMeetingId(meetingId);
        if (ObjectUtils.isEmpty(meetingDO)) {
            // Note that there is no such meeting, it may have been cancelled, maybe the input of meetingId is wrong
            throw new ResponseException(StatusCodeEnu.MEETING_ID_NOT_EXIST);
        }
        meetingDO.setDeleteTime(formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);
        return ResponseUtils.returnDefaultSuccess();

    }

    @UserLogin
    @ApiOperation(value = "View meeting room equipment", notes = "View meeting room equipment")
    @PostMapping(value = "/search_device_by_room_id")
    public Response<List<Integer>> searchDeviceByRoomId(
            @ApiParam(name = "room_id", value = "房间id")
            @RequestParam(value = "room_id") String roomId){
        log.info("View meeting room equipment, roomId:{}", roomId);
        if (Objects.isNull(roomId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        return ResponseUtils.returnSuccess(meetingService.searchDeviceByRoomId(roomId));
    }

    @UserLogin
    @ApiOperation(value = "Select meeting rooms by meeting room equipment and scale",
            notes = "Select meeting rooms by meeting room equipment and scale")
    @PostMapping(value = "/select")
    public Response<PageInfo<MeetingRoomVO>> meetingRoomSearchingByDQuery(
            @ApiParam(name = "device_id_list", value = "device id list for example:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList,
            @ApiParam(name = "room_scale", value = "meeting room capacity")
            @RequestParam(value = "room_scale") Integer roomScale,
            @ApiParam(name = "booking_date", value = "meeting date yyyy-MM-dd")
            @RequestParam(value = "booking_date") String bookingDate,
            @ApiParam(name = "page_num", value = "page number") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "Number per page: check all when it is 0") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        log.info("Select meeting rooms by meeting room equipment and scale, deviceIdList:{}, roomScale:{}", deviceIdList, roomScale);
        if (Objects.isNull(deviceIdList) || Objects.isNull(roomScale)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // Select the meeting room of the specification containing these devices
        PageInfo<MeetingRoomVO> pageInfo = meetingService.searchRoomByQuery(parseDateFormatToSQLNeed(bookingDate),
                deviceIdList, roomScale, pageNum, pageSize);
        return ResponseUtils.returnSuccess(pageInfo);
    }

    @UserLogin
    @ApiOperation(value = "Search user list by department", notes = "Search user list by department")
    @PostMapping(value = "/department/listUser")
    public Response<List<String>> departmentUserList(
            @ApiParam(name = "department_name", value = "department name")
            @RequestParam(value = "department_name") String departmentName) {
        log.info("Search user list by department,department_name:{}", departmentName);
        if (StringUtils.isEmpty(departmentName)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery query = new DepartmentQuery();
        query.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(query);
        if (CollectionUtils.isEmpty(departmentDTOList)) {
            throw new ResponseException(StatusCodeEnu.DEPARTMENT_NOT_EXIST);
        }
        List<String> userList = managerService.getDepartmentUser(departmentName);
        return ResponseUtils.returnSuccess(userList);
    }


    private String parseDateFormatToSQLNeed(String date){
        // 2019-11-04 transfer 20191104
        String result = date.replaceAll("-","");
        return result;
    }

}
