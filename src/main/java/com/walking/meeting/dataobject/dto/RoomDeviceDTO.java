/** 
 * RoomDeviceDTO.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:55:52 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.dataobject.dto;

import java.util.Date;

import com.walking.meeting.common.IdEncryption;
import lombok.Data;

@Data
public class RoomDeviceDTO {
    /**
     * 设备表id
     */
    @IdEncryption
    private String id;

    /**
     * 房间id
     */
    private String roomId;

    /**
     * 设备id,1投影仪,2电插头,3遥控器,4黑板,5HDMI接口
     */
    private Integer deviceId;

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