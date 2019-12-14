package com.opencloud.base.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.IpLimitApi;
import com.opencloud.base.client.model.entity.GatewayIpLimit;
import com.opencloud.base.client.model.entity.GatewayIpLimitApi;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 网关IP访问控制
 *
 * @author liuyadu
 */
public interface GatewayIpLimitService extends IBaseService<GatewayIpLimit> {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<GatewayIpLimit> findListPage(PageParams pageParams);

    /**
     * 查询白名单
     *
     * @return
     */
    List<IpLimitApi> findBlackList();

    /**
     * 查询黑名单
     *
     * @return
     */
    List<IpLimitApi> findWhiteList();

    /**
     * 查询策略已绑定API列表
     *
     * @return
     */
    List<GatewayIpLimitApi> findIpLimitApiList(Long policyId);

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    GatewayIpLimit getIpLimitPolicy(Long policyId);

    /**
     * 添加IP限制策略
     *
     * @param policy
     * @return
     */
    GatewayIpLimit addIpLimitPolicy(GatewayIpLimit policy);

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    GatewayIpLimit updateIpLimitPolicy(GatewayIpLimit policy);

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    void removeIpLimitPolicy(Long policyId);

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    void addIpLimitApis(Long policyId, String... apis);

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    void clearIpLimitApisByPolicyId(Long policyId);

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    void clearIpLimitApisByApiId(Long apiId);
}
