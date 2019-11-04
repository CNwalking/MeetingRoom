/** 
 * DeviceDO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:10 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "device")
public class DeviceDO implements Serializable {
    /**
     * 数据库表字段注释 : 设备表id
     *
     * 数据库表字段名称 : device.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 设备类型名称
     *
     * 数据库表字段名称 : device.device_type
     */
    public static final String PROP_DEVICE_TYPE="deviceType";
    private String deviceType;

    /**
     * 数据库表字段注释 : 设备id,1投影仪,2电插头,3遥控器,4黑板,5HDMI接口
     *
     * 数据库表字段名称 : device.device_id
     */
    public static final String PROP_DEVICE_ID="deviceId";
    private Integer deviceId;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : device.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : device.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : device.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private String deleteTime;

    private static final long serialVersionUID = 1L;
}