package com.walking.meeting.Service;


import com.walking.meeting.dataobject.dao.UserDO;
import com.walking.meeting.dataobject.dto.UserDTO;
import com.walking.meeting.dataobject.query.UserQuery;

public interface UserService {
    UserDO getUserByUserQuery(UserQuery userQuery);

    void addUser(UserDTO userDTO);

    void updateUserSelective(UserDO userDO);
}
