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
}
