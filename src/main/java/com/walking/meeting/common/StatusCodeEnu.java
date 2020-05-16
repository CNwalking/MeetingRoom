package com.walking.meeting.common;

public enum StatusCodeEnu {
    SYSTEM_ERROR(new StatusCode(-1, "System Busy")),
    USERNAME_NOT_EXIST(new StatusCode(43001, "Username not exist, please register")),
    USERNAME_EXIST(new StatusCode(43002, "Username already exist")),
    USERNAME_OR_PSWD_ERROR(new StatusCode(43003, "Username or password ERROR!")),
    PORTION_PARAMS_NULL_ERROR(new StatusCode(43004, "Some params ERROR!")),
    MEETING_TIME_ERROR(new StatusCode(43005, "Meeting Time Error")),
    ROOM_EXIST(new StatusCode(43006, "Room already exist")),
    MEETING_ID_NOT_EXIST(new StatusCode(43007, "MeetingId do not exist")),
    MEETING_TIME_TOO_EARLY(new StatusCode(43008, "The meeting start time cannot be earlier than the meeting room free start time")),
    MEETING_TIME_TOO_LATE(new StatusCode(43009, "The meeting start time cannot be later than the meeting room free end time")),
    MEETING_ROOM_FULL(new StatusCode(43010, "The meeting room was fully booked that day")),
    MEETING_TIME_ILLEGAL(new StatusCode(43011, "This time cannot be booked")),
    MEETING_PARAMETER_ERROR(new StatusCode(43012, "The scheduled meeting parameters are incomplete")),
    MEETING_ROOM_NOT_EXIST(new StatusCode(43013, "Room not exist")),
    LEVEL_TOO_HIGH(new StatusCode(43014, "Level too high")),
    NO_SUCH_DEVICE(new StatusCode(43015, "No such device")),
    DEVICE_ALREADY_EXIST(new StatusCode(43016, "Device already exist")),
    DEPARTMENT_ALREADY_EXIST(new StatusCode(43017, "Department already exist")),
    DEPARTMENT_NOT_EXIST(new StatusCode(43018, "Department do not exist")),
    NO_RIGHT(new StatusCode(43019, "You have no Rights")),
    USER_ROLE_ERROR(new StatusCode(43020, "Please choose true role")),
    TWO_PSWD_NOT_SAME(new StatusCode(43021, "Two new passwords are inconsistent")),
    TWO_PSWD_SAME(new StatusCode(43022, "New and old passwords can't be the same")),
    QUESTION_NOT_RIGHT(new StatusCode(43023, "Secret security question is wrong")),
    ANSWER_NOT_RIGHT(new StatusCode(43024, "The answer to the secret security question is wrong")),
    NOT_MANAGER(new StatusCode(43025, "The operation only can be performed by an administrator")),
    DEVICE_ID_CANNOT_REPEAT(new StatusCode(43026, "Device ID cannot be repeated")),
    DEVICE_TYPE_CANNOT_REPEAT(new StatusCode(43027, "Device Type cannot be repeated")),
    TOKEN_ERROR(new StatusCode(43028, "Token ERROR!")),
    DEFAULT_SUCCESS(new StatusCode(0, "Request Success"));

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
