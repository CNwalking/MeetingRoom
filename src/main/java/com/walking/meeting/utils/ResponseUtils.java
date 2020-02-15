package com.walking.meeting.utils;

import com.walking.meeting.common.Response;

public class ResponseUtils {
    public static <T> Response<T> returnSuccess(T data) {
        Response<T> response = new Response<>();
        response.setMessage("成功");
        response.setData(data);
        return response;
    }

    public static Response returnDefaultSuccess() {
        Response response = new Response();
        response.setMessage("操作成功");
        return response;
    }


    public static Response returnMsgSuccess(String msg) {
        Response response = new Response();
        response.setMessage(msg);
        return response;
    }

    public static <T> Response<T> returnInfo(int code, String message,T t) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(t);
        return response;
    }

    public static Response returnError(int code, String message) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

//    public static Response returnError(ReturnCode returnCode) {
//        Response response = new Response();
//        response.setCode(returnCode.getCode());
//        response.setMessage(returnCode.getDesc());
//        return response;
//    }

    public static Response returnDefaultError() {
        Response response = new Response();
        response.setCode(407);
        response.setMessage("系统内部异常");
        return response;
    }

    public static Response returnOperationError() {
        Response response = new Response();
        response.setCode(408);
        response.setMessage("插入或者更新失败");
        return response;
    }

    public static Response returnGetValueError() {
        Response response = new Response();
        response.setCode(409);
        response.setMessage("获取数据失败");
        return response;
    }

    public static Response returnTokenError() {
        Response response = new Response();
        response.setCode(400);
        response.setMessage("Token异常");
        return response;
    }

    public static <T> Response<T> returnError(int code, String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
