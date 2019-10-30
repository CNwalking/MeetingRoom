package com.walking.meeting.common;

public enum StatusCodeEnu {
    SYSTEM_ERROR(new StatusCode(-1, "系统繁忙")),
    USERNAME_NOT_EXIT(new StatusCode(43001, "用户名不存在，请注册")),
    PORTION_PARAMS_NULL_ERROR(new StatusCode(43003, "部分参数为空")),
    USER_ID_NULL_ERROR(new StatusCode(43004, "userId不能为空")),
    DEFAULT_SUCCESS(new StatusCode(0, "请求成功")),
    PARAMS_VALID_FAIL(new StatusCode(40006, "入参校验失败"));

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
