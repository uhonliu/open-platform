package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

import java.util.Date;

/**
 * 系统权限-应用关联
 *
 * @author liuyadu
 */
@TableName("base_authority_app")
public class BaseAuthorityApp extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long authorityId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 过期时间:null表示长期
     */
    private Date expireTime;

    /**
     * 获取权限ID
     *
     * @return authority_id - 权限ID
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    /**
     * 设置权限ID
     *
     * @param authorityId 权限ID
     */
    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * 获取应用ID
     *
     * @return app_id - 应用ID
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置应用ID
     *
     * @param appId 应用ID
     */
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    /**
     * 获取过期时间:null表示长期
     *
     * @return expire_time - 过期时间:null表示长期
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * 设置过期时间:null表示长期
     *
     * @param expireTime 过期时间:null表示长期
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
