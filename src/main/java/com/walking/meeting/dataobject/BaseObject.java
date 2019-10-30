package com.walking.meeting.dataobject;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class BaseObject {
    public static final Long DEFAULT_ID = -1L;
    public static final String VAL_NOT_DELETED = "";
    public static final String PROP_CREATE_TIME = "createTime";
    private Date createTime;
    public static final String PROP_UPDATE_TIME = "updateTime";
    private Date updateTime;
    public static final String PROP_DELETE_TIME = "deleteTime";
    private String deleteTime;

    public BaseObject() {
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeleteTime() {
        return this.deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}