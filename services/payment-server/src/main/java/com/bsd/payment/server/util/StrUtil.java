package com.bsd.payment.server.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dingzhiwei
 * @date 17/11/1
 */
public class StrUtil {
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String toString(Object obj, String nullStr) {
        return obj == null ? nullStr : obj.toString();
    }

    public static String retEmptyToNull(String val) {
        return StringUtils.isEmpty(val) ? null : val;
    }

    public static String substr(String val, int maxSize) {
        if (StringUtils.isEmpty(val)) {
            return val;
        }
        if (val.length() <= maxSize) {
            return val;
        }
        return val.substring(0, maxSize);
    }
}
