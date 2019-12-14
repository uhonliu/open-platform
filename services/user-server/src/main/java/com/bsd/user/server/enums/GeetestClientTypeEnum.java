package com.bsd.user.server.enums;

/**
 * 极验客户端类型枚举
 *
 * @Author: linrongxin
 * @Date: 2019/9/4 15:27
 */
public enum GeetestClientTypeEnum {
    WEB("web", "pc浏览器"),
    H5("h5", "手机浏览器,webview"),
    NATIVE("native", "原生app"),
    UNKNOWN("unknown", "未知");

    /**
     * 客户端类型编码
     */
    private String clientTypeCode;
    /**
     * 客户端类型说明
     */
    private String clientTypeName;

    /**
     * 构造方法
     *
     * @param clientTypeCode
     * @param clientTypeName
     */
    GeetestClientTypeEnum(String clientTypeCode, String clientTypeName) {
        this.clientTypeCode = clientTypeCode;
        this.clientTypeName = clientTypeName;
    }

    /**
     * 判断客户端类型编码是否存在
     *
     * @param clientTypeCode
     * @return
     */
    public static boolean isContainCode(String clientTypeCode) {
        for (GeetestClientTypeEnum getestClientTypeEnum : GeetestClientTypeEnum.values()) {
            if (getestClientTypeEnum.clientTypeCode.equals(clientTypeCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取客户端类型编码
     *
     * @return
     */
    public String getClientTypeCode() {
        return clientTypeCode;
    }
}
