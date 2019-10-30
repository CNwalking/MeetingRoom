package com.walking.meeting.Service.impl;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.mapper.UserMapper;
import com.walking.meeting.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean checkUserNameExist(String userName) {
        Example.Builder builder = DbUtils.newExampleBuilder(UserDO.class);
        DbUtils.setEqualToProp(builder, UserDO.PROP_USER_ID, userName);
        List<UserDO> userDOList = userMapper.selectByExample(builder);
        // 用户名没有，返回true；有则重复，返回false
        if (userDOList.isEmpty()){
            return true;
        } else{
            return false;
        }
    }
}
