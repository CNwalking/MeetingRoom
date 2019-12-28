package com.walking.meeting.common;

public enum StatusCodeEnu {
    SYSTEM_ERROR(new StatusCode(-1, "系统繁忙")),
    USERNAME_NOT_EXIST(new StatusCode(43001, "用户名不存在，请注册哦")),
    USERNAME_EXIST(new StatusCode(43002, "用户名已存在啦")),
    USERNAME_OR_PSWD_ERROR(new StatusCode(43003, "用户名或密码错误哦")),
    PORTION_PARAMS_NULL_ERROR(new StatusCode(43004, "部分参数为空哦")),
    MEETING_TIME_ERROR(new StatusCode(43005, "会议时间参数问题")),
    ROOM_EXIST(new StatusCode(43006, "房间名或房间id已经存在咯")),
    MEETING_ID_NOT_EXIST(new StatusCode(43007, "会议id不存在哦")),
    MEETING_TIME_TOO_EARLY(new StatusCode(43008, "会议开始时间不能早于会议室能预定的时间哦")),
    MEETING_TIME_TOO_LATE(new StatusCode(43009, "会议结束时间不能晚于会议室能预定的时间哦")),
    MEETING_ROOM_FULL(new StatusCode(43010, "该会议室当天已被订满咯")),
    MEETING_TIME_ILLEGAL(new StatusCode(43011, "这个时间不能被预定哦")),
    MEETING_PARAMETER_ERROR(new StatusCode(43012, "预定会议参数不全哦")),
    MEETING_ROOM_NOT_EXIST(new StatusCode(43013, "会议室不存在哦")),
    LEVEL_TOO_HIGH(new StatusCode(43014, "等级设置的太高啦")),
    NO_SUCH_DEVICE(new StatusCode(43015, "没有这种设备哦")),
    DEVICE_ALREADY_EXIST(new StatusCode(43016, "该设备已经存在了哦")),
    DEPARTMENT_ALREADY_EXIST(new StatusCode(43017, "该部门已经存在了哦")),






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
