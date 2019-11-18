/** 
 * MeetingMapper.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-07 09:16:59 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.MeetingDO;
import com.walking.meeting.dataobject.dto.MeetingDTO;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MeetingMapper extends Mapper<MeetingDO> {
    @Select({
            "SELECT" ,
            "booking_start_time, booking_end_time" ,
            "FROM meeting" ,
            "WHERE DATE_FORMAT(booking_date,'%Y%m%d') = #{date}",
            "AND room_id = '#{room_id}'",
            "ORDER BY booking_start_time asc"
    })
    @Results({
            @Result(column = "room_id", property = "roomId"),
            @Result(column = "booking_start_time", property = "name"),
            @Result(column = "booking_end_time", property = "phone")
    })
    List<MeetingDTO> selectTimeByDateAndRoomID(@Param("date") String date,@Param("room_id") String roomId);
}