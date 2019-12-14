package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LJH-PC
 */
@ApiModel("支付渠道表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_pay_channel")
public class PayChannel extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "channel_id", value = "渠道ID", example = "1")
    private Long channelId;

    @ApiModelProperty("渠道编码")
    private String channelCode;

    @ApiModelProperty("渠道名称,如:alipay,wechat")
    private String channelName;

    @ApiModelProperty("渠道商户ID")
    private String channelMchId;

    @ApiModelProperty("商户ID")
    private String mchId;

    @ApiModelProperty(value = "渠道状态,0-停止使用,1-使用中", name = "state", example = "0")
    private Byte state;

    @ApiModelProperty("配置参数,json字符串")
    private String param;

    @ApiModelProperty("备注")
    private String remark;
}