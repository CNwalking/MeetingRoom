package com.walking.meeting.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "MeetingController", description = "会议模块")
@RestController("MeetingController")
@RequestMapping("/meeting")
public class MeetingController {
    // TODO 预定会议接口（算法），查看会议预定历史接口（通用list，按人），删除会议室接口


}
