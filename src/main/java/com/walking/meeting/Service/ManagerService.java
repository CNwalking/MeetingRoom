package com.walking.meeting.Service;

import com.walking.meeting.dataobject.dao.DeviceDO;
import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dao.RoomDeviceDO;
import com.walking.meeting.dataobject.dto.DepartmentDTO;
import com.walking.meeting.dataobject.dto.DeviceDTO;
import com.walking.meeting.dataobject.dto.MeetingRoomDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceDTO;
import com.walking.meeting.dataobject.query.DepartmentQuery;
import com.walking.meeting.dataobject.query.DeviceQuery;
import com.walking.meeting.dataobject.query.MeetingRoomQuery;

import java.util.List;

public interface ManagerService {
    List<DepartmentDTO> listDepartment();

    List<DeviceDTO> listDevice();

    List<MeetingRoomDTO> listMeetingRoom();

    void addDevice(DeviceDTO deviceDTO);

    void addDepartment(DepartmentDTO departmentDTO);

    void updateDeviceSelective(DeviceDTO deviceDTO);

    void updateDepartmentSelective(DepartmentDTO departmentDTO);

    void updateRoomSelective(MeetingRoomDTO meetingRoomDTO);

    void addRoomDevice(RoomDeviceDTO roomDeviceDtO);

    void addMeetingRoom(MeetingRoomDTO meetingRoomDTO);

    /**
     *   只限于会议室名字、Id和规模三种属性
     */
//    MeetingRoomDO getMeetingRoomByQuery(MeetingRoomQuery meetingRoomQuery);
    List<MeetingRoomDO> getMeetingRoomByQuery(MeetingRoomQuery meetingRoomQuery);


    DeviceDTO getDeviceByDeviceQuery(DeviceQuery deviceQuery);

    List<DepartmentDTO> getDepartmentByDepartmentQuery(DepartmentQuery departmentQuery);


}
