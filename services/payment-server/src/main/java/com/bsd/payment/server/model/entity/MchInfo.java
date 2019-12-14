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
@ApiModel("商户信息表")
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("bsd_mch_info")
public class MchInfo extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商户ID")
    private String mchId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("类型,0-平台,1-私有")
    private String type;

    @ApiModelProperty("请求私钥")
    private String reqKey;

    @ApiModelProperty("响应私钥")
    private String resKey;

    @ApiModelProperty(value = "商户状态,0-停止使用,1-使用中", name = "state", example = "0")
    private Byte state;
}