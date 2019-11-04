package com.walking.meeting.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "ManagerController", description = "管理端模块")
@RestController("ManagerController")
@RequestMapping("/manager")
public class ManagerController {
    // TODO 添加，删除，修改会议室属性接口，强制修改会议室预约接口（算法），查看预定会议室历史（通用list，看会议室）

}
