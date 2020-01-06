package com.walking.meeting.controller;

import com.alibaba.fastjson.JSON;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.Service.UserService;
import com.walking.meeting.common.Const;
import com.walking.meeting.common.Response;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
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
@Api(tags = "ManagerController", description = "管理端模块")
@RestController("ManagerController")
@RequestMapping("/manager")
public class ManagerController {
    // TODO 添加，删除，修改会议室属性接口，强制修改会议室预约接口（算法），查看预定会议室历史（通用list，看会议室）

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "添加会议室", notes = "添加会议室")
    @PostMapping(value = "/addRoom")
    public Response addRoom(
            @ApiParam(name = "room_id", value = "会议室id") @RequestParam(value = "room_id") String roomId,
            @ApiParam(name = "room_name", value = "会议室中文名") @RequestParam(value = "room_name") String roomName,
            @ApiParam(name = "device_id_list", value = "设备id列表，格式例如:1,2,3")
            @RequestParam(value = "device_id_list") String deviceIdList,
            @ApiParam(name = "free_time_start", value = "开放时间始")
            @RequestParam(value = "free_time_start") String freeTimeStart,
            @ApiParam(name = "free_time_end", value = "开放时间末")
            @RequestParam(value = "free_time_end") String freeTimeEnd,
            @ApiParam(name = "room_scale", value = "会议室可容纳人数")
            @RequestParam(value = "room_scale") Integer roomScale, HttpServletRequest request
            ){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("添加会议室, roomId:{}, roomName:{}, deviceIdList:{}, freeTimeStart:{}, freeTimeEnd:{}, roomScale:{}",
                roomId, roomName, deviceIdList, freeTimeStart, freeTimeEnd, roomScale);
        if (Objects.isNull(roomId) || Objects.isNull(roomName)|| Objects.isNull(deviceIdList)||
                Objects.isNull(freeTimeStart)|| Objects.isNull(freeTimeEnd)|| Objects.isNull(roomScale)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // 先判断这个roomId和roomName已经存在
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        meetingRoomQuery.setRoomName(roomName);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // 存在则报错
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
        // 没有这种设备就抛异常
        deviceList.forEach(ele ->{
            if (!existDeviceList.contains(Integer.parseInt(ele))) {
                throw new ResponseException(StatusCodeEnu.NO_SUCH_DEVICE);
            }
        });
        // 添加到meeting_room表里
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setRoomId(roomId);
        meetingRoomDTO.setRoomScale(roomScale.shortValue());
        meetingRoomDTO.setRoomName(roomName);
        meetingRoomDTO.setFreeTimeStart(DateUtils.parse(freeTimeStart, TIME));
        meetingRoomDTO.setFreeTimeEnd(DateUtils.parse(freeTimeEnd, TIME));
        managerService.addMeetingRoom(meetingRoomDTO);
        // 添加到room_device表里
        RoomDeviceDTO roomDeviceDTO = new RoomDeviceDTO();
        roomDeviceDTO.setRoomId(roomId);
        deviceList.forEach(deviceId -> {
            // 把List每个元素加入room_device表
            roomDeviceDTO.setDeviceId(Integer.parseInt(deviceId.toString()));
            managerService.addRoomDevice(roomDeviceDTO);
        });
        return ResponseUtils.returnDefaultSuccess();
    }



    @ApiOperation(value = "添加device", notes = "添加device")
    @PostMapping(value = "/addDevice")
    public Response addDevice(
            @ApiParam(name = "device_id", value = "设备id") @RequestParam(value = "device_id") Integer deviceId,
            @ApiParam(name = "device_type", value = "设备类型") @RequestParam(value = "device_type") String deviceType,
            HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("添加device, deviceId:{}, deviceType:{}", deviceId, deviceType);
        if (Objects.isNull(deviceId) || Objects.isNull(deviceType)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceId(deviceId);
        deviceQuery.setDeviceType(deviceType);
        DeviceDTO device= managerService.getDeviceByDeviceQuery(deviceQuery);
        // 如果这种设备已经存在，就报错
        if (ObjectUtils.isNotEmpty(device)) {
            throw new ResponseException(StatusCodeEnu.DEVICE_ALREADY_EXIST);
        }
        // 添加device的时候device_id不能重复
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);
        deviceDTO.setDeviceType(deviceType);
        managerService.addDevice(deviceDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "删除device", notes = "删除device")
    @PostMapping(value = "/delDevice")
    public Response delDevice(
            @ApiParam(name = "device_id", value = "设备id")
            @RequestParam(value = "device_id") Integer deviceId, HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("删除device, deviceId:{}", deviceId);
        if (Objects.isNull(deviceId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        // 删除设备前先校验设备是否存在
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setDeviceId(deviceId);
        DeviceDTO device= managerService.getDeviceByDeviceQuery(deviceQuery);
        // 如果这种设备不存在，就报错
        if (ObjectUtils.isEmpty(device)) {
            throw new ResponseException(StatusCodeEnu.NO_SUCH_DEVICE);
        }
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceId(deviceId);
        deviceDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateDeviceSelective(deviceDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "删除会议室", notes = "删除会议室")
    @PostMapping(value = "/delRoom")
    public Response delRoom(
            @ApiParam(name = "room_id", value = "会议室id")
            @RequestParam(value = "room_id") String roomId, HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("删除会议室, roomId:{}", roomId);
        if (Objects.isNull(roomId)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        meetingRoomQuery.setRoomId(roomId);
        MeetingRoomDO meetingRoomDO = DbUtils.getOne(managerService.getMeetingRoomByQuery(meetingRoomQuery))
                .orElse(null);
        // 没有这个会议室就报错
        if (ObjectUtils.isEmpty(meetingRoomDO)) {
            throw new ResponseException(StatusCodeEnu.MEETING_ROOM_NOT_EXIST);
        }
        MeetingRoomDTO meetingRoomDTO = new MeetingRoomDTO();
        meetingRoomDTO.setRoomId(roomId);
        meetingRoomDTO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
        managerService.updateRoomSelective(meetingRoomDTO);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "添加department", notes = "添加department")
    @PostMapping(value = "/addDepartment")
    public Response addDepartment(
            @ApiParam(name = "department_name", value = "部门名字")
            @RequestParam(value = "department_name")String departmentName,
            @ApiParam(name = "department_level", value = "部门等级")
            @RequestParam(value = "department_level") Integer departmentLevel
            , HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("添加department, departmentName:{}, departmentLevel:{}", departmentName, departmentLevel);
        if (Objects.isNull(departmentName) || Objects.isNull(departmentLevel)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery departmentQuery = new DepartmentQuery();
        departmentQuery.setDepartmentLevel(departmentLevel);
        departmentQuery.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(departmentQuery);
        // 这个部门已经存在了，就报错
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

    @ApiOperation(value = "列出department列表", notes = "列出department列表")
    @GetMapping(value = "/listDepartment")
    public Response<List<DepartmentDTO>> listDepartment(HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("列出department列表");
        List<DepartmentDTO> resultList = managerService.listDepartment();
        return ResponseUtils.returnSuccess(resultList);
    }

    @ApiOperation(value = "列出device列表", notes = "列出device列表")
    @GetMapping(value = "/listDevice")
    public Response<List<DeviceDTO>> listDevice(HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("列出device列表");
        List<DeviceDTO> resultList = managerService.listDevice();
        return ResponseUtils.returnSuccess(resultList);
    }

    @ApiOperation(value = "列出会议室列表", notes = "列出会议室列表")
    @GetMapping(value = "/listMeetingRoom")
    public Response<List<MeetingRoomDTO>> listMeetingRoom(HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("列出会议室列表");
        List<MeetingRoomDTO> resultList = managerService.listMeetingRoom();
        return ResponseUtils.returnSuccess(resultList);
    }


    @ApiOperation(value = "删除department", notes = "删除department")
    @PostMapping(value = "/delDepartment")
    public Response delDepartment(
            @ApiParam(name = "department_name", value = "部门名字")
            @RequestParam(value = "department_name") String departmentName, HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("删除department, departmentName:{}", departmentName);
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

    @ApiOperation(value = "给用户设置department", notes = "给用户设置department")
    @PostMapping(value = "/setDepartment")
    public Response setDepartmentForUser(
            @ApiParam(name = "username", value = "要设置的那个用户的名字")
            @RequestParam(value = "username") String username,
            @ApiParam(name = "department_name", value = "部门名字")
            @RequestParam(value = "department_name") String departmentName, HttpServletRequest request){
        UserDO userDO = (UserDO) request.getSession().getAttribute(Const.CURRENT_USER);
        if (userDO.getRoleId() != 0) {
            throw new ResponseException(StatusCodeEnu.NOT_MANAGER);
        }
        log.info("给用户:{}设置department:{}", username, departmentName);
        if (Objects.isNull(departmentName) || Objects.isNull(username)) {
            throw new ResponseException(StatusCodeEnu.PORTION_PARAMS_NULL_ERROR);
        }
        DepartmentQuery departmentQuery = new DepartmentQuery();
        departmentQuery.setDepartmentName(departmentName);
        List<DepartmentDTO> departmentDTOList = managerService.getDepartmentByDepartmentQuery(departmentQuery);
        if (CollectionUtils.isEmpty(departmentDTOList)) {
            throw new ResponseException(StatusCodeEnu.DEPARTMENT_NOT_EXIST);
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setUserName(username);
        UserDO userDoInDB = userService.getUserByUserQuery(userQuery);
        if (ObjectUtils.isEmpty(userDoInDB)) {
            throw new ResponseException(StatusCodeEnu.USERNAME_NOT_EXIST);
        }
        userDoInDB.setDepartmentName(departmentName);
        userService.updateUserSelective(userDoInDB);
        return ResponseUtils.returnDefaultSuccess();
    }

    @ApiOperation(value = "测试连接", notes = "测试连接")
    @PostMapping(value = "/test")
    public Response testConnection(
            @ApiParam(name = "input", value = "入参")
            @RequestParam(value = "input") String input,
            HttpSession httpSession) {
        httpSession.setAttribute("测试入参",input);
        log.info("有人在测试连接, input:{}", input);
        return ResponseUtils.returnSuccess(input);
    }


}