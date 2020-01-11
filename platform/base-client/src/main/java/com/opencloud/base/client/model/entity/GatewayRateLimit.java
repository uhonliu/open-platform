package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * @author liuyadu
 */
@TableName("gateway_rate_limit")
public class GatewayRateLimit extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long policyId;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 限流规则类型:url,origin,user
     */
    private String policyType;

    /**
     * 限制数
     */
    private Long limitQuota;

    /**
     * 单位时间:seconds-秒,minutes-分钟,hours-小时,days-天
     */
    private String intervalUnit;

    public Long getLimitQuota() {
        return limitQuota;
    }

    public void setLimitQuota(Long limitQuota) {
        this.limitQuota = limitQuota;
    }

    /**
     * 获取时间单位:seconds-秒,minutes-分钟,hours-小时,days-天
     *
     * @return interval_unit - 时间单位:seconds-秒,minutes-分钟,hours-小时,days-天
     */
    public String getIntervalUnit() {
        return intervalUnit;
    }

    /**
     * 设置时间单位:second-秒,minute-分钟,hour-小时,day-天
     *
     * @param intervalUnit 时间单位:second-秒,minute-分钟,hour-小时,day-天
     */
    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit == null ? null : intervalUnit.trim();
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
}
