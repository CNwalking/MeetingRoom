package com.walking.meeting.common;

public class SuccessResponse {
        private Integer code;
        private String msg;

        private SuccessResponse() {
        }

        private SuccessResponse(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public static SuccessResponse defaultSuccess() {
            return getByStatusCodeEnu(StatusCodeEnu.DEFAULT_SUCCESS);
        }

        public static SuccessResponse getByStatusCodeEnu(StatusCodeEnu statusCodeEnu) {
            return statusCodeEnu == null ? defaultSuccess() : new SuccessResponse(statusCodeEnu.getCode(), statusCodeEnu.getDescribe());
        }

        public Integer getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
}
