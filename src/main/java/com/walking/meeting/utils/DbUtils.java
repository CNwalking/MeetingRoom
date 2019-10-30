package com.walking.meeting.utils;

import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import javax.persistence.Id;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class DbUtils {
    private static final String WILDCARD = "%";

    private DbUtils() {
    }

    public static void setPrimaryKeyValue(Object obj, Object primaryKey) {
        if (null != obj) {
            ReflectionUtils.doWithFields(obj.getClass(), (field) -> {
                if (field.isAnnotationPresent(Id.class)) {
                    if (!Modifier.isPublic(field.getModifiers()) || !field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    field.set(obj, primaryKey);
                }

            });
        }
    }

    public static Example.Criteria getCriteria(Example example) {
        return example.createCriteria().andEqualTo("deleteTime", "");
    }

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

    public static <T> Optional<T> getOne(PageInfo<T> pageInfo) {
        return CollectionUtils.isEmpty(pageInfo.getList()) ? Optional.empty() : Optional.ofNullable(pageInfo.getList().get(0));
    }

    public static <T> Optional<T> getOne(List<T> list) {
        return CollectionUtils.isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    public static void setCollectionProp(Example.Builder builder, String property, Collection<?> collection) {
        if (null != collection) {
            if (1 == collection.size()) {
                Object obj = collection.iterator().next();
                if (null != obj) {
                    builder.andWhere(Sqls.custom().andEqualTo(property, obj));
                }
            } else if (collection.isEmpty()) {
                builder.andWhere(Sqls.custom().andEqualTo("deleteTime", "ywwuyi"));
            } else {
                builder.andWhere(Sqls.custom().andIn(property, collection));
            }

        }
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

    public static void setNotEqualToProp(Example.Builder builder, String property, Object value, boolean allowStrBlank) {
        if (null != value) {
            if (allowStrBlank || !(value instanceof String) || !StringUtils.isBlank((String)value)) {
                builder.andWhere(Sqls.custom().andNotEqualTo(property, value));
            }
        }
    }

    public static void setNotEqualToProp(Example.Builder builder, String property, Object value) {
        setNotEqualToProp(builder, property, value, false);
    }

    public static void setLikeProp(Example.Builder builder, String property, String value, boolean allowStrBlank) {
        if (null != value) {
            if (allowStrBlank || !StringUtils.isBlank(value)) {
                builder.andWhere(Sqls.custom().andLike(property, getLikeStr(value)));
            }
        }
    }

    public static String getLikeStr(String value) {
        return "%" + value + "%";
    }

    public static void setLikeProp(Example.Builder builder, String property, String value) {
        setLikeProp(builder, property, value, false);
    }

    public static void setLikePropPrefix(Example.Builder builder, String property, String value, boolean allowStrBlank) {
        if (null != value) {
            if (allowStrBlank || !StringUtils.isBlank(value)) {
                builder.andWhere(Sqls.custom().andLike(property, getLikeStrPrefix(value)));
            }
        }
    }

    public static String getLikeStrPrefix(String value) {
        return value + "%";
    }

    public static void setLikePropPrefix(Example.Builder builder, String property, String value) {
        setLikePropPrefix(builder, property, value, false);
    }

    public static void setCondition(Example.Builder builder, String property, String condition) {
        Sqls sqls = Sqls.custom();
        sqls.getCriteria().getCriterions().add(new Sqls.Criterion(property, condition, "and"));
        builder.andWhere(sqls);
    }

    public static void setIntervalProp(Example.Builder builder, String property, Object beginValue, Object endValue) {
        if (null != beginValue) {
            builder.andWhere(Sqls.custom().andGreaterThanOrEqualTo(property, beginValue));
        }

        if (null != endValue) {
            builder.andWhere(Sqls.custom().andLessThan(property, endValue));
        }

    }

    public static void setNullProp(Example.Builder builder, String property, Boolean isNull) {
        if (null != isNull) {
            if (isNull) {
                builder.andWhere(Sqls.custom().andIsNull(property));
            } else {
                builder.andWhere(Sqls.custom().andIsNotNull(property));
            }

        }
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
}