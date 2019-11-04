/** 
 * MeetingDO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:06 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "meeting")
public class MeetingDO implements Serializable {
    /**
     * 数据库表字段注释 : 会议id
     *
     * 数据库表字段名称 : meeting.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 会议名称
     *
     * 数据库表字段名称 : meeting.meeting_name
     */
    public static final String PROP_MEETING_NAME="meetingName";
    private String meetingName;

    /**
     * 数据库表字段注释 : 会议等级0面试1例会2高级3紧急
     *
     * 数据库表字段名称 : meeting.meeting_level
     */
    public static final String PROP_MEETING_LEVEL="meetingLevel";
    private Integer meetingLevel;

    /**
     * 数据库表字段注释 : 会议室id
     *
     * 数据库表字段名称 : meeting.room_id
     */
    public static final String PROP_ROOM_ID="roomId";
    private String roomId;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : meeting.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : meeting.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : meeting.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private String deleteTime;

    private static final long serialVersionUID = 1L;
}