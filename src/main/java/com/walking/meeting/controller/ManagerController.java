package com.walking.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.Service.TokenService;
import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.*;
import com.walking.meeting.dataobject.dao.*;
import com.walking.meeting.dataobject.dto.DepartmentDTO;
import com.walking.meeting.dataobject.dto.DeviceDTO;
import com.walking.meeting.dataobject.dto.MeetingRoomDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceDTO;
import com.walking.meeting.dataobject.query.DepartmentQuery;
import com.walking.meeting.dataobject.query.DeviceQuery;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.dataobject.vo.MeetingRoomVO;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.DbUtils;
import com.walking.meeting.utils.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static com.walking.meeting.utils.DateUtils.*;

@CrossOrigin
@Slf4j
@Api(tags = "ManagerController", description = "manage module")
@RestController("ManagerController")
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @UserLogin
    @ApiOperation(value = "add meeting room", notes = "add meeting room")
    @PostMapping(value = "/addRoom")
    public Response addRoom(
            @ApiParam(name = "room_id", value = "meeting room Id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "room_name", value = "meeting room name") @RequestParam(value = "room_name") String roomName,
            @ApiParam(name = "device_id_list", value = "device id list for example:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList,
            @ApiParam(name = "free_time_start", value = "start free time")
            @RequestParam(value = "free_time_start") String freeTimeStart,
            @ApiParam(name = "free_time_end", value = "end free time")
            @RequestParam(value = "free_time_end") String freeTimeEnd,
            @ApiParam(name = "room_scale", value = "meeting room capacity")
            @RequestParam(value = "room_scale") Integer roomScale, HttpServletRequest request
            ){
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("add room req, roomId:{}, roomName:{}, deviceIdList:{}, freeTimeStart:{}, freeTimeEnd:{}, roomScale:{}",
                roomId, roomName, deviceIdList, freeTimeStart, freeTimeEnd, roomScale);
        if (StringUtils.isEmpty(roomId) || StringUtils.isEmpty(roomName)|| StringUtils.isEmpty(deviceIdList)||
                StringUtils.isEmpty(freeTimeStart)|| StringUtils.isEmpty(freeTimeEnd)|| Objects.isNull(roomScale)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // First judge if the roomId and roomName already exist
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        meetingRoomQuery.setRoomName(roomName);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        if (!Objects.isNull(meetingRoomDO)){
            throw new ResponseException(StatusCodeEnu.ROOM_EXIST);
        }
        List<Integer> existDeviceList = new ArrayList<>();
        managerService.listDevice().forEach(ele->{
            existDeviceList.add(ele.getDeviceId());
        });
        List<String> deviceList = new ArrayList();
        if (StringUtils.isNotBlank(deviceIdList)) {
            deviceList = Arrays.asList(deviceIdList.split(","));
        }
        // if there is no such device
        deviceList.forEach(ele ->{
            if (!existDeviceList.contains(Integer.parseInt(ele))) {
                throw new ResponseException(StatusCodeEnu.NO_SUCH_DEVICE);
            }
        });
        // add to meeting_room table
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setRoomId(roomId);
        meetingRoomDTO.setRoomScale(roomScale.shortValue());
        meetingRoomDTO.setRoomName(roomName);
        meetingRoomDTO.setFreeTimeStart(DateUtils.parse(freeTimeStart, TIME));
        meetingRoomDTO.setFreeTimeEnd(DateUtils.parse(freeTimeEnd, TIME));
        managerService.addMeetingRoom(meetingRoomDTO);
        // add to room_device table
        RoomDeviceDTO roomDeviceDTO = new RoomDeviceDTO();
        roomDeviceDTO.setRoomId(roomId);
        deviceList.forEach(deviceId -> {
            // add every element in List into room_device table
            roomDeviceDTO.setDeviceId(Integer.parseInt(deviceId.toString()));
            managerService.addRoomDevice(roomDeviceDTO);
        });
        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "modify room", notes = "modify room")
    @PostMapping(value = "/modifyRoom")
    public Response modifyRoom(
            @ApiParam(name = "room_id", value = "meeting room Id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "room_name", value = "meeting room name") @RequestParam(value = "room_name") String roomName,
            @ApiParam(name = "device_id_list", value = "device id list for example:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList,
            @ApiParam(name = "free_time_start", value = "start free time")
            @RequestParam(value = "free_time_start") String freeTimeStart,
            @ApiParam(name = "free_time_end", value = "end free time")
            @RequestParam(value = "free_time_end") String freeTimeEnd,
            @ApiParam(name = "room_scale", value = "meeting room capacity")
            @RequestParam(value = "room_scale") Integer roomScale, HttpServletRequest request
    ){
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("modify room, roomId:{}, roomName:{}, deviceIdList:{}, freeTimeStart:{}, freeTimeEnd:{}, roomScale:{}",
                roomId, roomName, deviceIdList, freeTimeStart, freeTimeEnd, roomScale);
        if (StringUtils.isEmpty(roomId) || StringUtils.isEmpty(roomName)|| StringUtils.isEmpty(deviceIdList)||
                StringUtils.isEmpty(freeTimeStart)|| StringUtils.isEmpty(freeTimeEnd)|| Objects.isNull(roomScale)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        meetingRoomQuery.setRoomName(roomName);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        if (Objects.isNull(meetingRoomDO)){
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_NOT_EXIST);
        }
        List<Integer> existDeviceList = new ArrayList<>();
        managerService.listDevice().forEach(ele->{ existDeviceList.add(ele.getDeviceId()); });
        List<String> deviceList = new ArrayList();
        if (StringUtils.isNotBlank(deviceIdList)) {
            deviceList = Arrays.asList(deviceIdList.split(","));
        }
        deviceList.forEach(ele ->{
            if (!existDeviceList.contains(Integer.parseInt(ele))) {
                throw new ResponseException(StatusCodeEnu.NO_SUCH_DEVICE);
            }
        });
        // update data to meeting_room table
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setRoomId(roomId);
        meetingRoomDTO.setRoomScale(roomScale.shortValue());
        meetingRoomDTO.setRoomName(roomName);
        meetingRoomDTO.setFreeTimeStart(DateUtils.parse(freeTimeStart, TIME));
        meetingRoomDTO.setFreeTimeEnd(DateUtils.parse(freeTimeEnd, TIME));
        managerService.updateRoomSelective(meetingRoomDTO);
        // update data to room_device table
        managerService.updateRoomDeviceMethod(roomId,deviceList);
        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "add device", notes = "add device")
    @PostMapping(value = "/addDevice")
    public Response addDevice(
            @ApiParam(name = "device_id", value = "device id") @RequestParam(value = "device_id") Integer deviceId,
            @ApiParam(name = "device_type", value = "device type") @RequestParam(value = "device_type") String deviceType,
            HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("add device, deviceId:{}, deviceType:{}", deviceId, deviceType);
        if (Objects.isNull(deviceId) || Objects.isNull(deviceType)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceId(deviceId);
        deviceQuery.setDeviceType(deviceType);
        DeviceDTO device= managerService.getDeviceByDeviceQuery(deviceQuery);
        if (ObjectUtils.isNotEmpty(device)) {
            throw new ResponseException(StatusCodeEnu.DEVICE_ALREADY_EXIST);
        }
        // device_id cannot be repeated when adding device
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);
        deviceDTO.setDeviceType(deviceType);
        managerService.addDevice(deviceDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "delete device", notes = "delete device")
    @PostMapping(value = "/delDevice")
    public Response delDevice(
            @ApiParam(name = "device_id", value = "device id")
            @RequestParam(value = "device_id") Integer deviceId, HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("delete device, deviceId:{}", deviceId);
        if (Objects.isNull(deviceId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceId(deviceId);
        DeviceDTO device= managerService.getDeviceByDeviceQuery(deviceQuery);
        if (ObjectUtils.isEmpty(device)) {
            throw new ResponseException(StatusCodeEnu.NO_SUCH_DEVICE);
        }
        // Not only delete the data in device table, but also delete the data in room_device table
        RoomDeviceDTO roomDeviceDTO = new RoomDeviceDTO();
        roomDeviceDTO.setDeviceId(deviceId);
        roomDeviceDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateRoomDevice(roomDeviceDTO);

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);
        deviceDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateDeviceSelective(deviceDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "delete meeting room", notes = "delete meeting room")
    @PostMapping(value = "/delRoom")
    public Response delRoom(
            @ApiParam(name = "room_id", value = "meeting room id")
            @RequestParam(value = "room_id") String roomId, HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("delete meeting room, roomId:{}", roomId);
        if (StringUtils.isEmpty(roomId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // throw the exception without this meeting room
        if (ObjectUtils.isEmpty(meetingRoomDO)) {
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_NOT_EXIST);
        }
        // Not only update the data in device table, but also update the data in room_device table
        RoomDeviceDTO roomDeviceDTO = new RoomDeviceDTO();
        roomDeviceDTO.setRoomId(roomId);
        roomDeviceDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateRoomDevice(roomDeviceDTO);

        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setRoomId(roomId);
        meetingRoomDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateRoomSelective(meetingRoomDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @UserLogin
    @ApiOperation(value = "add department", notes = "add department")
    @PostMapping(value = "/addDepartment")
    public Response addDepartment(
            @ApiParam(name = "department_name", value = "department name")
            @RequestParam(value = "department_name")String departmentName,
            @ApiParam(name = "department_level", value = "department level")
            @RequestParam(value = "department_level") Integer departmentLevel
            , HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("add department, departmentName:{}, departmentLevel:{}", departmentName, departmentLevel);
        if (StringUtils.isEmpty(departmentName) || Objects.isNull(departmentLevel)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery departmentQuery = new DepartmentQuery();
        departmentQuery.setDepartmentLevel(departmentLevel);
        departmentQuery.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(departmentQuery);
        if (CollectionUtils.isNotEmpty(departmentDTOList)) {
            throw new ResponseException(StatusCodeEnu.DEPARTMENT_ALREADY_EXIST);
        }
        if (departmentLevel > 3) {
            throw new ResponseException(StatusCodeEnu.LEVEL_TOO_HIGH);
        }
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setDepartmentLevel(departmentLevel);
        departmentDTO.setDepartmentName(departmentName);
        managerService.addDepartment(departmentDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "list department", notes = "list department")
    @GetMapping(value = "/listDepartment")
    public Response<List<DepartmentDTO>> listDepartment(HttpServletRequest request){
        log.info("list department");
        List<DepartmentDTO> resultList = managerService.listDepartment();
        return ResponseUtils.returnSuccess(resultList);
    }

    @ApiOperation(value = "list device", notes = "list device")
    @GetMapping(value = "/listDevice")
    public Response<List<DeviceDTO>> listDevice(HttpServletRequest request){
        log.info("list device");
        List<DeviceDTO> resultList = managerService.listDevice();
        return ResponseUtils.returnSuccess(resultList);
    }

    @ApiOperation(value = "list room", notes = "list room")
    @GetMapping(value = "/listMeetingRoom")
    public Response<List<MeetingRoomDTO>> listMeetingRoom(HttpServletRequest request){
        log.info("list room");
        List<MeetingRoomDTO> resultList = managerService.listMeetingRoom();
        return ResponseUtils.returnSuccess(resultList);
    }


    @ApiOperation(value = "delete department", notes = "delete department")
    @PostMapping(value = "/delDepartment")
    public Response delDepartment(
            @ApiParam(name = "department_name", value = "delete department")
            @RequestParam(value = "department_name") String departmentName, HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String username;
        try {
            username = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("delete department, departmentName:{}", departmentName);
        if (Objects.isNull(departmentName)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery departmentQuery = new DepartmentQuery();
        departmentQuery.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(departmentQuery);
        if (CollectionUtils.isEmpty(departmentDTOList)) {
            throw new ResponseException(StatusCodeEnu.DEPARTMENT_NOT_EXIST);
        }
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setDepartmentName(departmentName);
        departmentDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateDepartmentSelective(departmentDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "set department for user", notes = "set department for user")
    @PostMapping(value = "/setDepartment")
    public Response setDepartmentForUser(
            @ApiParam(name = "username", value = "The name of the user to be set")
            @RequestParam(value = "username") String username,
            @ApiParam(name = "department_name", value = "Department name")
            @RequestParam(value = "department_name") String departmentName, HttpServletRequest request){
//        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        String loginUser;
        try {
            loginUser = tokenService.getUsername(request.getHeader("token"));
        } catch (Exception e) {
            throw new ResponseException(StatusCodeEnu.TOKEN_ERROR);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(loginUser);
        UserDO userDO = userService.getUserByUserQuery(userQuery);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }

        log.info("give the user:{} set department:{}", loginUser, departmentName);
        if (Objects.isNull(departmentName) || Objects.isNull(username)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery departmentQuery = new DepartmentQuery();
        departmentQuery.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(departmentQuery);
        if (CollectionUtils.isEmpty(departmentDTOList)) {
            throw new ResponseException(StatusCodeEnu.DEPARTMENT_NOT_EXIST);
        }

        UserQuery userQueryForUpdate = new UserQuery();
        userQueryForUpdate.setUserName(username);
        UserDO userDoInDB = userService.getUserByUserQuery(userQueryForUpdate);
        if (ObjectUtils.isEmpty(userDoInDB)) {
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIST);
        }
        userDoInDB.setDepartmentName(departmentName);
        userService.updateUserSelective(userDoInDB);
        return ResponseUtils.returnDefaultSuccess();
    }



}