package com.bsd.dingtalk.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 钉钉配置信息
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@TableName("org_dingtalk")
public class Dingtalk extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业corpid
     */
    private String corpId;

    /**
     * 应用的agentdId
     */
    private String agentdId;

    /**
     * 应用的AppKey
     */
    private String appKey;

    /**
     * 应用的AppSecret
     */
    private String appSecret;

    /**
     * 数据加密密钥
     */
    private String encodingAesKey;

    /**
     * 加解密需要用到的token
     */
    private String token;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 最后修改人
     */
    private Long updateBy;


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAgentdId() {
        return agentdId;
    }

    public void setAgentdId(String agentdId) {
        this.agentdId = agentdId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "Dingtalk{" +
                "companyId=" + companyId +
                ", corpId=" + corpId +
                ", agentdId=" + agentdId +
                ", appKey=" + appKey +
                ", appSecret=" + appSecret +
                ", encodingAesKey=" + encodingAesKey +
                ", token=" + token +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                "}";
    }
}
