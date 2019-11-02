/** 
 * MeetingDTO.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:06 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import cn.ucmed.yilian.common.utils.annotation.IdEncryption;
import java.util.Date;
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
     * 会议室id
     */
    private String roomId;

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