package com.walking.meeting.utils;

import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.util.List;
import java.util.Optional;

public abstract class DbUtils {
    public static Example.Builder newExampleBuilder(Class<?> clazz) {
        return Example.builder(clazz).andWhere(Sqls.custom().andEqualTo("deleteTime", ""));
    }

    public static Example.Builder newExampleBuilder(Class<?> clazz, QueryModel queryModel) {
        Example.Builder builder = Example.builder(clazz);
        builder.select(queryModel.getProperties());
        builder.andWhere(Sqls.custom().andEqualTo("deleteTime", queryModel.getDeleteTime()));
        if (StringUtils.isNotBlank(queryModel.getOrderBy())) {
            if (queryModel.isAsc()) {
                builder.orderByAsc(new String[]{queryModel.getOrderBy()});
            } else {
                builder.orderByDesc(new String[]{queryModel.getOrderBy()});
            }
        }
        return builder;
    }
    public static Example.Builder newExampleBuilder(Class<?> clazz, String property, Object value) {
        return !StringUtils.isEmpty(property) && null != value ? Example.builder(clazz).andWhere(Sqls.custom().andEqualTo(property, value)) : newExampleBuilder(clazz);
    }

    public static Example.Builder newExampleBuilder(Class<?> clazz, QueryModel queryModel, String property, Object value) {
        if (!StringUtils.isEmpty(property) && null != value) {
            Example.Builder builder = Example.builder(clazz);
            builder.select(queryModel.getProperties());
            builder.andWhere(Sqls.custom().andEqualTo(property, value));
            if (StringUtils.isNotBlank(queryModel.getOrderBy())) {
                if (queryModel.isAsc()) {
                    builder.orderByAsc(new String[]{queryModel.getOrderBy()});
                } else {
                    builder.orderByDesc(new String[]{queryModel.getOrderBy()});
                }
            }

            return builder;
        } else {
            return newExampleBuilder(clazz, queryModel);
        }
    }


    public static <T> Optional<T> getOne(PageInfo<T> pageInfo) {
        return CollectionUtils.isEmpty(pageInfo.getList()) ? Optional.empty() : Optional.ofNullable(pageInfo.getList().get(0));
    }

    public static <T> Optional<T> getOne(List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    public static void setEqualToProp(Example.Builder builder, String property, Object value, boolean allowStrBlank) {
        if (null != value) {
            if (allowStrBlank || !(value instanceof String) || !StringUtils.isBlank((String)value)) {
                builder.andWhere(Sqls.custom().andEqualTo(property, value));
            }
        }
    }

    public static void setEqualToProp(Example.Builder builder, String property, Object value) {
        setEqualToProp(builder, property, value, false);
    }
}
