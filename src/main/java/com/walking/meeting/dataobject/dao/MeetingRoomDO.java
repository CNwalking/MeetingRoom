/** 
 * MeetingRoomDO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-04 16:39:20 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "meeting_room")
public class MeetingRoomDO implements Serializable {
    /**
     * 数据库表字段注释 : 用户表id
     *
     * 数据库表字段名称 : meeting_room.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 房间id（不可重复）
     *
     * 数据库表字段名称 : meeting_room.room_id
     */
    public static final String PROP_ROOM_ID="roomId";
    private String roomId;

    /**
     * 数据库表字段注释 : 会议室可容纳人数
     *
     * 数据库表字段名称 : meeting_room.room_scale
     */
    public static final String PROP_ROOM_SCALE="roomScale";
    private Short roomScale;

    /**
     * 数据库表字段注释 : 中文房间名
     *
     * 数据库表字段名称 : meeting_room.room_name
     */
    public static final String PROP_ROOM_NAME="roomName";
    private String roomName;

    /**
     * 数据库表字段注释 : 开放时间开始
     *
     * 数据库表字段名称 : meeting_room.free_time_start
     */
    public static final String PROP_FREE_TIME_START="freeTimeStart";
    private Date freeTimeStart;

    /**
     * 数据库表字段注释 : 开放时间结束
     *
     * 数据库表字段名称 : meeting_room.free_time_end
     */
    public static final String PROP_FREE_TIME_END="freeTimeEnd";
    private Date freeTimeEnd;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : meeting_room.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : meeting_room.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : meeting_room.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private String deleteTime;

    private static final long serialVersionUID = 1L;
}