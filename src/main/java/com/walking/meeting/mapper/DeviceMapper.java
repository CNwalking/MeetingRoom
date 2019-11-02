/** 
 * DeviceMapper.Java
 * <p>
 * Copyright(C) 2017-2019 Hangzhou Zhuo Jian Mdt InfoTech Ltd
 * </p>
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:56:10 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.DeviceDO;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface DeviceMapper extends Mapper<DeviceDO> {
}