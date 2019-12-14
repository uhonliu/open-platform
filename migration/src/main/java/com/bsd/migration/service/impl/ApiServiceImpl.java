package com.bsd.migration.service.impl;

import com.bsd.migration.constants.CommonConstants;
import com.bsd.migration.model.dto.PageResult;
import com.bsd.migration.model.entity.BaseApi;
import com.bsd.migration.model.entity.GatewayRoute;
import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.ApiService;
import com.bsd.migration.utils.OAuth2RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
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
public class ApiServiceImpl implements ApiService {
    @Override
    public void sync(Config sourceConfig, Config targetConfig, Map<String, GatewayRoute> sourceRoutesMap, Map<String, GatewayRoute> targetRoutesMap) {
        //源api
        List<BaseApi> sourceApis = getApisByIp(sourceConfig);
        if (sourceApis == null || sourceApis.size() == 0) {
            log.info("源主机上没有api信息");
            return;
        }
        //目标api
        List<BaseApi> targetApis = getApisByIp(targetConfig);
        try {
            synchronizeApi(sourceApis, targetApis, sourceRoutesMap, targetRoutesMap, targetConfig);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("同步api数据失败,详细原因请查看日志");
            return;
        }
        log.info("同步api数据成功");
    }

    private void synchronizeApi(List<BaseApi> sourceApis, List<BaseApi> targetApis, Map<String, GatewayRoute> sourceRoutesMap, Map<String, GatewayRoute> targetRoutesMap, Config targetConfig) throws IOException {
        List<BaseApi> insertApiList = new ArrayList<>();
        List<BaseApi> updateApiList = new ArrayList<>();
        //目标api map
        Map<String, BaseApi> targetApiMap = getApiMap(targetApis);
        //遍历,判断是否需要新增或者更新
        for (BaseApi source : sourceApis) {
            BaseApi target = targetApiMap.get(source.getApiCode());
            if (target == null) {
                if (isCanAdd(source, sourceRoutesMap, targetRoutesMap)) {
                    insertApiList.add(source);
                }
            } else {
                if (isNeedUpdate(source, target, sourceRoutesMap, targetRoutesMap)) {
                    updateApiList.add(target);
                }
            }
        }

        //添加api
        log.info("添加api开始");
        saveApis(targetConfig, insertApiList);
        log.info("添加api结束");
        //更新api
        log.info("更新api开始");
        updateApis(targetConfig, updateApiList);
        log.info("更新api结束");
    }

    private void updateApis(Config targetConfig, List<BaseApi> updateApiList) throws IOException {
        for (BaseApi api : updateApiList) {
            log.info("updateApi:{}", api);
            BaseApi updateApiDTO = new BaseApi();
            BeanUtils.copyProperties(api, updateApiDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            OAuth2RequestUtils.postReq(targetConfig, CommonConstants.UPDATE_API_URL_SUFFIX, objectMapper.writeValueAsString(updateApiDTO), new ParameterizedTypeReference<ResultBody>() {
            });
        }
    }

    private void saveApis(Config targetConfig, List<BaseApi> insertApiList) throws IOException {
        for (BaseApi api : insertApiList) {
            log.info("saveApi:{}", api);
            BaseApi addApiDTO = new BaseApi();
            BeanUtils.copyProperties(api, addApiDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            OAuth2RequestUtils.postReq(targetConfig, CommonConstants.ADD_API_URL_SUFFIX, objectMapper.writeValueAsString(addApiDTO), new ParameterizedTypeReference<ResultBody<Long>>() {
            });
        }
    }

    private boolean isCanAdd(BaseApi source, Map<String, GatewayRoute> sourceRoutesMap, Map<String, GatewayRoute> targetRoutesMap) {
        GatewayRoute sourceRoute = sourceRoutesMap.get(String.valueOf(source.getServiceId()));
        if (sourceRoute == null) {
            return false;
        }
        GatewayRoute targetRoute = targetRoutesMap.get(sourceRoute.getRouteName());
        if (targetRoute == null) {
            return false;
        }
        source.setServiceId(targetRoute.getServiceId());
        return true;
    }

    /**
     * 判断是否需要更新
     *
     * @param source
     * @param target
     * @return
     */
    private boolean isNeedUpdate(BaseApi source, BaseApi target, Map<String, GatewayRoute> sourceRoutesMap, Map<String, GatewayRoute> targetRoutesMap) {
        boolean isNeedUpdate = false;
        //判断api名称是否被修改
        if (!source.getApiName().equals(target.getApiName())) {
            target.setApiName(source.getApiName());
            isNeedUpdate = true;
        }
        //判断api描述是否被修改
        if (!source.getApiDesc().equals(target.getApiDesc())) {
            target.setApiDesc(source.getApiDesc());
            isNeedUpdate = true;
        }
        //判断状态是否改变了
        if (source.getStatus().intValue() != target.getStatus().intValue()) {
            target.setStatus(source.getStatus());
            isNeedUpdate = true;
        }
        //判断优先级是否改变了
        if (source.getPriority().intValue() != target.getPriority().intValue()) {
            target.setPriority(source.getPriority());
            isNeedUpdate = true;
        }
        //判断路由是否改变了
        GatewayRoute sourceRoute = sourceRoutesMap.get(String.valueOf(source.getServiceId()));
        GatewayRoute targetRoute = targetRoutesMap.get(String.valueOf(target.getServiceId()));
        if (sourceRoute != null && targetRoute != null) {
            if (!sourceRoute.getRouteName().equals(targetRoute.getRouteName())) {
                GatewayRoute newTargetRoute = targetRoutesMap.get(sourceRoute.getRouteName());
                if (newTargetRoute != null) {
                    target.setServiceId(newTargetRoute.getServiceId());
                    isNeedUpdate = true;
                }
            }
        }
        return isNeedUpdate;
    }

    private Map<String, BaseApi> getApiMap(List<BaseApi> sourceApis) {
        Map<String, BaseApi> map = new HashMap<>(sourceApis.size());
        for (BaseApi api : sourceApis) {
            map.put(api.getApiCode(), api);
        }
        return map;
    }

    /**
     * 获取对应服务器上Api数据
     *
     * @param config
     * @return
     */
    private List<BaseApi> getApisByIp(Config config) {
        String url = CommonConstants.GET_API_URL_SUFFIX + "?page=1&limit=3000";
        ResultBody<PageResult<BaseApi>> resultBody = OAuth2RequestUtils.getReq(config, url, new ParameterizedTypeReference<ResultBody<PageResult<BaseApi>>>() {
        });
        return resultBody.getData().getRecords();
    }
}
