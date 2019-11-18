package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.dataobject.dao.*;
import com.walking.meeting.dataobject.dto.DepartmentDTO;
import com.walking.meeting.dataobject.dto.DeviceDTO;
import com.walking.meeting.dataobject.dto.MeetingRoomDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceDTO;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.mapper.DepartmentMapper;
import com.walking.meeting.mapper.DeviceMapper;
import com.walking.meeting.mapper.MeetingRoomMapper;
import com.walking.meeting.mapper.RoomDeviceMapper;
import com.walking.meeting.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private RoomDeviceMapper roomDeviceMapper;
    @Autowired
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public void addDevice(DeviceDTO deviceDTO) {
        DeviceDO deviceDO =  JSON.parseObject(JSON.toJSONString(deviceDTO), DeviceDO.class);
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
        Example.Builder builder = DbUtils.newExampleBuilder(DeviceDO.class);
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
    public void addMeetingRoom(MeetingRoomDTO meetingRoomDTO) {
        MeetingRoomDO meetingRoomDO =  JSON.parseObject(JSON.toJSONString(meetingRoomDTO), MeetingRoomDO.class);
        meetingRoomDO.setCreateTime(new Date());
        meetingRoomMapper.insertSelective(meetingRoomDO);
    }

    @Override
    public MeetingRoomDO getMeetingRoomByQuery(MeetingRoomQuery meetingRoomQuery) {
        Example.Builder builder = DbUtils.newExampleBuilder(MeetingRoomDO.class);
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_NAME, meetingRoomQuery.getRoomName());
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_ID, meetingRoomQuery.getRoomId());
        DbUtils.setEqualToProp(builder, MeetingRoomDO.PROP_ROOM_SCALE, meetingRoomQuery.getRoomScale());
        List<MeetingRoomDO> meetingRoomDOList = meetingRoomMapper.selectByExample(builder.build());
        return DbUtils.getOne(meetingRoomDOList).orElse(null);
    }
}
