package com.opencloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.base.server.mapper.GatewayRouteMapper;
import com.opencloud.base.server.service.GatewayRouteService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liuyadu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GatewayRouteServiceImpl extends BaseServiceImpl<GatewayRouteMapper, GatewayRoute> implements GatewayRouteService {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<GatewayRoute> findListPage(PageParams pageParams) {
        QueryWrapper<GatewayRoute> queryWrapper = new QueryWrapper();
        return page(pageParams, queryWrapper);
    }

    /**
     * 查询可用路由列表
     *
     * @return
     */
    @Override
    public List<GatewayRoute> findRouteList() {
        QueryWrapper<GatewayRoute> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(GatewayRoute::getStatus, BaseConstants.ENABLED);
        List<GatewayRoute> list = list(queryWrapper);
        return list;
    }

    /**
     * 获取路由信息
     *
     * @param routeId
     * @return
     */
    @Override
    public GatewayRoute getRoute(Long routeId) {
        return getById(routeId);
    }

    /**
     * 添加路由
     *
     * @param route
     */
    @Override
    public void addRoute(GatewayRoute route) {
        if (StringUtils.isBlank(route.getPath())) {
            throw new OpenAlertException(String.format("path不能为空!"));
        }
        if (isExist(route.getRouteName())) {
            throw new OpenAlertException(String.format("路由名称已存在!"));
        }
        route.setIsPersist(0);
        save(route);
    }

    /**
     * 更新路由
     *
     * @param route
     */
    @Override
    public void updateRoute(GatewayRoute route) {
        if (StringUtils.isBlank(route.getPath())) {
            throw new OpenAlertException(String.format("path不能为空"));
        }
        GatewayRoute saved = getRoute(route.getRouteId());
        if (saved == null) {
            throw new OpenAlertException("路由信息不存在!");
        }
        if (saved != null && saved.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许修改"));
        }
        if (!saved.getRouteName().equals(route.getRouteName())) {
            // 和原来不一致重新检查唯一性
            if (isExist(route.getRouteName())) {
                throw new OpenAlertException("路由名称已存在!");
            }
        }
        updateById(route);
    }

    /**
     * 删除路由
     *
     * @param routeId
     */
    @Override
    public void removeRoute(Long routeId) {
        GatewayRoute saved = getRoute(routeId);
        if (saved != null && saved.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除"));
        }
        removeById(routeId);
    }

    /**
     * 查询地址是否存在
     *
     * @param routeName
     */
    @Override
    public Boolean isExist(String routeName) {
        QueryWrapper<GatewayRoute> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(GatewayRoute::getRouteName, routeName);
        int count = count(queryWrapper);
        return count > 0;
    }
}
