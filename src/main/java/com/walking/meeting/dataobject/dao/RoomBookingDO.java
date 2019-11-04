/** 
 * RoomBookingDO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:55:57 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "room_booking")
public class RoomBookingDO implements Serializable {
    /**
     * 数据库表字段注释 : 用户表id
     *
     * 数据库表字段名称 : room_booking.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 房间id（不可重复）
     *
     * 数据库表字段名称 : room_booking.room_id
     */
    public static final String PROP_ROOM_ID="roomId";
    private String roomId;

    /**
     * 数据库表字段注释 : 定会议室事件
     *
     * 数据库表字段名称 : room_booking.date
     */
    public static final String PROP_DATE="date";
    private String date;

    /**
     * 数据库表字段注释 : 定会议室起始时间
     *
     * 数据库表字段名称 : room_booking.start_time
     */
    public static final String PROP_START_TIME="startTime";
    private Date startTime;

    /**
     * 数据库表字段注释 : 定会议室起始时间
     *
     * 数据库表字段名称 : room_booking.end_time
     */
    public static final String PROP_END_TIME="endTime";
    private Date endTime;

    /**
     * 数据库表字段注释 : 会议预订者
     *
     * 数据库表字段名称 : room_booking.username
     */
    public static final String PROP_USERNAME="username";
    private String username;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : room_booking.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : room_booking.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : room_booking.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private String deleteTime;

    private static final long serialVersionUID = 1L;
}