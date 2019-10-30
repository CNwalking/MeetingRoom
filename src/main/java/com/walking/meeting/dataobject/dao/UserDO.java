package com.walking.meeting.dataobject.dao;

import com.walking.meeting.dataobject.BaseObject;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * user_test
 * @author 
 */
@Data
public class UserDO extends BaseObject implements Serializable {
    /**
     * 数据库表字段注释 : 主键
     *
     * 数据库表字段名称 : user.id
     */
    public static final String PROP_ID="id";
    @Id
    private Integer id;

    /**
     * 数据库表字段注释 : 用户唯一编码
     *
     * 数据库表字段名称 : user.user_id
     */
    public static final String PROP_USER_ID="userId";
    private Long userId;

    /**
     * 数据库表字段注释 : 登录用户名
     *
     * 数据库表字段名称 : user.user_name
     */
    public static final String PROP_USER_NAME="userName";
    private String username;

    /**
     * 数据库表字段注释 : MD5加密密码
     *
     * 数据库表字段名称 : user.password
     */
    public static final String PROP_MD5_PWD="password";
    private String password;

    /**
     * 数据库表字段注释 : 用户E_Mail
     *
     * 数据库表字段名称 : user.email
     */
    public static final String PROP_E_MAIL="email";
    private String email;

    /**
     * 找回密码问题
     */
    public static final String PROP_QUESTION="question";
    private String question;

    /**
     * 找回密码答案
     */
    public static final String PROP_ANSWER="answer";
    private String answer;

    /**
     * 角色0-管理员,1-普通用户
     */
    public static final String PROP_ROLE="role";
    private Integer role;

    private static final long serialVersionUID = 1L;


}