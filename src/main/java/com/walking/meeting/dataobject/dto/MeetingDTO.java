/** 
 * MeetingDTO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-07 09:16:59 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.walking.meeting.common.IdEncryption;
import lombok.Data;

@Data
public class MeetingDTO {
    /**
     * 会议id
     */
    @IdEncryption
    private String id;

    /**
     * 会议名称
     */
    private String meetingName;

    /**
     * 会议等级0面试1例会2高级3紧急
     */
    private Integer meetingLevel;

    /**
     * 全局meeting
     */
    private String meetingId;

    /**
     * 会议室id
     */
    private String roomId;

    /**
     * 会议预定者
     */
    private String username;

    /**
     * 会议预定日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date bookingDate;

    /**
     * 定会议室起始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bookingStartTime;

    /**
     * 定会议室结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bookingEndTime;

    /**
     * 会议时长
     */
    private BigDecimal requiredTime;

    /**
     * 预定的部门名
     */
    private String departmentName;

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
    private String deleteTime;
}