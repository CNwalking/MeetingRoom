/** 
 * MeetingRoomDTO.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:01 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import cn.ucmed.yilian.common.utils.annotation.IdEncryption;
import java.util.Date;
import lombok.Data;

@Data
public class MeetingRoomDTO {
    /**
     * 用户表id
     */
    @IdEncryption
    private String id;

    /**
     * 房间id（不可重复）
     */
    private String roomId;

    /**
     * 会议室可容纳人数
     */
    private Short scale;

    /**
     * 中文房间名
     */
    private String roomName;

    /**
     * 开放时间开始
     */
    private Date freeTimeStart;

    /**
     * 开放时间结束
     */
    private Date freeTimeEnd;

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