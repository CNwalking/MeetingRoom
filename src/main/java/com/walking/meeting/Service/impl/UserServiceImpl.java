package com.walking.meeting.Service.impl;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.mapper.UserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDO getUserByUserQuery(UserQuery userQuery) {
        Example example = new Example(UserDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo( UserDO.PROP_USERNAME, userQuery.getUserName());
        criteria.andEqualTo( UserDO.PROP_DEPARTMENT_NAME, userQuery.getDepartmentName());
        criteria.andEqualTo( UserDO.PROP_EMAIL, userQuery.getEmail());
        List<UserDO> userDOList = userMapper.selectByExample(example);
        UserDO userDO = CollectionUtils.isEmpty(userDOList) ? null : userDOList.get(0);
        return userDO;
    }

    @Override
    public void addUser(UserDTO userDTO) {
        UserDO userDO =  JSON.parseObject(JSON.toJSONString(userDTO), UserDO.class);
        userDO.setRole(1); // 默认普通用户，0为管理员
        userDO.setCreateTime(new Date());
        userMapper.insertSelective(userDO);
    }


}
