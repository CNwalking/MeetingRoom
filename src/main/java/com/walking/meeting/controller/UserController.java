package com.walking.meeting.controller;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.UserDO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@Api(tags = "UserController", description = "用户模块")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/login")
    public SuccessResponse userLogin(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "user_role", value = "0:管理员 1:普通用户") @RequestParam(
                    value = "user_role") Integer userRole) {
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password) || Objects.isNull(userRole)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // 判username是否存在
        if (userService.checkUserNameExist(loginName)){
            // false说明不存在,抛用户名不存在，请注册
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIT);
        }
        // TODO 数据库判账号密码正确性，密码md5
        UserDO userDO = new UserDO();
        userDO.setUsername(loginName);
        userDO.setPassword(password);
        userDO.setRole(userRole);

        log.info("username:{},pswd:{}",loginName,password);
        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping(value = "/register")
    public SuccessResponse userRegister(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "question", value = "密保问题") @RequestParam(value = "question") String question,
            @ApiParam(name = "answer", value = "密保问题回答") @RequestParam(value = "password") String answer) {
        // 参数判空
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password) ||
                StringUtils.isBlank(question) || StringUtils.isBlank(answer)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        log.info("username:{},pswd:{},question:{},answer:{}",loginName,password,question,answer);
        // TODO 数据库判username是否存在

        // TODO 数据库录入操作，密码md5

        return SuccessResponse.defaultSuccess();
    }
}
