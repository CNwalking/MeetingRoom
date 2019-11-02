/** 
 * UserMapper.Java
 * All rights reserved
 * ------------------------------------------
 * @Date: 2019-11-02 16:57:49 Created
 * @Author: Technical team of walking
 * @ProjectName: meeting
 */
package com.walking.meeting.mapper;

import com.walking.meeting.dataobject.dao.UserDO;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface UserMapper extends Mapper<UserDO> {
}