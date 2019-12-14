package com.opencloud.common.mybatis;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.HashMap;

/**
 * 自定义Map
 */
public class EntityMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    public static EnumConvertInterceptor interceptors = null;
    // private RedisUtils redisUtils = SpringContextHolder.getBean("redisUtils");
    // private Map<Object, Object> dataMaps = redisUtils.getMap("DICTDATA_MAPS");

    public EntityMap() {

    }

    private static String getField(String field) {
        String str = "";
        int s = field.indexOf(".");
        if (s > -1) {
            str = field.substring(s + 1);
        } else {
            str = field;
        }
        return str;
    }

    public static void setEnumConvertInterceptor(EnumConvertInterceptor interceptor) {
        interceptors = interceptor;
    }

    @Override
    public EntityMap put(String key, Object value) {
        /*List<Object> dictKeys = redisUtils.getList("DICT_KEYS");*/
        /*判断字段是否是字典类型*/
        /*if (dictKeys.contains(key) && ObjectUtils.isNotEmpty(value)) {
            Object dictValue = dataMaps.get(key + "_" + value.toString());
            *//*返回数据中添加字典显示值*//*
            super.put(key + "Title", dictValue);
        }*/
        if (ObjectUtils.isNotEmpty(interceptors)) {
            interceptors.convert(this, key, value);
        }
        if (ObjectUtils.isNotNull(value)) {
            super.put(key, value);
        } else {
            super.put(key, "");
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        T t = null;
        Object obj = super.get(key);
        if (ObjectUtils.isNotEmpty(obj)) {
            t = (T) obj;
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T def) {
        Object obj = super.get(key);
        if (ObjectUtils.isNotEmpty(obj)) {
            return (T) obj;
        } else {
            return def;
        }
    }
}
