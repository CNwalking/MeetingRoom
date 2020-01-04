package com.walking.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.*;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.dataobject.vo.MeetingRoomVO;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.ResponseUtils;
import com.walking.meeting.utils.SnowFlakeIdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static com.walking.meeting.utils.DateUtils.*;

@CrossOrigin
@Slf4j
@Api(tags = "MeetingController", description = "会议模块")
@RestController("MeetingController")
@RequestMapping("/meeting")
public class MeetingController {
    // TODO 预定会议接口（算法），查看会议预定历史接口（通用list，按人），删除会议接口（取消会议）,搜索会议室接口（算法)

    @Autowired
    private MeetingService meetingService;

    @ApiOperation(value = "查看所有会议预定列表", notes = "查看所有会议预定列表")
    @PostMapping(value = "/list")
    public Response<PageInfo<MeetingReturnDTO>> meetingList(
            @ApiParam(name = "username", value = "会议室预定者")
            @RequestParam(value = "username",required = false) String username,
            @ApiParam(name = "room_id", value = "会议室id")
            @RequestParam(value = "room_id",required = false) String roomId,
            @ApiParam(name = "room_name", value = "会议室名字")
            @RequestParam(value = "room_name",required = false) String roomName,
            @ApiParam(name = "required_time", value = "会议时长例如1.5")
            @RequestParam(value = "required_time",required = false) String requiredTime,
            @ApiParam(name = "department_name", value = "定会议的部门")
            @RequestParam(value = "department_name",required = false) String departmentName,
            @ApiParam(name = "page_num", value = "页码") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "每页数量：为0时查全部") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        log.info("查看会议预定列表, username:{}, roomId:{}, roomName:{}, requiredTime:{}, departmentName:{}, pageNum:{}, pageSize:{}",
                username, roomId, roomName, requiredTime, departmentName, pageNum, pageSize);
        ListMeetingDTO listMeetingDTO = new ListMeetingDTO();
        listMeetingDTO.setUsername(username);
        listMeetingDTO.setRoomId(roomId);
        listMeetingDTO.setRoomName(roomName);
        if (StringUtils.isNotBlank(requiredTime) && requiredTime != "") {
            listMeetingDTO.setRequiredTime(new BigDecimal(requiredTime.trim()));
        }
        listMeetingDTO.setDepartmentName(departmentName);
        listMeetingDTO.setPageNum(pageNum);
        listMeetingDTO.setPageSize(pageSize);
        PageInfo<MeetingReturnDTO> meetingDOPageInfo = meetingService.listMeeting(listMeetingDTO);

        return ResponseUtils.returnSuccess(meetingDOPageInfo);
    }


    @ApiOperation(value = "预定会议", notes = "预定会议")
    @PostMapping(value = "/booking")
    public Response meetingBooking(
            @ApiParam(name = "meeting_name", value = "会议名称") @RequestParam(value = "meeting_name") String meetingName,
            @ApiParam(name = "room_id", value = "会议室id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "username", value = "会议室预定者") @RequestParam(value = "username") String username,
            @ApiParam(name = "booking_date", value = "会议日期yyyy-MM-dd") @RequestParam(value = "booking_date") String bookingDate,
            @ApiParam(name = "start_time", value = "会议开始时间yyyy-MM-dd HH:mm") @RequestParam(value = "start_time") String startTime,
            @ApiParam(name = "end_time", value = "会议结束时间yyyy-MM-dd HH:mm") @RequestParam(value = "end_time") String endTime,
            @ApiParam(name = "required_time", value = "会议时长")
            @RequestParam(value = "required_time") BigDecimal requiredTime,
            @ApiParam(name = "department_name", value = "会议室预定者的部门")
            @RequestParam(value = "department_name") String departmentName,
            @ApiParam(name = "meeting_level", value = "0面试1例会2高级3紧急")
            @RequestParam(value = "meeting_level") Integer meetingLevel){
        log.info("预定信息参数：会议室id:{},会议室预定者:{},会议日期:{},会议开始时间:{},会议结束时间:{},会议时长:{}," +
                "会议室预定者的部门:{},会议等级:{}",roomId,username,bookingDate,startTime,
                endTime,requiredTime,departmentName,meetingLevel);
        // 参数判空
        if (StringUtils.isBlank(meetingName) || StringUtils.isBlank(roomId) || StringUtils.isBlank(username) ||
            StringUtils.isBlank(bookingDate) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime) ||
            StringUtils.isBlank(departmentName) || meetingLevel == null || requiredTime == null
        ) {
            throw new ResponseException(StatusCodeEnu.MEETING_PARAMETER_ERROR);
        }
        // 先判断会议室所选日期是否有空，再是时间是否有空，这两个判定完以后addMeeting到数据库表
        String isTimeFree = meetingService.selectTimeByDateAndRoomID(parseDateFormatToSQLNeed(bookingDate),roomId);
        if (isTimeFree.equals(Const.ROOM_FULL_TIME)){
            log.info("会议室:{},在日期:{}已经被订满了", roomId, bookingDate);
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_FULL);
        }
        if (isTimeFree.equals(Const.ROOM_CROWDED)) {
            log.info("会议室:{},在日期:{}需要进入候补队列", roomId, bookingDate);
            // TODO 进入候补队列
        }
        if (isTimeFree.equals(Const.ROOM_AVAILABLE)) {
            log.info("会议室:{},在日期:{}时可被直接预定", roomId, bookingDate);
        }
        // 开始时间、结束时间判定,判断想预定的时间是否合法。
        Boolean isMeetingTimeAvailable = meetingService.isTimeAvailable(startTime, endTime,
                parseDateFormatToSQLNeed(bookingDate), roomId);
        // false则这个想定的会议时间不合理
        if (!isMeetingTimeAvailable){
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_ILLEGAL);
        }
        //  先进行设备相关的判定，判定完以后让用户选择room，然后读取roomId当这个方法的入参。
        //  取会议室ID这个方法另写，通过设备选出roomId，不在此方法中体现，见下面方法meetingRoomSearchingByDevice

        // 下面先什么都不管，add一个会议，到时候会判条件判了以后再add
        MeetingDTO meetingDTO = new MeetingDTO();
        String meetingId = String.valueOf(SnowFlakeIdGenerator.getIdGenerator());
        meetingDTO.setMeetingId(meetingId);
        // 构造会议名字
        String meetingLevelName = "";
        if (meetingLevel == 0){
            meetingLevelName = "面试";
        } else if (meetingLevel == 1) {
            meetingLevelName = "例会";
        } else if (meetingLevel == 2) {
            meetingLevelName = "高级会议";
        } else if (meetingLevel == 3) {
            meetingLevelName = "紧急会议";
        }
        StringBuilder meetingNameBuilder = new StringBuilder();
        String defaultMeetingName = meetingNameBuilder.append(departmentName).
                append(meetingLevelName).append(meetingName).toString();
        meetingDTO.setMeetingName(defaultMeetingName);
        meetingDTO.setUsername(username);
        meetingDTO.setMeetingLevel(meetingLevel);
        meetingDTO.setRoomId(roomId);
        meetingDTO.setDepartmentName(departmentName);
        // 此处判定了时长是否正确
        // 如果 开始时间-结束时间 != 会议时间 或开始时间 >结束时间 ，那么报错
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

    @ApiOperation(value = "取消会议", notes = "取消会议")
    @PostMapping(value = "/cancel")
    public Response meetingCancel(
            @ApiParam(name = "meeting_id", value = "会议id") @RequestParam(value = "meeting_id") String meetingId){
        log.info("取消会议, meetingId:{}", meetingId);
        if (Objects.isNull(meetingId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        MeetingDO meetingDO =  meetingService.searchMeetingByMeetingId(meetingId);
        if (ObjectUtils.isEmpty(meetingDO)) {
            // 说明没有这个会议,可能已经取消,可能meetingId输入错误
            throw new ResponseException(StatusCodeEnu.MEETING_ID_NOT_EXIST);
        }
        meetingDO.setDeleteTime(formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);
        return ResponseUtils.returnDefaultSuccess();

    }


    @ApiOperation(value = "查看会议室的设备", notes = "查看会议室的设备")
    @PostMapping(value = "/search_device_by_room_id")
    public Response<List<Integer>> searchDeviceByRoomId(
            @ApiParam(name = "room_id", value = "房间id")
            @RequestParam(value = "room_id") String roomId){
        log.info("查看会议室的设备, roomId:{}", roomId);
        if (Objects.isNull(roomId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        return ResponseUtils.returnSuccess(meetingService.searchDeviceByRoomId(roomId));
    }


    @ApiOperation(value = "通过会议室设备、规模和时间选出会议室", notes = "通过会议室设备、规模和时间选出会议室")
    @PostMapping(value = "/select")
    public Response<List<MeetingRoomVO>> meetingRoomSearchingByDQuery(
            @ApiParam(name = "device_id_list", value = "设备id列表，格式例如:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList,
            @ApiParam(name = "room_scale", value = "会议室可容纳人数")
            @RequestParam(value = "room_scale") Integer roomScale,
            @ApiParam(name = "booking_date", value = "会议日期yyyy-MM-dd")
            @RequestParam(value = "booking_date") String bookingDate){
        log.info("通过会议室设备和规模选出会议室, deviceIdList:{}, roomScale:{}", deviceIdList, roomScale);
        if (Objects.isNull(deviceIdList) || Objects.isNull(roomScale)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // 选出包含这些设备的该规格的会议室
        List<MeetingRoomVO> resultList = new ArrayList<>();
        List<MeetingRoomDO> roomList = meetingService.searchRoomByQuery(deviceIdList, roomScale);
        Map<String, String> roomMap = new HashMap<>();
        roomList.forEach(meetingRoomDO -> {
//            // 时间转化成看的清楚一些的时间，例如18：00
//            String StartTime = DateUtils.formatDate(meetingRoomDO.getFreeTimeStart(),SHOWTIME);
//            String endTime = DateUtils.formatDate(meetingRoomDO.getFreeTimeEnd(),SHOWTIME);
            String isBusy = meetingService.selectTimeByDateAndRoomID(parseDateFormatToSQLNeed(bookingDate), meetingRoomDO.getRoomId());
            MeetingRoomVO meetingRoomVO = JSON.parseObject(JSON.toJSONString(meetingRoomDO), MeetingRoomVO.class);
            meetingRoomVO.setFreeTimeStart(meetingRoomDO.getFreeTimeStart());
            meetingRoomVO.setFreeTimeEnd(meetingRoomDO.getFreeTimeEnd());
            if (isBusy == Const.ROOM_CROWDED) {
                meetingRoomVO.setBusyOrNot(8);
            }
            if (isBusy == Const.ROOM_JUST_SOSO) {
                meetingRoomVO.setBusyOrNot(6);
            }
            if (isBusy == Const.ROOM_AVAILABLE) {
                meetingRoomVO.setBusyOrNot(1);
            }
            resultList.add(meetingRoomVO);
        });
        return ResponseUtils.returnSuccess(resultList);
    }

    private String parseDateFormatToSQLNeed(String date){
        // 2019-11-04 转成 20191104
        String result = date.replaceAll("-","");
        return result;
    }

}
