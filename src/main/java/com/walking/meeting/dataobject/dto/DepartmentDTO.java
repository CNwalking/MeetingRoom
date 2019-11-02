/** 
 * DepartmentDTO.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:14 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.util.Date;

import com.walking.meeting.common.IdEncryption;
import lombok.Data;

@Data
public class DepartmentDTO {
    /**
     * 部门表id
     */
    @IdEncryption
    private String id;

    /**
     * 部门名字(不可重复）
     */
    private String departmentName;

    /**
     * 部门等级，123，最高3
     */
    private Integer departmentLevel;

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