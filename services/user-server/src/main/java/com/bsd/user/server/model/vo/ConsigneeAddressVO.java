package com.bsd.user.server.model.vo;

import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/8/22 17:55
 */
@Data
public class ConsigneeAddressVO {
    /**
     * 收件人
     */
    private String consigneeName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否为默认地址:0-否 1-是
     */
    private Integer isDefault;
}
