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
@ApiModel("苹果支付凭据表")
@TableName("bsd_iap_receipt")
@Data
@EqualsAndHashCode(callSuper = false)
public class IapReceipt extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("支付订单号")
    private String payOrderId;

    @ApiModelProperty("商户ID")
    private String mchId;

    @ApiModelProperty("IAP业务号")
    private String transactionId;

    @ApiModelProperty(value = "处理状态:0-未处理,1-处理成功,-1-处理失败", name = "status", example = "0")
    private Byte status;

    @ApiModelProperty(value = "处理次数", name = "handleCount", example = "0")
    private Byte handleCount;

    @ApiModelProperty("凭据内容")
    private String receiptData;
}