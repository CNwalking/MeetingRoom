package com.walking.meeting.common;

import com.alibaba.fastjson.JSON;

public class StatusCode {
    private Integer code;
    private String describe;

    private StatusCode() {
    }

    public StatusCode(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDescribe() {
        return this.describe;
    }
}