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
@ApiModel("商品订单表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_goods_order")
public class GoodsOrder extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品订单ID")
    private String goodsOrderId;

    @ApiModelProperty("商品ID")
    private String goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty(value = "金额,单位分", name = "amount", example = "0")
    private Long amount;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty(value = "订单状态,订单生成(0),支付成功(1),处理完成(2),处理失败(-1)", name = "status", example = "0")
    private Byte status;

    @ApiModelProperty("支付订单号")
    private String payOrderId;

    @ApiModelProperty("渠道编码")
    private String channelCode;

    @ApiModelProperty("支付渠道用户ID(微信openID或支付宝账号等第三方支付账号)")
    private String channelUserId;
}