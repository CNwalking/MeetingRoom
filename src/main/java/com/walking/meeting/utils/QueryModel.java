package com.walking.meeting.utils;

import com.github.pagehelper.PageHelper;

import java.io.Serializable;

public class QueryModel<T extends QueryModel> implements Serializable {
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    private static final long serialVersionUID = 1L;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
    private String orderBy;
    private Boolean isAsc = true;
    private Boolean countSql = true;
    private Boolean pageSizeZero = true;
    private Boolean reasonable;
    private String deleteTime = "";
    private String[] properties;

    public QueryModel() {
    }

    public T setPageNumAndSize(Integer pageNumParam, Integer pageSizeParam) {
        if (null != pageNumParam && pageNumParam > 0) {
            this.pageNum = pageNumParam;
        } else {
            this.pageNum = 1;
        }

        if (null != pageSizeParam && pageSizeParam >= 0) {
            this.pageSize = pageSizeParam;
        } else {
            this.pageSize = 20;
        }

        return (T) this;
    }

    public T checkPageNumAndSize() {
        if (null == this.pageNum || this.pageNum <= 0) {
            this.pageNum = 1;
        }

        if (null == this.pageSize || this.pageSize < 0) {
            this.pageSize = 20;
        }

        return (T) this;
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public T setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return (T) this;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public T setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return (T) this;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public T setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return (T) this;
    }

    public Boolean isAsc() {
        return this.isAsc;
    }

    public T setAsc(Boolean isAsc) {
        this.isAsc = isAsc;
        return (T) this;
    }

    public Boolean getCountSql() {
        return this.countSql;
    }

    public T setCountSql(Boolean countSql) {
        this.countSql = countSql;
        return (T) this;
    }

    public Boolean getPageSizeZero() {
        return this.pageSizeZero;
    }

    public T setPageSizeZero(Boolean pageSizeZero) {
        this.pageSizeZero = pageSizeZero;
        return (T) this;
    }

    public Boolean getReasonable() {
        return this.reasonable;
    }

    public T setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
        return (T) this;
    }

    public String getDeleteTime() {
        return this.deleteTime;
    }

    public T setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
        return (T) this;
    }

    public String[] getProperties() {
        return this.properties;
    }

    public T setProperties(String... properties) {
        this.properties = properties;
        return (T) this;
    }

    public T selectAll() {
        this.pageSize = 0;
        return (T) this;
    }

    public void startPage() {
        PageHelper.startPage(this.getPageNum(), this.getPageSize(), this.getCountSql(), this.getReasonable(), this.getPageSizeZero());
    }
}
