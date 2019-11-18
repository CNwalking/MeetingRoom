package com.walking.meeting.controller;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.ListMeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.MeetingReturnDTO;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.SnowFlakeIdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.walking.meeting.utils.DateUtils.*;

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
    public PageInfo<MeetingReturnDTO> meetingList(
            @ApiParam(name = "username", value = "会议室预定者")
            @RequestParam(value = "username",required = false) String username,
            @ApiParam(name = "room_id", value = "会议室id")
            @RequestParam(value = "room_id",required = false) String roomId,
            @ApiParam(name = "room_scale", value = "会议室可容纳人数")
            @RequestParam(value = "room_scale",required = false) Short roomScale,
            @ApiParam(name = "room_name", value = "会议室名字")
            @RequestParam(value = "room_name",required = false) String roomName,
            @ApiParam(name = "required_time", value = "会议时长")
            @RequestParam(value = "required_time",required = false) String requiredTime,
            @ApiParam(name = "department_name", value = "定会议的部门")
            @RequestParam(value = "department_name",required = false) String departmentName,
            @ApiParam(name = "page_num", value = "页码") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "每页数量：为0时查全部") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        ListMeetingDTO listMeetingDTO = new ListMeetingDTO();
        listMeetingDTO.setUsername(username);
        listMeetingDTO.setRoomId(roomId);
        listMeetingDTO.setRoomScale(roomScale);
        listMeetingDTO.setRoomName(roomName);
        if (StringUtils.isNotBlank(requiredTime) && requiredTime != "") {
            listMeetingDTO.setRequiredTime(new BigDecimal(requiredTime.trim()));
        }
        listMeetingDTO.setDepartmentName(departmentName);
        listMeetingDTO.setPageNum(pageNum);
        listMeetingDTO.setPageSize(pageSize);
        PageInfo<MeetingReturnDTO> meetingDOPageInfo = meetingService.listMeeting(listMeetingDTO);
        return meetingDOPageInfo;
    }


    @ApiOperation(value = "预定会议", notes = "预定会议")
    @PostMapping(value = "/booking")
    public SuccessResponse meetingBooking(
            @ApiParam(name = "meeting_name", value = "会议名称") @RequestParam(value = "meeting_name") String meetingName,
            @ApiParam(name = "room_id", value = "会议室id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "username", value = "会议室预定者") @RequestParam(value = "username") String username,
            @ApiParam(name = "booking_date", value = "会议日期") @RequestParam(value = "booking_date") String bookingDate,
            @ApiParam(name = "start_time", value = "会议开始时间") @RequestParam(value = "start_time") String startTime,
            @ApiParam(name = "end_time", value = "会议结束时间") @RequestParam(value = "end_time") String endTime,
            @ApiParam(name = "required_time", value = "会议时长")
            @RequestParam(value = "required_time") BigDecimal requiredTime,
            @ApiParam(name = "department_name", value = "会议室预定者的部门")
            @RequestParam(value = "department_name") String departmentName,
            @ApiParam(name = "meeting_level", value = "0面试1例会2高级3紧急")
            @RequestParam(value = "meeting_level") Integer meetingLevel){
        // 先判断会议室所选日期是否有空，再是时间是否有空，这两个判定完以后addMeeting到数据库表
        Boolean isTimeFree = meetingService.selectTimeByDateAndRoomID(parseDateFormatToSQLNeed(bookingDate),roomId);
        // false则没空
        if (!isTimeFree){
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_FULL);
        }
        // 开始时间、结束时间判定,判断想预定的时间是否合法。
        Boolean isMeetingTimeAvailable = meetingService.isTimeAvailable(startTime, endTime, bookingDate, roomId);
        // false则这个想定的会议时间不合理
        if (!isMeetingTimeAvailable){
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_FULL);
        }
        // TODO 设备相关要判定。但这个方法另写，通过设备选出roomId，不在此方法中体现，见下面方法meetingRoomSearchingByDevice


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
        }else if (meetingLevel == 2) {
            meetingLevelName = "高级会议";
        }else if (meetingLevel == 3) {
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
        // 如果 开始时间-结束时间 != 会议时间 或开始时间 >结束时间 ，那么报错
        Date start = DateUtils.parse(startTime, FORMAT_YYYY_MM_DD_HH_MM);
        Date end = DateUtils.parse(endTime, FORMAT_YYYY_MM_DD_HH_MM);
        log.info("startTime:{},endTime:{}",start,end);
        if (!new BigDecimal(DateUtils.getMeetingRequiredTime(startTime, endTime)).equals(requiredTime)
            || start.getTime() > end.getTime()) {
            throw new ResponseException(StatusCodeEnu.MEETING_TIME_ERROR);
        }
        meetingDTO.setRequiredTime(requiredTime);
        meetingDTO.setBookingDate(DateUtils.parse(bookingDate,FORMAT_YYYY_MM_DD));
        meetingDTO.setBookingStartTime(start);
        meetingDTO.setBookingEndTime(end);
        meetingService.addMeeting(meetingDTO);

        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "取消会议", notes = "取消会议")
    @PostMapping(value = "/cancel")
    public SuccessResponse meetingCancel(
            @ApiParam(name = "meeting_id", value = "会议id") @RequestParam(value = "meeting_id") String meetingId){
        MeetingDO meetingDO =  meetingService.searchMeetingByMeetingId(meetingId);
        if (ObjectUtils.isEmpty(meetingDO)) {
            // 说明没有这个会议,可能已经取消,可能meetingId输入错误
            throw new ResponseException(StatusCodeEnu.MEETING_ID_NOT_EXIST);
        }
        meetingDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);
        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "通过会议室设备选出会议室", notes = "通过会议室设备选出会议室")
    @PostMapping(value = "/select")
    public String meetingRoomSearchingByDevice(
            @ApiParam(name = "device_id_list", value = "设备id列表，格式例如:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList){
        List deviceList = new ArrayList();
        if (StringUtils.isNotBlank(deviceIdList)) {
            deviceList = Arrays.asList(deviceIdList.split(","));
        }
        //TODO
        return null;

    }

    @ApiOperation(value = "查看会议室的设备", notes = "查看会议室的设备")
    @PostMapping(value = "/search_device_by_room_id")
    public List<Integer> searchDeviceByRoomId(
            @ApiParam(name = "room_id", value = "房间id")
            @RequestParam(value = "room_id") String roomId){
        return meetingService.searchDeviceByRoomId(roomId);
    }

    private String parseDateFormatToSQLNeed(String date){
        // 2019-11-04 转成 20191104
        String result = date.replaceAll("-","");
        return result;
    }

}
