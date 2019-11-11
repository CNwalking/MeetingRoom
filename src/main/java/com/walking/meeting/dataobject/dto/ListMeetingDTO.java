package com.walking.meeting.dataobject.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ListMeetingDTO {
    /**
     * 按定会议的人来搜
     */
    private String username;

    /**
     * 按定会议室id
     */
    private String roomId;

    /**
     * 按定会议的部门来搜
     */
    private String departmentName;

    /**
     * 按定会议的日期来搜
     */
    private Date bookingDate;

    /**
     * 按会议时长来搜
     */
    private BigDecimal requiredTime;

    /**
     * 按会议等级搜 0面试1例会2高级3紧急
     */
    private Integer meetingLevel;

    /**
     * 按会议室可容纳人数搜
     */
    private Short roomScale;

    /**
     * 按中文房间名搜
     */
    private String roomName;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页数量")
    private Integer pageSize;

}
