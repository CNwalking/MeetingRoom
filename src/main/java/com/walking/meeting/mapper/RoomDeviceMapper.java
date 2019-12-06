/** 
 * RoomDeviceMapper.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:55:52 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.MeetingRoomDO;
import com.walking.meeting.dataobject.dao.RoomDeviceDO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import com.walking.meeting.dataobject.dto.RoomDeviceSearchResultDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface RoomDeviceMapper extends Mapper<RoomDeviceDO> {
    /**
     * 通过roomId搜它的设备
     * @param roomId
     * @return
     */
    @Select({
            "SELECT rd.room_id,GROUP_CONCAT(rd.device_id)",
                    "FROM room_device rd",
                    "WHERE rd.room_id = #{roomId}",
                    "GROUP BY rd.room_id"
    })
    @Results({
        @Result(column = "room_id", property = "roomId"),
        @Result(column = "GROUP_CONCAT(rd.device_id)", property = "deviceIdList")
    })
    List<RoomDeviceSearchResultDTO> searchDeviceByRoomId(@Param("roomId") String roomId);


    /**
     * 通过设备搜符合的roomId
     * @param roomScale
     * @return
     */
    @Select({
        "SELECT room_id",
                "FROM meeting_room",
                "WHERE room_scale = #{roomScale}"
    })
    @Results({
        @Result(column = "room_id",property = "roomId",javaType = String.class,
        many=@Many(select = "com.walking.meeting.mapper.RoomDeviceMapper.searchDeviceByRoomId"))
    })
    List<RoomDeviceSearchResultDTO> searchRoomIdByDevice(@Param("roomScale") Integer roomScale);
}