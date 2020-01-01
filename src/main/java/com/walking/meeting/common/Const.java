package com.walking.meeting.common;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String TOKEN_PREFIX = "token_";
    public static final String ROOM_FULL_TIME = "time_full";
    public static final String ROOM_CROWDED = "time_crowded";
    public static final String ROOM_JUST_SOSO = "time_just_soso";
    public static final String ROOM_AVAILABLE = "time_free";

    public interface RedisCacheExtime{
        int REDIS_SESSION_EXTIME = 60 *30;
    }
}
