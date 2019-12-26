package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSON;
import com.walking.meeting.Service.ManagerService;
import com.walking.meeting.dataobject.dao.*;
import com.walking.meeting.dataobject.dto.*;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;
import com.walking.meeting.mapper.DepartmentMapper;
import com.walking.meeting.mapper.DeviceMapper;
import com.walking.meeting.mapper.MeetingRoomMapper;
import com.walking.meeting.mapper.RoomDeviceMapper;
import com.walking.meeting.utils.DateUtils;
import com.walking.meeting.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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

    @Override
    public List<DepartmentDTO> listDepartment() {
        Example.Builder builder = DbUtils.newExampleBuilder(DepartmentDO.class);
        List<DepartmentDO> departmentDOS = departmentMapper.selectByExample(builder.build());
        List<DepartmentDTO> resultList = new ArrayList<>();
        departmentDOS.forEach(ele -> {
            resultList.add(JSON.parseObject(JSON.toJSONString(ele), DepartmentDTO.class));
        });
        return resultList;
    }

    @Override
    public List<DeviceDTO> listDevice() {
        Example.Builder builder = DbUtils.newExampleBuilder(DeviceDO.class);
        List<DeviceDO> deviceDOS = deviceMapper.selectByExample(builder.build());
        List<DeviceDTO> resultList = new ArrayList<>();
        deviceDOS.forEach(ele -> {
            resultList.add(JSON.parseObject(JSON.toJSONString(ele), DeviceDTO.class));
        });
        return resultList;
    }

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
    public List<MeetingRoomDO> searchRoomByQuery(String deviceIdList, Integer roomScale) {
        // TODO 合起来写报错...  figure out 有什么问题
//        List<RoomDeviceSearchResultDTO> roomDeviceSearchResultDTOList = roomDeviceMapper.searchRoomIdByDevice(roomScale);
//        分开写，先通过scale来搜roomId,再用roomId来搜list
        MeetingRoomQuery meetingRoomQuery = new MeetingRoomQuery();
        short scale = Short.valueOf(String.valueOf(roomScale));
        meetingRoomQuery.setRoomScale(scale);
        List<MeetingRoomDO> meetingRoomDOList = getMeetingRoomByQuery(meetingRoomQuery);
//        log.info(meetingRoomDOList.toString());
        // 通过roomId来搜它的设备list,搜出的结果存到 HashMap<roomId,deviceList>
        Map<String, String> roomDeviceMap = new HashMap<>();
        meetingRoomDOList.forEach(meetingRoomDO -> {
            roomDeviceMapper.searchDeviceByRoomId(meetingRoomDO.getRoomId()).forEach(
                    ResultDTO ->{
                        roomDeviceMap.put(ResultDTO.getRoomId(), ResultDTO.getDeviceIdList());
                    }
            );
        });
//        log.info(roomDeviceMap.toString());
        // Map的结果{6-25=1,2,3,4,5, 4-07=1,2,3,4, 6-21=1,2,3,4}，需要进行判定来选取合适的几个MeetingRoom
        MeetingRoomQuery meetingRoomQuery2 = new MeetingRoomQuery();
        List<MeetingRoomDO> resultList = new ArrayList<>();
        Set<Map.Entry<String, String>> entrySet = roomDeviceMap.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String roomId = next.getKey();
            String deviceList = next.getValue();
            if (isContainDevice(deviceList,deviceIdList)){
                meetingRoomQuery2.setRoomId(roomId);
                resultList.add(DbUtils.getOne(getMeetingRoomByQuery(meetingRoomQuery2)).orElse(null));
            }
        }
//        log.info(resultList.toString());
        return resultList;
    }

    private Boolean isContainDevice(String roomDevice, String needDevice) {
        List<String> needDeviceList = new ArrayList();
        if (StringUtils.isNotBlank(needDevice)) {
            needDeviceList = Arrays.asList(needDevice.split(","));
        }
        for (int i = 0; i < needDeviceList.size(); i++) {
            // 如果等于-1，说明没有这个device
            if(roomDevice.indexOf(needDeviceList.get(i)) == -1){
                return false;
            }
        }
        return true;
    }


}
