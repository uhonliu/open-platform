package com.bsd.user.server.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: linrongxin
 * @Date: 2019/8/26 11:42
 */
public enum UserSourceEnum {
    KJZD(0, "跨境知道"),
    MJCZ(1, "卖家成长"),
    MANUAL_INPUT(3, "人工录入");

    private String sourceName;
    private int sourceCode;
    private static Map<Integer, String> lookup = new HashMap<Integer, String>();

    static {
        for (UserSourceEnum item : EnumSet.allOf(UserSourceEnum.class)) {
            lookup.put(item.sourceCode, item.sourceName);
        }
        //限制map被修改
        lookup = Collections.unmodifiableMap(lookup);
    }


    UserSourceEnum(int sourceCode, String sourceName) {
        this.sourceCode = sourceCode;
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(int sourceCode) {
        this.sourceCode = sourceCode;
    }

    public static Map<Integer, String> userSourceMap() {
        return lookup;
    }
}
