package com.bsd.payment.server.configuration.channel.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsd.payment.server.util.RegexUtils;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.opencloud.common.utils.StringUtils;

import java.io.File;

/**
 * @author liujianhong
 * @date 2019-07-05
 */
public class WxPayUtil {
    /**
     * 获取微信支付配置
     *
     * @param configParam
     * @param tradeType
     * @param certRootPath
     * @param notifyUrl
     * @return
     */
    public static WxPayConfig getWxPayConfig(String configParam, String tradeType, String certRootPath, String notifyUrl) {
        if (StringUtils.isNotBlank(certRootPath)) {
            String urlSeparator = "/";
            if (RegexUtils.isUrl(certRootPath) && !certRootPath.endsWith(urlSeparator)) {
                certRootPath = certRootPath + urlSeparator;
            } else if (!RegexUtils.isUrl(certRootPath) && !certRootPath.endsWith(File.separator)) {
                certRootPath = certRootPath + File.separator;
            }
        }
        WxPayConfig wxPayConfig = new WxPayConfig();
        JSONObject paramObj = JSON.parseObject(configParam);
        wxPayConfig.setMchId(paramObj.getString("mchId"));
        wxPayConfig.setAppId(paramObj.getString("appId"));
        wxPayConfig.setKeyPath(certRootPath + paramObj.getString("certLocalPath"));
        wxPayConfig.setMchKey(paramObj.getString("key"));
        wxPayConfig.setNotifyUrl(notifyUrl);
        wxPayConfig.setTradeType(tradeType);
        return wxPayConfig;
    }

    /**
     * 获取微信支付配置
     *
     * @param configParam
     * @return
     */
    public static WxPayConfig getWxPayConfig(String configParam) {
        WxPayConfig wxPayConfig = new WxPayConfig();
        JSONObject paramObj = JSON.parseObject(configParam);
        wxPayConfig.setMchId(paramObj.getString("mchId"));
        wxPayConfig.setAppId(paramObj.getString("appId"));
        wxPayConfig.setMchKey(paramObj.getString("key"));
        return wxPayConfig;
    }
}
