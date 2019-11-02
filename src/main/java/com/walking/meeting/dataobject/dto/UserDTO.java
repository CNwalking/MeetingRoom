/** 
 * UserDTO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:57:49 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.util.Date;

import com.walking.meeting.common.IdEncryption;
import lombok.Data;

@Data
public class UserDTO {
    /**
     * 用户表id
     */
    @IdEncryption
    private String id;

    /**
     * 用户名（不可重复）
     */
    private String username;

    /**
     * 用户密码，MD5加密
     */
    private String password;

    /**
     * 部门名字（不可重复）
     */
    private String departmentName;

    private String email;

    /**
     * 找回密码问题
     */
    private String question;

    /**
     * 找回密码答案
     */
    private String answer;

    /**
     * 角色0-管理员,1-普通用户
     */
    private Integer role;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后一次更新时间
     */
    private Date updateTime;

    /**
     * 有值则已经被删除
     */
    private Date deleteTime;
}