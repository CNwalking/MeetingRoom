package com.walking.meeting.common;

public class ResponseException extends RuntimeException{
    private StatusCode statusCode;

    public ResponseException(StatusCodeEnu statusCodeEnu) {
        super(statusCodeEnu.getStatusCode().toString());
        this.statusCode = statusCodeEnu.getStatusCode();
    }

    public ResponseException(StatusCode statusCode) {
        super(statusCode.toString());
        this.statusCode = statusCode;
    }

    private ResponseException() {
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
