package com.opencloud.msg.client.model;

import io.swagger.annotations.ApiModel;

import java.util.Map;

/**
 * @author: liuyadu
 * @date: 2019/7/17 14:01
 * @description:
 */
@ApiModel("邮件模板消息")
public class EmailTplMessage extends EmailMessage {
    /**
     * 模板编号
     */
    private String tplCode;

    /**
     * 模板参数
     */
    private Map<String, Object> tplParams;

    public String getTplCode() {
        return tplCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public Map<String, Object> getTplParams() {
        return tplParams;
    }

    public void setTplParams(Map<String, Object> tplParams) {
        this.tplParams = tplParams;
    }
}
