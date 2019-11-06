/** 
 * MeetingMapper.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-05 19:59:12 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.MeetingDO;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface MeetingMapper extends Mapper<MeetingDO> {
}