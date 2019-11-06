package com.walking.meeting.controller;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.SnowFlakeIdGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

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
    public PageInfo<MeetingDO> meetingList(
            @ApiParam(name = "page_num", value = "页码") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "每页数量：为0时查全部") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        PageInfo<MeetingDO> meetingDOPageInfo = meetingService.listMeeting(pageNum, pageSize);
        return meetingDOPageInfo;
    }

    @ApiOperation(value = "查看某个用户的会议预定列表", notes = "查看会议预定列表")
    @PostMapping(value = "/listByUser")
    public PageInfo<MeetingDO> meetingListOfUser(
            @ApiParam(name = "page_num", value = "页码") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "每页数量：为0时查全部") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){

        PageInfo<MeetingDO> meetingDOPageInfo = meetingService.listMeeting(pageNum, pageSize);
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
        // TODO 先判断会议室所选日期是否有空，再是时间是否有空，这两个判定完以后addMeeting到数据库表

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
        MeetingDO meetingDO = new MeetingDO();
        meetingDO.setMeetingId(meetingId);
        meetingDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);
        return SuccessResponse.defaultSuccess();
    }



}
