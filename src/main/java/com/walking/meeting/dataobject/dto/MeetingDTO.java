/** 
 * MeetingDTO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-04 16:38:07 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.util.Date;

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