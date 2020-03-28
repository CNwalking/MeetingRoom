package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.common.ResponseException;
import com.walking.meeting.common.StatusCodeEnu;
import com.walking.meeting.dataobject.dao.*;
import com.walking.meeting.dataobject.dto.*;
import com.walking.meeting.dataobject.query.DepartmentQuery;
import com.walking.meeting.dataobject.query.DeviceQuery;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.mapper.*;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

import static com.walking.meeting.utils.DateUtils.FORMAT_YYYY_MM_DD_HH_MM;

@Service
@Slf4j
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private RoomDeviceMapper roomDeviceMapper;
    @Autowired
    private MeetingRoomMapper meetingRoomMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<DepartmentDTO> listDepartment() {
        Example.Builder builder = DbUtils.newExampleBuilder(DepartmentDO.class);
        List<DepartmentDO> departmentDOList = departmentMapper.selectByExample(builder.build());
        List<DepartmentDTO> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(departmentDOList)) {
            departmentDOList.forEach(ele -> {
                resultList.add(JSON.parseObject(JSON.toJSONString(ele), DepartmentDTO.class));
            });
        }
        return resultList;
    }

    @Override
    public List<DeviceDTO> listDevice() {
        Example.Builder builder = DbUtils.newExampleBuilder(DeviceDO.class);
        List<DeviceDO> deviceDOList = deviceMapper.selectByExample(builder.build());
        List<DeviceDTO> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceDOList)) {
            deviceDOList.forEach(ele -> {
                resultList.add(JSON.parseObject(JSON.toJSONString(ele), DeviceDTO.class));
            });
        }
        return resultList;
    }

    @Override
    public List<MeetingRoomDTO> listMeetingRoom() {
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingRoomDO.class);
        List<MeetingRoomDO> meetingRoomDOList = meetingRoomMapper.selectByExample(builder.build());
        List<MeetingRoomDTO> resultList = new ArrayList<>();
        Map<String, List<Integer>> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(meetingRoomDOList)) {
            meetingRoomDOList.forEach(ele -> {
                resultList.add(JSON.parseObject(JSON.toJSONString(ele), MeetingRoomDTO.class));
                Example.Builder builderRoomDevice = DbUtils.newExampleBuilder(RoomDeviceDO.class);
                DbUtils.setEqualToProp(builderRoomDevice, RoomDeviceDO.PROP_ROOM_ID, ele.getRoomId());
                List<RoomDeviceDO> existDeviceList = roomDeviceMapper.selectByExample(builderRoomDevice.build());
                List<Integer> deviceIdList = new ArrayList<>();
                existDeviceList.forEach(deviceDO->{ deviceIdList.add(deviceDO.getDeviceId()); });
                map.put(ele.getRoomId(), deviceIdList);
            });
        }
        resultList.forEach(ele->{
            ele.setDeviceIdList(map.get(ele.getRoomId()));
        });
        return resultList;
    }

    @Override
    public void addDevice(DeviceDTO deviceDTO) {
        DeviceDO deviceDO =  JSON.parseObject(JSON.toJSONString(deviceDTO), DeviceDO.class);
        List<DeviceDTO> list = listDevice();
        list.forEach(ele->{
            if (deviceDO.getDeviceId().equals(ele.getDeviceId())) {
                throw new ResponseException(StatusCodeEnu.DEVICE_ID_CANNOT_REPEAT);
            }
            if (deviceDO.getDeviceType().equals(ele.getDeviceType())) {
                throw new ResponseException(StatusCodeEnu.DEVICE_TYPE_CANNOT_REPEAT);
            }
        });
        deviceDO.setCreateTime(new Date());
        deviceMapper.insertSelective(deviceDO);
    }

    @Override
    public void addDepartment(DepartmentDTO departmentDTO) {
        DepartmentDO departmentDO =  JSON.parseObject(JSON.toJSONString(departmentDTO), DepartmentDO.class);
        departmentDO.setCreateTime(new Date());
        departmentMapper.insertSelective(departmentDO);
    }

    @Override
    public void updateDeviceSelective(DeviceDTO deviceDTO) {
        if(Objects.isNull(deviceDTO.getDeviceId())){
            return;
        }
        DeviceDO deviceDO =  JSON.parseObject(JSON.toJSONString(deviceDTO), DeviceDO.class);
        Example.Builder builder = DbUtils.newExampleBuilder(DeviceDO.class);
        DbUtils.setEqualToProp(builder, DeviceDO.PROP_DEVICE_ID, deviceDO.getDeviceId());
        deviceDO.setUpdateTime(new Date());
        deviceMapper.updateByExampleSelective(deviceDO,builder.build());
    }

    @Override
    public void updateDepartmentSelective(DepartmentDTO departmentDTO) {
        if(Objects.isNull(departmentDTO.getDepartmentName())){
            return;
        }
        DepartmentDO departmentDO =  JSON.parseObject(JSON.toJSONString(departmentDTO), DepartmentDO.class);
        Example.Builder builder = DbUtils.newExampleBuilder(DepartmentDO.class);
        DbUtils.setEqualToProp(builder, DepartmentDO.PROP_DEPARTMENT_NAME, departmentDTO.getDepartmentName());
        departmentDO.setUpdateTime(new Date());
        departmentMapper.updateByExampleSelective(departmentDO,builder.build());
    }

    @Override
    public void updateRoomSelective(MeetingRoomDTO meetingRoomDTO) {
        if(Objects.isNull(meetingRoomDTO.getRoomId())){
            return;
        }
        MeetingRoomDO meetingRoomDO =  JSON.parseObject(JSON.toJSONString(meetingRoomDTO), MeetingRoomDO.class);
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingRoomDO.class);
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_ID, meetingRoomDO.getRoomId());
        meetingRoomDO.setUpdateTime(new Date());
        meetingRoomMapper.updateByExampleSelective(meetingRoomDO,builder.build());
    }

    @Override
    public void addRoomDevice(RoomDeviceDTO roomDeviceDTO) {
        RoomDeviceDO roomDeviceDO =  JSON.parseObject(JSON.toJSONString(roomDeviceDTO), RoomDeviceDO.class);
        roomDeviceDO.setCreateTime(new Date());
        roomDeviceMapper.insertSelective(roomDeviceDO);
    }

    @Override
    public void updateRoomDevice(RoomDeviceDTO roomDeviceDTO) {
        if(ObjectUtils.isEmpty(roomDeviceDTO)){
            return;
        }
        RoomDeviceDO roomDeviceDO =  JSON.parseObject(JSON.toJSONString(roomDeviceDTO), RoomDeviceDO.class);
        Example.Builder builder = DbUtils.newExampleBuilder(RoomDeviceDO.class);
        DbUtils.setEqualToProp(builder, RoomDeviceDO.PROP_ROOM_ID, roomDeviceDO.getRoomId());
        DbUtils.setEqualToProp(builder, RoomDeviceDO.PROP_DEVICE_ID, roomDeviceDO.getDeviceId());
        roomDeviceDO.setUpdateTime(new Date());
        roomDeviceMapper.updateByExampleSelective(roomDeviceDO,builder.build());
    }


    @Override
    public void addMeetingRoom(MeetingRoomDTO meetingRoomDTO) {
        MeetingRoomDO meetingRoomDO =  JSON.parseObject(JSON.toJSONString(meetingRoomDTO), MeetingRoomDO.class);
        meetingRoomDO.setCreateTime(new Date());
        meetingRoomMapper.insertSelective(meetingRoomDO);
    }

//    @Override
//    public MeetingRoomDO getMeetingRoomByQuery(MeetingRoomQuery meetingRoomQuery) {
//        Example.Builder builder = DbUtils.newExampleBuilder(MeetingRoomDO.class);
//        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_NAME, meetingRoomQuery.getRoomName());
//        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_ID, meetingRoomQuery.getRoomId());
//        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_SCALE, meetingRoomQuery.getRoomScale());
//        // 选出有所需device的room列表,device在room_device表,该方法不适用。需要用sql写。
//        List<MeetingRoomDO> meetingRoomDOList = meetingRoomMapper.selectByExample(builder.build());
//        return DbUtils.getOne(meetingRoomDOList).orElse(null);
//    }

    @Override
    public List<MeetingRoomDO> getMeetingRoomByQuery(MeetingRoomQuery meetingRoomQuery) {
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingRoomDO.class);
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_NAME, meetingRoomQuery.getRoomName());
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_ID, meetingRoomQuery.getRoomId());
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_SCALE, meetingRoomQuery.getRoomScale());
        // 选出有所需device的room列表,device在room_device表,该方法不适用。需要用sql写。
        return meetingRoomMapper.selectByExample(builder.build());
    }



    @Override
    public DeviceDTO getDeviceByDeviceQuery(DeviceQuery deviceQuery) {
        Example.Builder builder = DbUtils.newExampleBuilder(DeviceDO.class);
        DbUtils.setEqualToProp(builder, DeviceDO.PROP_DEVICE_ID, deviceQuery.getDeviceId());
        DbUtils.setEqualToProp(builder, DeviceDO.PROP_DEVICE_TYPE, deviceQuery.getDeviceType());
        // 选出有所需device的room列表,device在room_device表,该方法不适用。需要用sql写。
        DeviceDO deviceDO = deviceMapper.selectOneByExample(builder.build());
        DeviceDTO deviceDTO = null;
        if (ObjectUtils.isNotEmpty(deviceDO)) {
            deviceDTO = JSON.parseObject(JSON.toJSONString(deviceDO), DeviceDTO.class);
        }
        return deviceDTO;
    }
    @Override
    public List<DepartmentDTO> getDepartmentByDepartmentQuery(DepartmentQuery departmentQuery) {
        Example.Builder builder = DbUtils.newExampleBuilder(DepartmentDO.class);
        DbUtils.setEqualToProp(builder, DepartmentDO.PROP_DEPARTMENT_NAME, departmentQuery.getDepartmentName());
        DbUtils.setEqualToProp(builder, DepartmentDO.PROP_DEPARTMENT_LEVEL, departmentQuery.getDepartmentLevel());
        // 选出有所需device的room列表,device在room_device表,该方法不适用。需要用sql写。
        List<DepartmentDO> departmentDOList = departmentMapper.selectByExample(builder.build());
        List<DepartmentDTO> departmentDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(departmentDOList)) {
            departmentDOList.forEach(ele ->{
                departmentDTOList.add(JSON.parseObject(JSON.toJSONString(ele), DepartmentDTO.class));
            });
        }
        return departmentDTOList;
    }

    @Override
    public List<String> getDepartmentUser(String departmentName) {
        Example.Builder builder = DbUtils.newExampleBuilder(UserDO.class);
        DbUtils.setEqualToProp(builder, UserDO.PROP_DEPARTMENT_NAME, departmentName);
        List<UserDO> userDOList = userMapper.selectByExample(builder.build());
        List<String> userNameList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userDOList)) {
            userDOList.forEach(ele -> {
                userNameList.add(Optional.ofNullable(ele.getUsername()).orElse(""));
            });
        }
        return userNameList;
    }

    @Override
    public void updateRoomDeviceMethod(String roomId, List<String> deviceIdList) {

        Example.Builder builder = DbUtils.newExampleBuilder(RoomDeviceDO.class);
        DbUtils.setEqualToProp(builder, RoomDeviceDO.PROP_ROOM_ID, roomId);
        // 先选出这个roomId下的deviceIdList
        List<RoomDeviceDO> existDeviceList = roomDeviceMapper.selectByExample(builder.build());
        List<Integer> needToAddList = new ArrayList<>();
        deviceIdList.forEach(deviceId->{
            // 添加入参中没有的room_device纪录到表中
            if (!existDeviceList.contains(Integer.valueOf(deviceId))){
                needToAddList.add(Integer.valueOf(deviceId));
            }
        });
        // 剩下的是要delete的纪录
        existDeviceList.removeAll(deviceIdList);
        if (CollectionUtils.isNotEmpty(existDeviceList)) {
            existDeviceList.forEach(ele->{
                RoomDeviceDO roomDeviceDO =  new RoomDeviceDO();
                Example.Builder builderDelete = DbUtils.newExampleBuilder(RoomDeviceDO.class);
                DbUtils.setEqualToProp(builderDelete, RoomDeviceDO.PROP_ROOM_ID, roomDeviceDO.getRoomId());
                DbUtils.setEqualToProp(builderDelete, RoomDeviceDO.PROP_DEVICE_ID, roomDeviceDO.getDeviceId());
                roomDeviceDO.setUpdateTime(new Date());
                roomDeviceDO.setDeleteTime(DateUtils.formatDate(new Date(), FORMAT_YYYY_MM_DD_HH_MM));
                roomDeviceMapper.updateByExampleSelective(roomDeviceDO,builderDelete.build());
            });
        }

        if (CollectionUtils.isNotEmpty(needToAddList)) {
            needToAddList.forEach(ele->{
                RoomDeviceDO roomDeviceDO = new RoomDeviceDO();
                roomDeviceDO.setRoomId(roomId);
                roomDeviceDO.setDeviceId(ele);
                roomDeviceDO.setCreateTime(new Date());
                roomDeviceMapper.insertSelective(roomDeviceDO);
            });
        }
    }


}
