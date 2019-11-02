/** 
 * RoomDeviceDO.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:55:52 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "room_device")
public class RoomDeviceDO implements Serializable {
    /**
     * 数据库表字段注释 : 设备表id
     *
     * 数据库表字段名称 : room_device.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 房间id
     *
     * 数据库表字段名称 : room_device.room_id
     */
    public static final String PROP_ROOM_ID="roomId";
    private String roomId;

    /**
     * 数据库表字段注释 : 设备id,1投影仪,2电插头,3遥控器,4黑板,5HDMI接口
     *
     * 数据库表字段名称 : room_device.device_id
     */
    public static final String PROP_DEVICE_ID="deviceId";
    private Integer deviceId;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : room_device.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : room_device.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : room_device.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private Date deleteTime;

    private static final long serialVersionUID = 1L;
}