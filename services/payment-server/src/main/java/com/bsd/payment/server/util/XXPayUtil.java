package com.bsd.payment.server.util;

import com.alibaba.fastjson.JSON;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.constant.PayEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付工具类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class XXPayUtil {
    private static final MyLog _log = MyLog.getLog(XXPayUtil.class);

    public static Map<String, Object> makeRetMap(String retCode, String retMsg, String resCode, String errCode, String errCodeDesc) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (retCode != null) {
            retMap.put(PayConstant.RETURN_PARAM_RETCODE, retCode);
        }
        if (retMsg != null) {
            retMap.put(PayConstant.RETURN_PARAM_RETMSG, retMsg);
        }
        if (resCode != null) {
            retMap.put(PayConstant.RESULT_PARAM_RESCODE, resCode);
        }
        if (errCode != null) {
            retMap.put(PayConstant.RESULT_PARAM_ERRCODE, errCode);
        }
        if (errCodeDesc != null) {
            retMap.put(PayConstant.RESULT_PARAM_ERRCODEDES, errCodeDesc);
        }
        return retMap;
    }

    public static Map<String, Object> makeRetMap(String retCode, String retMsg, String resCode, PayEnum payEnum) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (retCode != null) {
            retMap.put(PayConstant.RETURN_PARAM_RETCODE, retCode);
        }
        if (retMsg != null) {
            retMap.put(PayConstant.RETURN_PARAM_RETMSG, retMsg);
        }
        if (resCode != null) {
            retMap.put(PayConstant.RESULT_PARAM_RESCODE, resCode);
        }
        if (payEnum != null) {
            retMap.put(PayConstant.RESULT_PARAM_ERRCODE, payEnum.getCode());
            retMap.put(PayConstant.RESULT_PARAM_ERRCODEDES, payEnum.getMessage());
        }
        return retMap;
    }

    public static String makeRetData(Map retMap, String resKey) {
        if (retMap.get(PayConstant.RETURN_PARAM_RETCODE).equals(PayConstant.RETURN_VALUE_SUCCESS)) {
            String sign = PayDigestUtil.getSign(retMap, resKey, "payParams");
            retMap.put(PayConstant.RESULT_PARAM_SIGN, sign);
        }
        _log.info("生成响应数据:{}", retMap);
        return JSON.toJSONString(retMap);
    }

    public static String makeRetFail(Map retMap) {
        _log.info("生成响应数据:{}", retMap);
        return JSON.toJSONString(retMap);
    }

    /**
     * 验证支付中心签名
     *
     * @param params
     * @return
     */
    public static boolean verifyPaySign(Map<String, Object> params, String key) {
        String sign = (String) params.get("sign"); // 签名
        params.remove("sign");    // 不参与签名
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    /**
     * 验证VV平台支付中心签名
     *
     * @param params
     * @return
     */
    public static boolean verifyPaySign(Map<String, Object> params, String key, String... noSigns) {
        String sign = (String) params.get("sign"); // 签名
        params.remove("sign");    // 不参与签名
        if (noSigns != null && noSigns.length > 0) {
            for (String noSign : noSigns) {
                params.remove(noSign);
            }
        }
        String checkSign = PayDigestUtil.getSign(params, key);
        if (!checkSign.equalsIgnoreCase(sign)) {
            return false;
        }
        return true;
    }

    public static String genUrlParams(Map<String, Object> paraMap) {
        if (paraMap == null || paraMap.isEmpty()) {
            return "";
        }
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for (String key : keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key));
            if (++i == keySet.size()) {
                break;
            }
            urlParam.append("&");
        }
        return urlParam.toString();
    }
}
