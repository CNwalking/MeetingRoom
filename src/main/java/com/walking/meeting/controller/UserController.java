package com.walking.meeting.controller;

import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.common.SuccessResponse;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.MD5Encrypt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.walking.meeting.utils.DateUtils.FORMAT_YYYY_MM_DD_HH_MM;

@Slf4j
@Api(tags = "UserController", description = "用户模块")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {
    // TODO 用户登录，注册，退出，删除接口（软删deletetime置值），人脸识别登录接口，查看会议预定历史接口

    @Autowired
    private UserService userService;
    @Autowired
    private MeetingService meetingService;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/login")
    public SuccessResponse userLogin(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "user_role", value = "0:管理员 1:普通用户") @RequestParam(
                    value = "user_role",required=false,defaultValue="1") Integer userRole) {
        log.info("用户登录, loginName:{}, password:{}, userRole:{}", loginName, password, userRole);
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password) || Objects.isNull(userRole)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        // 判username是否存在
        if (Objects.isNull(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIST);
        }
        // 判账号密码正确性，密码md5,不相等就抛异常
        if (!StringUtils.equals(userDO.getPswd(),MD5Encrypt.md5Encrypt(password))){
            log.info("数据库里的:{},加密完的数据:{}",userDO.getPswd(),MD5Encrypt.md5Encrypt(password));
            throw new ResponseException(StatusCodeEnu.USERNAME_OR_PSWD_ERROR);
        }
        // TODO 加session相关
        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping(value = "/register")
    public SuccessResponse userRegister(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password,
            @ApiParam(name = "question", value = "密保问题") @RequestParam(value = "question") String question,
            @ApiParam(name = "email", value = "email") @RequestParam(value = "email") String email,
            @ApiParam(name = "answer", value = "密保问题回答") @RequestParam(value = "answer") String answer) {
        log.info("用户注册,username:{},password:{},question:{},answer:{},email:{}",loginName,password,question,answer,email);
        // 参数判空
        if (Objects.isNull(loginName)||Objects.isNull(password) || Objects.isNull(question) ||
                Objects.isNull(email) || Objects.isNull(answer)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // 数据库判username是否存在
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        // 判username是否存在
        if (!Objects.isNull(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_EXIST);
        }
        // 数据库录入操作，密码md5
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(loginName);
        userDTO.setPswd(MD5Encrypt.md5Encrypt(password));
        userDTO.setQuestion(question);
        userDTO.setAnswer(answer);
        userDTO.setEmail(email);
        userService.addUser(userDTO);
        return SuccessResponse.defaultSuccess();
    }

    @ApiOperation(value = "用户删除", notes = "用户删除")
    @PostMapping(value = "/del")
    public SuccessResponse userRegister(
            @ApiParam(name = "login_name", value = "用户名") @RequestParam(value = "login_name") String loginName){
        UserDO userDO = new UserDO();
        userDO.setUsername(loginName);
        userDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        userService.updateUserSelective(userDO);

        // room_booking表也要删除username相关字段
        MeetingDO meetingDO = new MeetingDO();
        meetingDO.setUsername(loginName);
        meetingDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);
        return SuccessResponse.defaultSuccess();
    }

    // TODO 给用户添加部门，再写个通过部门来搜出用户列表


//    public static void main(String[] args) {
//        String password = "qwe123";
//        String EnPassword = MD5Encrypt.md5Encrypt(password);
//        boolean isTrue = MD5Encrypt.verify(password, EnPassword);
//        System.out.println(EnPassword+"   "+isTrue);
//    }


}
