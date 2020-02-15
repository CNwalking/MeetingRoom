package com.walking.meeting.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.walking.meeting.dataobject.dao.UserDO;
import java.util.Date;

public class JwtUtils {
    /**
     * 过期时间一天，
     */
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    /**
     * token私钥,未使用
     */
    private static final String TOKEN_SECRET = "cnwalking_cjlu_aut_good";

    /**
     * 校验token是否正确
     *
     * @param token 密钥
     * @return 是否正确
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


    public static String getToken(UserDO user) {
        // expire time
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        String token="";
        token= JWT.create().withAudience(user.getUsername()).withExpiresAt(date)
                .sign(Algorithm.HMAC256(user.getPswd()));
        return token;
    }




}