package com.walking.meeting.dataobject.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MeetingRoomVO {
    /**
     * 房间id（不可重复）
     */
    private String roomId;

    /**
     * 会议室可容纳人数
     */
    private Short roomScale;

    /**
     * 中文房间名
     */
    private String roomName;

    /**
     * 开放时间开始
     */
    private String freeTimeStart;

    /**
     * 开放时间结束
     */
    private String freeTimeEnd;
}