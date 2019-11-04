package com.walking.meeting.controller;

import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.mapper.MeetingMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "MeetingController", description = "会议模块")
@RestController("MeetingController")
@RequestMapping("/meeting")
public class MeetingController {
    // TODO 预定会议接口（算法），查看会议预定历史接口（通用list，按人），删除会议接口

    @Autowired
    private MeetingService meetingService;

    @ApiOperation(value = "查看会议预定列表", notes = "查看会议预定列表")
    @PostMapping(value = "/list")
    public PageInfo<MeetingDO> meetingList(
            @ApiParam(name = "page_num", value = "页码") @RequestParam(value = "page_num",
                    defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(name = "page_size", value = "每页数量：为0时查全部") @RequestParam(value = "page_size",
                    defaultValue = "20", required = false) Integer pageSize){
        PageInfo<MeetingDO> meetingDOPageInfo = meetingService.listMeeting(pageNum, pageSize);
        return meetingDOPageInfo;
    }

    @ApiOperation(value = "预定会议", notes = "预定会议")
    @PostMapping(value = "/add")
    public SuccessResponse meetingBooking(
            @ApiParam(name = "meeting_name", value = "会议名称") @RequestParam(value = "meeting_name") String meetingName,
            @ApiParam(name = "username", value = "会议室预定者") @RequestParam(value = "username") String username,
            @ApiParam(name = "department_name", value = "会议室预定者的部门")
            @RequestParam(value = "department_name") String departmentName,
            @ApiParam(name = "meeting_level", value = "0面试1例会2高级3紧急")
            @RequestParam(value = "meeting_level") Integer meetingLevel){
        return SuccessResponse.defaultSuccess();
    }


}
