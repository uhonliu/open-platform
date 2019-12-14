package com.opencloud.base.client.model;

import com.opencloud.base.client.model.entity.BaseApi;

import java.io.Serializable;

/**
 * @author liuyadu
 */
public class RateLimitApi extends BaseApi implements Serializable {
    private static final long serialVersionUID = 1212925216631391016L;
    private Long itemId;
    private Long policyId;
    private String policyName;
    private Long limitQuota;
    private String intervalUnit;
    private String url;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public Long getLimitQuota() {
        return limitQuota;
    }

    public void setLimitQuota(Long limitQuota) {
        this.limitQuota = limitQuota;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}