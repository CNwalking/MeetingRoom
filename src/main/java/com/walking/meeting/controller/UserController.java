package com.walking.meeting.controller;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.utils.MD5Encrypt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Api(tags = "UserController", description = "用户模块")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {
    // TODO 用户登录，注册，删除接口（软删deletetime置值），人脸识别登录接口

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
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        // 判username是否存在
        if (ObjectUtils.isEmpty(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIT);
        }
        // 判账号密码正确性，密码md5,不相等就抛异常
        if (!StringUtils.equals(userDO.getPassword(),MD5Encrypt.md5Encrypt(password))){
            log.info("数据库里的:{},加密完的数据:{}",userDO.getPassword(),MD5Encrypt.md5Encrypt(password));
            throw new ResponseException(StatusCodeEnu.USERNAME_OR_PSWD_ERROR);
        }
        // session在
        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping(value = "/register")
    public SuccessResponse userRegister(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "question", value = "密保问题") @RequestParam(value = "question") String question,
            @ApiParam(name = "email", value = "email") @RequestParam(value = "email") String email,
            @ApiParam(name = "department_name", value = "部门名字") @RequestParam(value = "department_name") String departmentName,
            @ApiParam(name = "answer", value = "密保问题回答") @RequestParam(value = "answer") String answer) {
        // 参数判空
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password) ||
                StringUtils.isBlank(question) || StringUtils.isBlank(answer)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        log.info("username:{},pswd:{},question:{},answer:{}",loginName,password,question,answer);
        // 数据库判username是否存在
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        // 判username是否存在
        if (!ObjectUtils.isEmpty(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_EXIT);
        }
        // 数据库录入操作，密码md5
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(loginName);
        userDTO.setPassword(MD5Encrypt.md5Encrypt(password));
        userDTO.setQuestion(question);
        userDTO.setAnswer(answer);
        userDTO.setEmail(email);
        userDTO.setDepartmentName(departmentName);
        userService.addUser(userDTO);
        return SuccessResponse.defaultSuccess();
    }

//    public static void main(String[] args) {
//        String password = "qwe123";
//        String EnPassword = MD5Encrypt.md5Encrypt(password);
//        boolean isTrue = MD5Encrypt.verify(password, EnPassword);
//        System.out.println(EnPassword+"   "+isTrue);
//    }


}
