package com.walking.meeting.dataobject.dto;

import lombok.Data;

@Data
public class RoomDeviceSearchResultDTO {
    /**
     * 房间id
     */
    private String roomId;
    /**
     * 返回的设备id，例如1,2,3,4
     */
    private String deviceIdList;
}
