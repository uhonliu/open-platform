package com.bsd.payment.server.enumm;

import com.bsd.payment.server.constant.PayConstant;

/**
 * 支付渠道枚举类
 * 对应方法调用返回值中的RetCode和RetMsg
 *
 * @author wangyankai690
 * @date 2019/8/27
 */
public enum ChannelEnum {
    WX_JSAPI("WX_JSAPI", "微信公众号支付"),
    WX_NATIVE("WX_NATIVE", "微信原生扫码支付"),
    WX_APP("WX_APP", "微信APP支付"),
    WX_MWEB("WX_MWEB", "微信H5支付"),
    ALIPAY_MOBILE("ALIPAY_MOBILE", "支付宝移动支付"),
    ALIPAY_PC("ALIPAY_PC", "支付宝PC支付"),
    ALIPAY_WAP("ALIPAY_WAP", "支付宝WAP支付"),
    ALIPAY_QR("ALIPAY_QR", "支付宝当面付之扫码支付");

    private String code;
    private String value;

    ChannelEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 根据code获取value
     *
     * @param code
     * @return
     */
    public static String getValueByCode(String code) {
        for (ChannelEnum channelEnum : ChannelEnum.values()) {
            if (code.equals(channelEnum.getCode())) {
                return channelEnum.getValue();
            }
        }
        return null;
    }

    /**
     * 根据code获取type
     *
     * @param code
     * @return
     */
    public static String getTypeByCode(String code) {
        if (code.startsWith("WX")) {
            return PayConstant.CHANNEL_NAME_WX.toLowerCase();
        } else if (code.startsWith("ALIPAY")) {
            return PayConstant.CHANNEL_NAME_ALIPAY.toLowerCase();
        }
        return null;
    }
}
