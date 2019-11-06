package com.walking.meeting.dataobject.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ListMeetingDTO {
    /**
     * 按定会议的人来搜
     */
    private String username;

    /**
     * 按定会议的部门来搜
     */
    private String departmentName;

    /**
     * 按定会议的日期来搜
     */
    private Date bookingDate;

}
