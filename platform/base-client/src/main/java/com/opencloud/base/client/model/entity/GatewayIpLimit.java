package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * @author liuyadu
 */
@TableName("gateway_ip_limit")
public class GatewayIpLimit extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long policyId;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 策略类型:0-拒绝/黑名单 1-允许/白名单
     */
    private Integer policyType;

    /**
     * ip地址/IP段:多个用隔开;最多10个
     */
    private String ipAddress;

    /**
     * 获取策略ID
     *
     * @return policy_id - 策略ID
     */
    public Long getPolicyId() {
        return policyId;
    }

    /**
     * 设置策略ID
     *
     * @param policyId 策略ID
     */
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    /**
     * 获取策略名称
     *
     * @return policy_name - 策略名称
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * 设置策略名称
     *
     * @param policyName 策略名称
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName == null ? null : policyName.trim();
    }

    /**
     * 获取策略类型:0-拒绝/黑名单 1-允许/白名单
     *
     * @return policy_type - 策略类型:0-拒绝/黑名单 1-允许/白名单
     */
    public Integer getPolicyType() {
        return policyType;
    }

    /**
     * 设置策略类型:0-拒绝/黑名单 1-允许/白名单
     *
     * @param policyType 策略类型:0-拒绝/黑名单 1-允许/白名单
     */
    public void setPolicyType(Integer policyType) {
        this.policyType = policyType;
    }

    /**
     * 获取ip地址/IP段:多个用,隔开,最多20个
     *
     * @return ip_address - ip地址/IP段:多个用,隔开,最多20个
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 设置ip地址/IP段:多个用,隔开,最多20个
     *
     * @param ipAddress ip地址/IP段:多个用,隔开,最多20个
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }
}
