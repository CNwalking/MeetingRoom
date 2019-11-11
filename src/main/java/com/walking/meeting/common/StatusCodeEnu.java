package com.walking.meeting.common;

public enum StatusCodeEnu {
    SYSTEM_ERROR(new StatusCode(-1, "系统繁忙")),
    USERNAME_NOT_EXIST(new StatusCode(43001, "用户名不存在，请注册")),
    USERNAME_EXIST(new StatusCode(43002, "用户名已存在")),
    USERNAME_OR_PSWD_ERROR(new StatusCode(43003, "用户名或密码错误")),
    PORTION_PARAMS_NULL_ERROR(new StatusCode(43004, "部分参数为空")),
    MEETING_TIME_ERROR(new StatusCode(43005, "会议时间参数问题")),
    ROOM_EXIST(new StatusCode(43006, "房间名或房间id已经存在")),
    MEETING_ID_NOT_EXIST(new StatusCode(43007, "会议id不存在")),


    DEFAULT_SUCCESS(new StatusCode(0, "请求成功"));

    private StatusCode statusCode;

    private StatusCodeEnu(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getCode() {
        return this.statusCode.getCode();
    }

    public String getDescribe() {
        return this.statusCode.getDescribe();
    }

    @Override
    public String toString() {
        return this.statusCode.toString();
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }
}
