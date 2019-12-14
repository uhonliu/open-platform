package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * @author liuyadu
 */
@TableName("gateway_rate_limit_api")
public class GatewayRateLimitApi extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 限制数量
     */
    private Long policyId;

    /**
     * 时间间隔(秒)
     */
    private Long apiId;


    /**
     * 获取限制数量
     *
     * @return policy_id - 限制数量
     */
    public Long getPolicyId() {
        return policyId;
    }

    /**
     * 设置限制数量
     *
     * @param policyId 限制数量
     */
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    /**
     * 获取时间间隔(秒)
     *
     * @return api_id - 时间间隔(秒)
     */
    public Long getApiId() {
        return apiId;
    }

    /**
     * 设置时间间隔(秒)
     *
     * @param apiId 时间间隔(秒)
     */
    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}
