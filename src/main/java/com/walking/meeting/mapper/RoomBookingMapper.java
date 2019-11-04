/** 
 * RoomBookingMapper.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-04 16:40:22 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.RoomBookingDO;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface RoomBookingMapper extends Mapper<RoomBookingDO> {
}