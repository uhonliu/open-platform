package com.opencloud.msg.client.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author woodev
 */
@ApiModel("短信消息")
public class SmsMessage extends BaseMessage {
    private static final long serialVersionUID = -8924332753124953766L;

    @ApiModelProperty("手机号码")
    private String phoneNum;
    @ApiModelProperty("短信签名")
    private String signName;
    @ApiModelProperty("模板编号")
    private String tplCode;
    @ApiModelProperty("模板参数")
    private String tplParams;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTplCode() {
        return tplCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public String getTplParams() {
        return tplParams;
    }

    public void setTplParams(String tplParams) {
        this.tplParams = tplParams;
    }
}
