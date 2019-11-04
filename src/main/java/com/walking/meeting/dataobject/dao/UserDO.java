/** 
 * UserDO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:57:49 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "user")
public class UserDO implements Serializable {
    /**
     * 数据库表字段注释 : 用户表id
     *
     * 数据库表字段名称 : user.id
     */
    public static final String PROP_ID="id";
    @javax.persistence.Id
    private Integer id;

    /**
     * 数据库表字段注释 : 用户名（不可重复）
     *
     * 数据库表字段名称 : user.username
     */
    public static final String PROP_USERNAME="username";
    private String username;

    /**
     * 数据库表字段注释 : 用户密码，MD5加密
     *
     * 数据库表字段名称 : user.password
     */
    public static final String PROP_PASSWORD="password";
    private String password;

    /**
     * 数据库表字段注释 : 部门名字（不可重复）
     *
     * 数据库表字段名称 : user.department_name
     */
    public static final String PROP_DEPARTMENT_NAME="departmentName";
    private String departmentName;

    /**
     *
     * 数据库表字段名称 : user.email
     */
    public static final String PROP_EMAIL="email";
    private String email;

    /**
     * 数据库表字段注释 : 找回密码问题
     *
     * 数据库表字段名称 : user.question
     */
    public static final String PROP_QUESTION="question";
    private String question;

    /**
     * 数据库表字段注释 : 找回密码答案
     *
     * 数据库表字段名称 : user.answer
     */
    public static final String PROP_ANSWER="answer";
    private String answer;

    /**
     * 数据库表字段注释 : 角色0-管理员,1-普通用户
     *
     * 数据库表字段名称 : user.role
     */
    public static final String PROP_ROLE="role";
    private Integer role;

    /**
     * 数据库表字段注释 : 创建时间
     *
     * 数据库表字段名称 : user.create_time
     */
    public static final String PROP_CREATE_TIME="createTime";
    private Date createTime;

    /**
     * 数据库表字段注释 : 最后一次更新时间
     *
     * 数据库表字段名称 : user.update_time
     */
    public static final String PROP_UPDATE_TIME="updateTime";
    private Date updateTime;

    /**
     * 数据库表字段注释 : 有值则已经被删除
     *
     * 数据库表字段名称 : user.delete_time
     */
    public static final String PROP_DELETE_TIME="deleteTime";
    private String deleteTime;

    private static final long serialVersionUID = 1L;
}