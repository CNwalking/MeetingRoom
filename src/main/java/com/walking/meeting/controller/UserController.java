package com.walking.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.walking.meeting.Service.MeetingService;
import com.walking.meeting.Service.TokenService;
import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.*;
import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

import static com.walking.meeting.utils.DateUtils.FORMAT_YYYY_MM_DD_HH_MM;

@CrossOrigin
@Slf4j
@Api(tags = "UserController", description = "user module")
@RestController("UserController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "user login", notes = "user login")
    @PassToken
    @PostMapping(value = "/login")
    public Response userLogin(
            @ApiParam(name = "login_name", value = "login name") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "password") @RequestParam(value = "password") String password,
            @ApiParam(name = "user_role", value = "0:administrator 1:general user") @RequestParam(
                    value = "user_role",required=false,defaultValue="1") Integer userRole,
            HttpServletRequest request) {
        log.info("user login, loginName:{}, password:{}, userRole:{}", loginName, password, userRole);
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password) || Objects.isNull(userRole)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (Objects.isNull(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIST);
        }
        // Judge the correctness of the account password, the password encrypt by MD5,
        // throw an exception if they are not equal
        if (!StringUtils.equals(userDO.getPswd(),MD5Encrypt.md5Encrypt(password))){
            log.info("In the database: {}, encrypted data: {}",userDO.getPswd(),MD5Encrypt.md5Encrypt(password));
            throw new ResponseException(StatusCodeEnu.USERNAME_OR_PSWD_ERROR);
        }
        // judge user_role
        if (!userRole.equals(userDO.getRoleId())) {
            throw new ResponseException(StatusCodeEnu.USER_ROLE_ERROR);
        }
        // Add to session
//        request.getSession().setAttribute(Const.CURRENT_USER,userDO);
        // generate token
        JSONObject jsonObject=new JSONObject();
        String token = tokenService.generateToken(loginName);
        jsonObject.put("token", token);
        jsonObject.put("user", userDO);
        tokenService.save(token,userDO.getUsername());
        return ResponseUtils.returnSuccess(jsonObject);
    }


    @ApiOperation(value = "register", notes = "register")
    @PassToken
    @PostMapping(value = "/register")
    public Response userRegister(
            @ApiParam(name = "login_name", value = "username") @RequestParam(value = "login_name") String loginName,
            @ApiParam(name = "password", value = "password") @RequestParam(value = "password") String password,
            @ApiParam(name = "question", value = "security question") @RequestParam(value = "question") String question,
            @ApiParam(name = "email", value = "email") @RequestParam(value = "email") String email,
            @ApiParam(name = "answer", value = "answer of security question") @RequestParam(value = "answer") String answer) {
        log.info("register request,username:{},password:{},question:{},answer:{},email:{}",loginName,password,question,answer,email);
        if (Objects.isNull(loginName)||Objects.isNull(password) || Objects.isNull(question) ||
                Objects.isNull(email) || Objects.isNull(answer)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginName);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (!Objects.isNull(userDO)){
            throw new ResponseException(StatusCodeEnu.USERNAME_EXIST);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(loginName);
        userDTO.setPswd(MD5Encrypt.md5Encrypt(password));
        userDTO.setQuestion(question);
        userDTO.setAnswer(answer);
        userDTO.setEmail(email);
        userService.addUser(userDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "delete user", notes = "delete user")
    @UserLogin
    @PostMapping(value = "/del")
    public Response userRegister(
            @ApiParam(name = "login_name", value = "username") @RequestParam(value = "login_name") String loginName,
            HttpServletRequest request){
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO isUserRoleExist = userService.getUserByUserQuery(userQuery);
        // Cannot delete user if you are not administrator
        if (isUserRoleExist.getRoleId()!=0) {
            throw new ResponseException(StatusCodeEnu.NO_RIGHT);
        }
        UserDO userDO = new UserDO();
        userDO.setUsername(loginName);
        userDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        userService.updateUserSelective(userDO);

        // room_booking table also delete the username related fields
        MeetingDO meetingDO = new MeetingDO();
        meetingDO.setUsername(loginName);
        meetingDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        meetingService.updateMeetingSelective(meetingDO);

        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "user logout", notes = "user logout")
    @UserLogin
    @PostMapping(value = "/logout")
    public Response userLogout(HttpServletRequest request){
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
//        UserDO userDo = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        tokenService.delToken(request.getHeader("token"));
        log.info("user " + username + " logout");
//        request.getSession().removeAttribute(Const.CURRENT_USER);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "Get user information", notes = "Get user information")
    @UserLogin
    @PostMapping(value = "/info")
    public Response getUserInfo(HttpServletRequest request){
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO user = userService.getUserByUserQuery(userQuery);
//        UserDO userDo = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (ObjectUtils.isNotEmpty(user)) {
            log.info("user "+ user.getUsername() +" get information");
            return ResponseUtils.returnSuccess(user);
        }
        return ResponseUtils.returnDefaultError();
    }

    @ApiOperation(value = "Change password under login", notes = "Change password under login")
    @UserLogin
    @PostMapping(value = "/reset/online")
    public Response resetPasswordOnline(
        @ApiParam(name = "new_password", value = "new password") @RequestParam(value = "new_password") String newPassword,
        @ApiParam(name = "new_password_confirm", value = "new password confirm")
        @RequestParam(value = "new_password_confirm") String newPasswordConfirm, HttpServletRequest request){
        if (!newPasswordConfirm.equals(newPassword)) {
            throw new ResponseException(StatusCodeEnu.TWO_PSWD_NOT_SAME);
        }
//        UserDO userDo = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        log.info("user "+ username +" change password under login");
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO user = userService.getUserByUserQuery(userQuery);
        // New and old passwords cannot be the same
        if (StringUtils.equals(user.getPswd(),MD5Encrypt.md5Encrypt(newPassword))){
            log.info("In the database: {}, encrypted data: {}",user.getPswd(),MD5Encrypt.md5Encrypt(newPassword));
            throw new ResponseException(StatusCodeEnu.TWO_PSWD_SAME);
        }
        user.setPswd(MD5Encrypt.md5Encrypt(newPassword));
        userService.updateUserSelective(user);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "Change password without logging in", notes = "Change password without logging in")
    @PassToken
    @PostMapping(value = "/reset/offline")
    public Response resetPasswordOnline(
            @ApiParam(name = "username", value = "username") @RequestParam(value = "username") String username,
            @ApiParam(name = "new_password", value = "new password") @RequestParam(value = "new_password") String newPassword,
            @ApiParam(name = "question", value = "security question") @RequestParam(value = "question") String question,
            @ApiParam(name = "answer", value = "answer of security question") @RequestParam(value = "answer") String answer){
        log.info("Change password without logging in,username:{},password:{},question:{},answer:{}",username,newPassword,question,answer);
        // 参数判空
        if (Objects.isNull(username)||Objects.isNull(newPassword) || Objects.isNull(question) || Objects.isNull(answer)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (ObjectUtils.isEmpty(userDO)) {
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIST);
        }
        if (!userDO.getQuestion().equals(question)) {
            throw new ResponseException(StatusCodeEnu.QUESTION_NOT_RIGHT);
        }
        if (!userDO.getAnswer().equals(answer)) {
            throw new ResponseException(StatusCodeEnu.ANSWER_NOT_RIGHT);
        }
        userDO.setPswd(MD5Encrypt.md5Encrypt(newPassword));
        userService.updateUserSelective(userDO);
        return ResponseUtils.returnDefaultSuccess();
    }


//    public static void main(String[] args) {
//        String password = "qwe123";
//        String EnPassword = MD5Encrypt.md5Encrypt(password);
//        boolean isTrue = MD5Encrypt.verify(password, EnPassword);
//        System.out.println(EnPassword+"   "+isTrue);
//    }


}
