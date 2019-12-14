package com.bsd.payment.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author liujianhong
 */
@ApiModel("商户通知表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "bsd_mch_notify")
public class MchNotify extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("商户ID")
    private String mchId;

    @ApiModelProperty("商户订单号")
    private String mchOrderNo;

    @ApiModelProperty("订单类型:1-支付,2-转账,3-退款")
    private String orderType;

    @ApiModelProperty("通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "通知次数", name = "notifyCount", example = "0")
    private Byte notifyCount;

    @ApiModelProperty("通知响应结果")
    private String result;

    @ApiModelProperty(value = "通知状态,1-通知中,2-通知成功,3-通知失败", name = "status", example = "0")
    private Byte status;

    @ApiModelProperty("最后一次通知时间")
    private Date lastNotifyTime;
}