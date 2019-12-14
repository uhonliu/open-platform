package com.bsd.migration.service.impl;

import com.bsd.migration.constants.CommonConstants;
import com.bsd.migration.model.dto.AddActionDTO;
import com.bsd.migration.model.dto.PageResult;
import com.bsd.migration.model.dto.UpdateActionDTO;
import com.bsd.migration.model.entity.BaseAction;
import com.bsd.migration.model.entity.BaseMenu;
import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.ActionService;
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
 * @Author: linrongxin
 * @Date: 2019/10/8 15:29
 */
@Slf4j
@Service
public class ActionServiceImpl implements ActionService {
    @Override
    public void sync(Config sourceConfig, Config targetConfig, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap) {
        //源菜单
        List<BaseAction> sourceActions = getActionsByIp(sourceConfig);
        if (sourceActions == null || sourceActions.size() == 0) {
            log.info("源主机上没有Action信息");
            return;
        }
        //目标菜单
        List<BaseAction> targetActions = getActionsByIp(targetConfig);
        try {
            synchronizeAction(sourceActions, targetActions, sourceMenusMap, targetMenusMap, targetConfig);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("同步action数据失败,详细原因请查看日志");
            return;
        }
        log.info("同步action数据成功");
    }

    private void synchronizeAction(List<BaseAction> sourceActions, List<BaseAction> targetActions, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap, Config targetConfig) throws IOException {
        List<BaseAction> insertActionList = new ArrayList<>();
        List<BaseAction> updateActionList = new ArrayList<>();
        //目标action map
        Map<String, BaseAction> targetActionMap = getActionMap(targetActions);
        //遍历,判断是否需要新增或者更新
        for (BaseAction source : sourceActions) {
            BaseAction target = targetActionMap.get(source.getActionCode());
            if (target == null) {
                if (isCanAdd(source, sourceMenusMap, targetMenusMap)) {
                    insertActionList.add(source);
                }
            } else {
                if (isNeedUpdate(source, target, sourceMenusMap, targetMenusMap)) {
                    updateActionList.add(target);
                }
            }
        }

        //添加action
        log.info("添加action开始");
        saveActions(targetConfig, insertActionList);
        log.info("添加action结束");
        //更新action
        log.info("更新action开始");
        updateActions(targetConfig, updateActionList);
        log.info("更新action结束");
    }

    private void updateActions(Config targetConfig, List<BaseAction> updateActionList) throws IOException {
        for (BaseAction action : updateActionList) {
            log.info("updateAction:{}", action);
            UpdateActionDTO updateActionDTO = new UpdateActionDTO();
            BeanUtils.copyProperties(action, updateActionDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            OAuth2RequestUtils.postReq(targetConfig, CommonConstants.UPDATE_ACTION_URL_SUFFIX, objectMapper.writeValueAsString(updateActionDTO), new ParameterizedTypeReference<ResultBody>() {
            });
        }
    }

    private void saveActions(Config targetConfig, List<BaseAction> insertActionList) throws IOException {
        for (BaseAction action : insertActionList) {
            log.info("saveAction:{}", action);
            AddActionDTO addActionDTO = new AddActionDTO();
            BeanUtils.copyProperties(action, addActionDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            OAuth2RequestUtils.postReq(targetConfig, CommonConstants.ADD_ACTION_URL_SUFFIX, objectMapper.writeValueAsString(addActionDTO), new ParameterizedTypeReference<ResultBody<Long>>() {
            });
        }
    }

    private boolean isCanAdd(BaseAction source, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap) {
        BaseMenu sourceMenu = sourceMenusMap.get(String.valueOf(source.getMenuId()));
        if (sourceMenu == null) {
            return false;
        }
        BaseMenu targetMenu = targetMenusMap.get(sourceMenu.getMenuCode());
        if (targetMenu == null) {
            return false;
        }
        source.setMenuId(targetMenu.getMenuId());
        return true;
    }

    /**
     * 判断是否需要更新
     *
     * @param source
     * @param target
     * @return
     */
    private boolean isNeedUpdate(BaseAction source, BaseAction target, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap) {
        boolean isNeedUpdate = false;
        //判断action名称是否被修改
        if (!source.getActionName().equals(target.getActionName())) {
            target.setActionName(source.getActionName());
            isNeedUpdate = true;
        }
        //判断action描述是否被修改
        if (!source.getActionDesc().equals(target.getActionDesc())) {
            target.setActionDesc(source.getActionDesc());
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
        //判断菜单是否改变了
        BaseMenu sourceMenu = sourceMenusMap.get(String.valueOf(source.getMenuId()));
        BaseMenu targetMenu = targetMenusMap.get(String.valueOf(target.getMenuId()));
        if (sourceMenu != null && targetMenu != null) {
            if (!sourceMenu.getMenuCode().equals(targetMenu.getMenuCode())) {
                BaseMenu newTargetMenu = targetMenusMap.get(sourceMenu.getMenuCode());
                if (newTargetMenu != null) {
                    target.setMenuId(newTargetMenu.getMenuId());
                    isNeedUpdate = true;
                }
            }
        }
        return isNeedUpdate;
    }

    private Map<String, BaseAction> getActionMap(List<BaseAction> sourceActions) {
        Map<String, BaseAction> map = new HashMap<>(sourceActions.size());
        for (BaseAction action : sourceActions) {
            map.put(action.getActionCode(), action);
        }
        return map;
    }

    /**
     * 获取对应服务器上Action数据
     *
     * @param config
     * @return
     */
    private List<BaseAction> getActionsByIp(Config config) {
        String url = CommonConstants.GET_ACTION_PAGE_URL_SUFFIX + "?page=1&limit=3000";
        ResultBody<PageResult<BaseAction>> resultBody = OAuth2RequestUtils.getReq(config, url, new ParameterizedTypeReference<ResultBody<PageResult<BaseAction>>>() {
        });
        return resultBody.getData().getRecords();
    }
}
