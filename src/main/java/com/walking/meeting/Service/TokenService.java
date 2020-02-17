package com.walking.meeting.Service;

import com.walking.meeting.dataobject.dao.UserDO;

public interface TokenService {

    public String generateToken(String username);

    public void save(String token, String username);

    public String getUsername(String token);

    public void delToken(String token);
}
