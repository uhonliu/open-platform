package com.opencloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.opencloud.base.client.model.RateLimitApi;
import com.opencloud.base.client.model.entity.GatewayRateLimit;
import com.opencloud.base.client.model.entity.GatewayRateLimitApi;
import com.opencloud.base.server.mapper.GatewayRateLimitApisMapper;
import com.opencloud.base.server.mapper.GatewayRateLimitMapper;
import com.opencloud.base.server.service.GatewayRateLimitService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author liuyadu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayRateLimitServiceImpl extends BaseServiceImpl<GatewayRateLimitMapper, GatewayRateLimit> implements GatewayRateLimitService {
    @Autowired
    private GatewayRateLimitMapper gatewayRateLimitMapper;

    @Autowired
    private GatewayRateLimitApisMapper gatewayRateLimitApisMapper;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<GatewayRateLimit> findListPage(PageParams pageParams) {
        GatewayRateLimit query = pageParams.mapToObject(GatewayRateLimit.class);
        QueryWrapper<GatewayRateLimit> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayRateLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayRateLimit::getPolicyType, query.getPolicyType());
        queryWrapper.orderByDesc("create_time");
        return gatewayRateLimitMapper.selectPage(pageParams, queryWrapper);
    }

    /**
     * 查询接口流量限制
     *
     * @return
     */
    @Override
    public List<RateLimitApi> findRateLimitApiList() {
        List<RateLimitApi> list = gatewayRateLimitApisMapper.selectRateLimitApi();
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @param policyId
     * @return
     */
    @Override
    public List<GatewayRateLimitApi> findRateLimitApiList(Long policyId) {
        QueryWrapper<GatewayRateLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayRateLimitApi::getPolicyId, policyId);
        List<GatewayRateLimitApi> list = gatewayRateLimitApisMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    @Override
    public GatewayRateLimit getRateLimitPolicy(Long policyId) {
        return gatewayRateLimitMapper.selectById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayRateLimit addRateLimitPolicy(GatewayRateLimit policy) {
        policy.setCreateTime(new Date());
        policy.setUpdateTime(policy.getCreateTime());
        gatewayRateLimitMapper.insert(policy);
        return policy;
    }

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayRateLimit updateRateLimitPolicy(GatewayRateLimit policy) {
        policy.setUpdateTime(new Date());
        gatewayRateLimitMapper.updateById(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    @Override
    public void removeRateLimitPolicy(Long policyId) {
        clearRateLimitApisByPolicyId(policyId);
        gatewayRateLimitMapper.deleteById(policyId);
    }

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    @Override
    public void addRateLimitApis(Long policyId, String... apis) {
        // 先清空策略已有绑定
        clearRateLimitApisByPolicyId(policyId);
        if (apis != null && apis.length > 0) {
            for (String api : apis) {
                Long apiId = Long.parseLong(api);
                // 先api解除所有绑定, 一个API只能绑定一个策略
                clearRateLimitApisByApiId(apiId);
                GatewayRateLimitApi item = new GatewayRateLimitApi();
                item.setApiId(apiId);
                item.setPolicyId(policyId);
                // 重新绑定策略
                gatewayRateLimitApisMapper.insert(item);
            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    @Override
    public void clearRateLimitApisByPolicyId(Long policyId) {
        QueryWrapper<GatewayRateLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayRateLimitApi::getPolicyId, policyId);
        gatewayRateLimitApisMapper.delete(queryWrapper);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    @Override
    public void clearRateLimitApisByApiId(Long apiId) {
        QueryWrapper<GatewayRateLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayRateLimitApi::getApiId, apiId);
        gatewayRateLimitApisMapper.delete(queryWrapper);
    }
}
