package com.walking.meeting.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.walking.meeting.Service.TokenService;
import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.utils.MD5Encrypt;
import com.walking.meeting.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    @Override
    public String generateToken(String username) {
        StringBuilder token = new StringBuilder();
        token.append(MD5Encrypt.md5Encrypt(username));
        return token.toString();
    }

    @Override
    public void save(String token, String username) {
        RedisUtils.setEx(token, username, 2*60*60);
    }

    @Override
    public String getUsername(String token) {
        return RedisUtils.get(token);
    }

    public void delToken(String token) {
        RedisUtils.del(token);
    }


}
