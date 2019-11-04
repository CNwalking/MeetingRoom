/** 
 * RoomBookingDTO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-04 16:40:22 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.util.Date;

import com.walking.meeting.common.IdEncryption;
import lombok.Data;

@Data
public class RoomBookingDTO {
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
     * 定会议室日期
     */
    private Date bookingDate;

    /**
     * 定会议室起始时间
     */
    private Date startTime;

    /**
     * 定会议室起始时间
     */
    private Date endTime;

    /**
     * 所预定的会议
     */
    private String meetingId;

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