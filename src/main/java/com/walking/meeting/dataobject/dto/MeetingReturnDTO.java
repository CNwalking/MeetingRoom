package com.walking.meeting.dataobject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MeetingReturnDTO {
    /**
     * 数据库表字段注释 : 会议名称
     *
     * 数据库表字段名称 : meeting.meeting_name
     */
    private String meetingName;

    /**
     * 数据库表字段注释 : 会议等级0面试1例会2高级3紧急
     *
     * 数据库表字段名称 : meeting.meeting_level
     */
    private Integer meetingLevel;



    /**
     * 数据库表字段注释 : 会议室id
     *
     * 数据库表字段名称 : meeting.room_id
     */
    private String roomId;

    /**
     * 数据库表字段注释 : 会议预定者
     *
     * 数据库表字段名称 : meeting.username
     */
    private String username;

    /**
     * 数据库表字段注释 : 会议预定日期
     *
     * 数据库表字段名称 : meeting.booking_date
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date bookingDate;

    /**
     * 数据库表字段注释 : 定会议室起始时间
     *
     * 数据库表字段名称 : meeting.booking_start_time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bookingStartTime;

    /**
     * 数据库表字段注释 : 定会议室结束时间
     *
     * 数据库表字段名称 : meeting.booking_end_time
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bookingEndTime;

    /**
     * 数据库表字段注释 : 会议时长
     *
     * 数据库表字段名称 : meeting.required_time
     */
    private BigDecimal requiredTime;

    /**
     * 数据库表字段注释 : 预定的部门名
     *
     * 数据库表字段名称 : meeting.department_name
     */
    private String departmentName;

    /**
     * 数据库表字段注释 : 全局meeting
     *
     * 数据库表字段名称 : meeting.meeting_id
     */
    private String meetingId;

}
