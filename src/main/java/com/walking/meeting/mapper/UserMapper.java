package com.walking.meeting.mapper;

import java.util.List;

import com.walking.meeting.dataobject.dao.UserDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserMapper extends Mapper<UserDO> {
}