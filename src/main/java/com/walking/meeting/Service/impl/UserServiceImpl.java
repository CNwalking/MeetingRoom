package com.walking.meeting.Service.impl;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.mapper.UserMapper;
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
        Example example = new Example(UserDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo( UserDO.PROP_USERNAME, userName);
        List<UserDO> userDOList = userMapper.selectByExample(example);
        // 用户名没有，返回true；有则重复，返回false
        if (userDOList.isEmpty()){
            return true;
        } else{
            return false;
        }
    }
}
