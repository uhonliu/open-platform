package com.bsd.migration.service.impl;

import com.bsd.migration.constants.CommonConstants;
import com.bsd.migration.model.dto.PageResult;
import com.bsd.migration.model.entity.GatewayRoute;
import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.ApiService;
import com.bsd.migration.service.RouteService;
import com.bsd.migration.utils.OAuth2RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yuanyujun
 * @Date: 2019/10/29
 */
@Slf4j
@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    private ApiService apiService;

    @Async
    @Override
    public void sync(Config sourceConfig, Config targetConfig) {
        log.info("同步路由数据开始");
        //源路由
        List<GatewayRoute> sourceRoutes = getRoutesByIp(sourceConfig);

        if (sourceRoutes == null || sourceRoutes.size() == 0) {
            log.info("源主机上没有路由信息");
            return;
        }
        //目标路由
        List<GatewayRoute> targetRoutes = getRoutesByIp(targetConfig);
        //转成map,便于搜素
        Map<String, GatewayRoute> targetRoutesMap = getRoutesMap(targetRoutes);
        Map<String, GatewayRoute> sourceRoutesMap = getRoutesMap(sourceRoutes);
        try {
            synchronizeRoute(sourceRoutes, targetRoutes, targetConfig, targetRoutesMap);
            apiService.sync(sourceConfig, targetConfig, sourceRoutesMap, targetRoutesMap);
            log.info("同步路由数据结束");
        } catch (IOException e) {
            log.info("同步菜路由据失败,详细原因请查看日志");
            return;
        }
        log.info("同步路由数据成功");
    }


    /**
     * 同步路由数据到目标服务器上
     *
     * @param sourceRoutes
     * @param targetRoutes
     * @param targetConfig
     */
    private void synchronizeRoute(List<GatewayRoute> sourceRoutes, List<GatewayRoute> targetRoutes, Config targetConfig, Map<String, GatewayRoute> targetRoutesMap) throws IOException {
        //需要更新的路由列表
        List<GatewayRoute> updateRouteList = new ArrayList<GatewayRoute>();
        //需要插入的路由列表
        List<GatewayRoute> insertRouteList = new ArrayList<GatewayRoute>();
        for (GatewayRoute sourceRoute : sourceRoutes) {
            //RouteName唯一性
            GatewayRoute targetRoute = targetRoutesMap.get(sourceRoute.getRouteName());
            if (targetRoute != null) {
                if (isUpdate(sourceRoute, targetRoute)) {
                    //需要更新的路由
                    updateRouteList.add(targetRoute);
                }
                continue;
            }
            //新增的路由
            insertRouteList.add(sourceRoute);
        }
        //保存新路由
        log.info("保存新路由开始,新增路由数:{}", insertRouteList.size());
        saveNewRoute(insertRouteList, targetRoutes, targetRoutesMap, targetConfig);
        log.info("保存新路由结束");
        //更新旧路由
        log.info("更新旧路由开始,更新路由数:{}", updateRouteList.size());
        updateOldRoute(updateRouteList, targetRoutesMap, targetConfig);
        log.info("更新旧路由结束");
    }

    /**
     * 更新路由
     *
     * @param updateRouteList
     * @param targetRoutesMap
     */
    private void updateOldRoute(List<GatewayRoute> updateRouteList, Map<String, GatewayRoute> targetRoutesMap, Config targetConfig) throws IOException {
        int size = updateRouteList.size();
        for (GatewayRoute baseRoute : updateRouteList) {
            log.info("剩余更新数:{},updateOldRoute:{}", size, baseRoute);

            GatewayRoute updateRouteDTO = new GatewayRoute();
            BeanUtils.copyProperties(baseRoute, updateRouteDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            ResultBody resultBody = OAuth2RequestUtils.postReq(targetConfig, CommonConstants.UPDATE_GATEWAY_ROUTE_URL_SUFFIX, objectMapper.writeValueAsString(updateRouteDTO), new ParameterizedTypeReference<ResultBody>() {
            });
            if (resultBody != null && resultBody.getCode() == 0) {
                //更新成功
                targetRoutesMap.put(baseRoute.getRouteName(), baseRoute);
                targetRoutesMap.put(String.valueOf(baseRoute.getRouteId()), baseRoute);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            size--;
        }
    }


    /**
     * 保存路由数据
     *
     * @param insertRouteList
     * @param targetRoutes
     * @param targetRoutesMap
     * @param targetConfig
     * @throws IOException
     */
    private void saveNewRoute(List<GatewayRoute> insertRouteList, List<GatewayRoute> targetRoutes, Map<String, GatewayRoute> targetRoutesMap, Config targetConfig) throws IOException {
        //插入数据
        for (GatewayRoute baseRoute : insertRouteList) {
            log.info("saveNewRoute:{}", baseRoute);
            GatewayRoute addRouteDTO = new GatewayRoute();
            BeanUtils.copyProperties(baseRoute, addRouteDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            ResultBody<Long> resultBody = OAuth2RequestUtils.postReq(targetConfig, CommonConstants.ADD_GATEWAY_ROUTE_URL_SUFFIX, objectMapper.writeValueAsString(addRouteDTO), new ParameterizedTypeReference<ResultBody<Long>>() {
            });
            baseRoute.setRouteId(resultBody.getData());
            targetRoutes.add(baseRoute);
            targetRoutesMap.put(baseRoute.getRouteName(), baseRoute);
            targetRoutesMap.put(String.valueOf(baseRoute.getRouteId()), baseRoute);
        }
    }

    /**
     * 判断是否需要更新
     *
     * @param sourceRoute
     * @param targetRoute
     * @return
     */
    private boolean isUpdate(GatewayRoute sourceRoute, GatewayRoute targetRoute) {
        boolean isUpdate = false;
        GatewayRoute tempTargetRoute = new GatewayRoute();
        BeanUtils.copyProperties(targetRoute, tempTargetRoute);

        //状态:0-无效 1-有效
        if (sourceRoute.getStatus().intValue() != targetRoute.getStatus().intValue()) {
            targetRoute.setStatus(sourceRoute.getStatus());
            log.info("路由状态改变了");
            isUpdate = true;
        }
        //保留数据0-否 1-是 不允许删除
        if (!sourceRoute.getIsPersist().equals(targetRoute.getIsPersist())) {
            targetRoute.setIsPersist(sourceRoute.getIsPersist());
            log.info("保留数据改变了");
            isUpdate = true;
        }
        //路由说明
        if (!sourceRoute.getRouteDesc().equals(targetRoute.getRouteDesc())) {
            targetRoute.setRouteDesc(sourceRoute.getRouteDesc());
            log.info("路由说明改变了");
            isUpdate = true;
        }

        //请求路径
        if (!sourceRoute.getPath().equals(targetRoute.getPath())) {
            targetRoute.setPath(sourceRoute.getPath());
            log.info("请求路径改变了");
            isUpdate = true;
        }
        //路由类型:service-负载均衡 url-反向代理
        if (!sourceRoute.getRouteType().equals(targetRoute.getRouteType())) {
            targetRoute.setRouteType(sourceRoute.getRouteType());
            log.info("路由类型改变了");
            isUpdate = true;
        }
        //完整地址
        if (!sourceRoute.getUrl().equals(targetRoute.getUrl())) {
            log.info("完整地址改变了");
            targetRoute.setUrl(sourceRoute.getUrl());
            isUpdate = true;
        }
        //服务ID
        if (!sourceRoute.getServiceId().equals(targetRoute.getServiceId())) {
            targetRoute.setServiceId(sourceRoute.getServiceId());
            log.info("服务ID改变了");
            isUpdate = true;
        }
        //忽略前缀
        if (!sourceRoute.getStripPrefix().equals(targetRoute.getStripPrefix())) {
            log.info("忽略前缀改变了");
            targetRoute.setStripPrefix(sourceRoute.getStripPrefix());
            isUpdate = true;
        }
        if (isUpdate) {
            log.info("isUpdate-targetRoute:{}", tempTargetRoute);
            log.info("isUpdate-sourceRoute:{}", sourceRoute);
        }
        return isUpdate;
    }

    /**
     * list 转 map
     *
     * @param targetRoutes
     * @return
     */
    private Map<String, GatewayRoute> getRoutesMap(List<GatewayRoute> targetRoutes) {
        Map<String, GatewayRoute> targetRoutesMap = new HashMap<>(targetRoutes.size());
        for (GatewayRoute targetRoute : targetRoutes) {
            targetRoutesMap.put(targetRoute.getRouteName(), targetRoute);
            targetRoutesMap.put(String.valueOf(targetRoute.getRouteId()), targetRoute);
        }
        return targetRoutesMap;
    }

    /**
     * 远程获取路由信息
     *
     * @param config
     * @return
     */
    private List<GatewayRoute> getRoutesByIp(Config config) {
        String url = CommonConstants.GET_API_URL_SUFFIX + "?page=1&limit=1000";
        ResultBody<PageResult<GatewayRoute>> resultBody = OAuth2RequestUtils.getReq(config, url, new ParameterizedTypeReference<ResultBody<PageResult<GatewayRoute>>>() {
        });
        return resultBody.getData().getRecords();
    }

    public static void main(String[] args) {
        RouteServiceImpl service = new RouteServiceImpl();
        Config sourceConfig = new Config();
        sourceConfig.setGatewayUrl("http://192.168.7.137:8888");
        sourceConfig.setClientId("7gBZcbsC7kLIWCdELIl8nxcs");
        sourceConfig.setClientSecret("0osTIhce7uPvDKHz6aa67bhCukaKoYl4");
        Config targetConfig = new Config();
        targetConfig.setGatewayUrl("http://192.168.7.83:8888");
        targetConfig.setClientId("7gBZcbsC7kLIWCdELIl8nxcs");
        targetConfig.setClientSecret("0osTIhce7uPvDKHz6aa67bhCukaKoYl4");
        service.sync(sourceConfig, targetConfig);
    }
}
