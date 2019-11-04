package com.walking.meeting.Service.impl;

import com.walking.meeting.Service.UserService;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;
import com.walking.meeting.mapper.UserMapper;
import com.walking.meeting.utils.DbUtils;
import org.apache.catalina.User;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Builder;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import tk.mybatis.mapper.util.Sqls;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDO getUserByUserQuery(UserQuery userQuery) {
//        Example example = new Example(UserDO.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo(UserDO.PROP_DELETE_TIME, "");
//        criteria.andEqualTo(UserDO.PROP_USERNAME, userQuery.getUserName());
//        criteria.andEqualTo(UserDO.PROP_EMAIL, userQuery.getEmail());
//        criteria.andEqualTo(UserDO.PROP_DEPARTMENT_NAME, userQuery.getDepartmentName());
//
//        List<UserDO> list=userMapper.selectByExample(example);
//        return DbUtils.getOne(list).orElse(null);
        Example.Builder builder = DbUtils.newExampleBuilder(UserDO.class);
        DbUtils.setEqualToProp(builder, UserDO.PROP_USERNAME, userQuery.getUserName());
        DbUtils.setEqualToProp(builder, UserDO.PROP_DEPARTMENT_NAME, userQuery.getDepartmentName());
        DbUtils.setEqualToProp(builder, UserDO.PROP_EMAIL, userQuery.getEmail());
        List<UserDO> userDOList = userMapper.selectByExample(builder.build());
        return DbUtils.getOne(userDOList).orElse(null);
    }

    @Override
    public void addUser(UserDTO userDTO) {
        UserDO userDO =  JSON.parseObject(JSON.toJSONString(userDTO), UserDO.class);
        userDO.setRoleId(1); // 默认普通用户，0为管理员
        userDO.setCreateTime(new Date());
        userMapper.insertSelective(userDO);
    }

    @Override
    public void updateUserSelective(UserDO userDO) {
        if(Objects.isNull(userDO.getUsername())){
            return;
        }
        // 只更新有效字段
//        Example example = new Example(UserDO.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo(UserDO.PROP_DELETE_TIME, "");
//        criteria.andEqualTo(UserDO.PROP_USERNAME, userDO.getUsername());
//        userMapper.updateByExampleSelective(userDO,example);
        Example.Builder builder = DbUtils.newExampleBuilder(UserDO.class);
        DbUtils.setEqualToProp(builder, UserDO.PROP_USERNAME, userDO.getUsername());
        userDO.setUpdateTime(new Date());
        userMapper.updateByExampleSelective(userDO,builder.build());
    }


}
