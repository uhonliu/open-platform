package com.opencloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.opencloud.base.client.model.IpLimitApi;
import com.opencloud.base.client.model.entity.GatewayIpLimit;
import com.opencloud.base.client.model.entity.GatewayIpLimitApi;
import com.opencloud.base.server.mapper.GatewayIpLimitApisMapper;
import com.opencloud.base.server.mapper.GatewayIpLimitMapper;
import com.opencloud.base.server.service.GatewayIpLimitService;
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
public class GatewayIpLimitServiceImpl extends BaseServiceImpl<GatewayIpLimitMapper, GatewayIpLimit> implements GatewayIpLimitService {
    @Autowired
    private GatewayIpLimitMapper gatewayIpLimitMapper;
    @Autowired
    private GatewayIpLimitApisMapper gatewayIpLimitApisMapper;


    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<GatewayIpLimit> findListPage(PageParams pageParams) {
        GatewayIpLimit query = pageParams.mapToObject(GatewayIpLimit.class);
        QueryWrapper<GatewayIpLimit> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getPolicyName()), GatewayIpLimit::getPolicyName, query.getPolicyName())
                .eq(ObjectUtils.isNotEmpty(query.getPolicyType()), GatewayIpLimit::getPolicyType, query.getPolicyType());
        queryWrapper.orderByDesc("create_time");
        return gatewayIpLimitMapper.selectPage(pageParams, queryWrapper);
    }

    /**
     * 查询白名单
     *
     * @return
     */
    @Override
    public List<IpLimitApi> findBlackList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(0);
        return list;
    }

    /**
     * 查询黑名单
     *
     * @return
     */
    @Override
    public List<IpLimitApi> findWhiteList() {
        List<IpLimitApi> list = gatewayIpLimitApisMapper.selectIpLimitApi(1);
        return list;
    }

    /**
     * 查询策略已绑定API列表
     *
     * @return
     */
    @Override
    public List<GatewayIpLimitApi> findIpLimitApiList(Long policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        List<GatewayIpLimitApi> list = gatewayIpLimitApisMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 获取IP限制策略
     *
     * @param policyId
     * @return
     */
    @Override
    public GatewayIpLimit getIpLimitPolicy(Long policyId) {
        return gatewayIpLimitMapper.selectById(policyId);
    }

    /**
     * 添加IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayIpLimit addIpLimitPolicy(GatewayIpLimit policy) {
        policy.setCreateTime(new Date());
        policy.setUpdateTime(policy.getCreateTime());
        gatewayIpLimitMapper.insert(policy);
        return policy;
    }

    /**
     * 更新IP限制策略
     *
     * @param policy
     */
    @Override
    public GatewayIpLimit updateIpLimitPolicy(GatewayIpLimit policy) {
        policy.setUpdateTime(new Date());
        gatewayIpLimitMapper.updateById(policy);
        return policy;
    }

    /**
     * 删除IP限制策略
     *
     * @param policyId
     */
    @Override
    public void removeIpLimitPolicy(Long policyId) {
        clearIpLimitApisByPolicyId(policyId);
        gatewayIpLimitMapper.deleteById(policyId);
    }

    /**
     * 绑定API, 一个API只能绑定一个策略
     *
     * @param policyId
     * @param apis
     */
    @Override
    public void addIpLimitApis(Long policyId, String... apis) {
        // 先清空策略已有绑定
        clearIpLimitApisByPolicyId(policyId);
        if (apis != null && apis.length > 0) {
            for (String api : apis) {
                // 先api解除所有绑定, 一个API只能绑定一个策略
                Long apiId = Long.parseLong(api);
                clearIpLimitApisByApiId(apiId);
                GatewayIpLimitApi item = new GatewayIpLimitApi();
                item.setApiId(apiId);
                item.setPolicyId(policyId);
                // 重新绑定策略
                gatewayIpLimitApisMapper.insert(item);
            }
        }
    }

    /**
     * 清空绑定的API
     *
     * @param policyId
     */
    @Override
    public void clearIpLimitApisByPolicyId(Long policyId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getPolicyId, policyId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }

    /**
     * API解除所有策略
     *
     * @param apiId
     */
    @Override
    public void clearIpLimitApisByApiId(Long apiId) {
        QueryWrapper<GatewayIpLimitApi> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(GatewayIpLimitApi::getApiId, apiId);
        gatewayIpLimitApisMapper.delete(queryWrapper);
    }
}
