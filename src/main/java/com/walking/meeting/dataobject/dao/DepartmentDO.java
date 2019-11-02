/** 
 * DepartmentDO.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:14 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "department")
public class DepartmentDO implements Serializable {
    /**
     * 数据库表字段注释 : 部门表id
     *
     * 数据库表字段名称 : department.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 部门名字(不可重复）
     *
     * 数据库表字段名称 : department.department_name
     */
    public static final String PROP_DEPARTMENT_NAME="departmentName";
    private String departmentName;

    /**
     * 数据库表字段注释 : 部门等级，123，最高3
     *
     * 数据库表字段名称 : department.department_level
     */
    public static final String PROP_DEPARTMENT_LEVEL="departmentLevel";
    private Integer departmentLevel;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : department.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : department.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : department.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private Date deleteTime;

    private static final long serialVersionUID = 1L;
}