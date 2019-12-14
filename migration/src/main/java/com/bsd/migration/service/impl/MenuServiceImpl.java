package com.bsd.migration.service.impl;

import com.bsd.migration.constants.CommonConstants;
import com.bsd.migration.model.dto.AddMenuDTO;
import com.bsd.migration.model.dto.UpdateMenuDTO;
import com.bsd.migration.model.entity.BaseMenu;
import com.bsd.migration.model.resp.Config;
import com.bsd.migration.model.resp.ResultBody;
import com.bsd.migration.service.ActionService;
import com.bsd.migration.service.MenuService;
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
 * @Author: linrongxin
 * @Date: 2019/9/30 10:16
 */
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private ActionService actionService;

    @Async
    @Override
    public void sync(Config sourceConfig, Config targetConfig) {
        log.info("同步菜单数据开始");
        //源菜单
        List<BaseMenu> sourceMenus = getMenusByIp(sourceConfig);
        if (sourceMenus == null || sourceMenus.size() == 0) {
            log.info("源主机上没有菜单信息");
            return;
        }
        //目标菜单
        List<BaseMenu> targetMenus = getMenusByIp(targetConfig);
        //转成map,便于搜素
        Map<String, BaseMenu> sourceMenusMap = getMenusMap(sourceMenus);
        Map<String, BaseMenu> targetMenusMap = getMenusMap(targetMenus);
        try {
            synchronizeMenu(sourceMenus, targetMenus, targetConfig, sourceMenusMap, targetMenusMap);
            actionService.sync(sourceConfig, targetConfig, sourceMenusMap, targetMenusMap);
            log.info("同步菜单数据结束");
        } catch (IOException e) {
            log.info("同步菜单数据失败,详细原因请查看日志");
            return;
        }
        log.info("同步菜单数据成功");
    }


    /**
     * 同步菜单数据到目标服务器上
     *
     * @param sourceMenus
     * @param targetMenus
     * @param targetConfig
     */
    private void synchronizeMenu(List<BaseMenu> sourceMenus, List<BaseMenu> targetMenus, Config targetConfig, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap) throws IOException {
        //需要更新的菜单列表
        List<BaseMenu> updateMenuList = new ArrayList<BaseMenu>();
        //需要插入的菜单列表
        List<BaseMenu> insertMenuList = new ArrayList<BaseMenu>();
        for (BaseMenu sourceMenu : sourceMenus) {
            //MenuCode唯一性
            BaseMenu targetMenu = targetMenusMap.get(sourceMenu.getMenuCode());
            if (targetMenu != null) {
                if (isUpdate(sourceMenu, targetMenu, sourceMenusMap, targetMenusMap)) {
                    //需要更新的菜单
                    updateMenuList.add(targetMenu);
                }
                continue;
            }
            //新增的菜单
            insertMenuList.add(sourceMenu);
        }
        //保存新菜单
        log.info("保存新菜单开始,新增菜单数:{}", insertMenuList.size());
        saveNewMenu(insertMenuList, targetMenus, targetMenusMap, sourceMenusMap, targetConfig);
        log.info("保存新菜单结束");
        //更新旧菜单
        log.info("更新旧菜单开始,更新菜单数:{}", updateMenuList.size());
        updateOldMenu(updateMenuList, targetMenusMap, targetConfig);
        log.info("更新旧菜单结束");
    }

    /**
     * 更新菜单
     *
     * @param updateMenuList
     * @param targetMenusMap
     */
    private void updateOldMenu(List<BaseMenu> updateMenuList, Map<String, BaseMenu> targetMenusMap, Config targetConfig) throws IOException {
        int size = updateMenuList.size();
        for (BaseMenu baseMenu : updateMenuList) {
            log.info("剩余更新数:{},updateOldMenu:{}", size, baseMenu);
            if (baseMenu.getParentId().longValue() == -1L) {
                BaseMenu parentBaseMenu = targetMenusMap.get(baseMenu.getParentCode());
                baseMenu.setParentId(parentBaseMenu.getMenuId());
            }
            UpdateMenuDTO updateMenuDTO = new UpdateMenuDTO();
            BeanUtils.copyProperties(baseMenu, updateMenuDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            ResultBody resultBody = OAuth2RequestUtils.postReq(targetConfig, CommonConstants.UPDATE_MENU_URL_SUFFIX, objectMapper.writeValueAsString(updateMenuDTO), new ParameterizedTypeReference<ResultBody>() {
            });
            if (resultBody != null && resultBody.getCode() == 0) {
                //更新成功
                targetMenusMap.put(baseMenu.getMenuCode(), baseMenu);
                targetMenusMap.put(String.valueOf(baseMenu.getMenuId()), baseMenu);
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
     * 保存菜单数据
     *
     * @param insertMenuList
     * @param targetMenus
     * @param targetMenusMap
     * @param targetConfig
     * @throws IOException
     */
    private void saveNewMenu(List<BaseMenu> insertMenuList, List<BaseMenu> targetMenus, Map<String, BaseMenu> targetMenusMap, Map<String, BaseMenu> sourceMenusMap, Config targetConfig) throws IOException {
        handleBeforInsert(insertMenuList, targetMenusMap, sourceMenusMap);
        //插入数据
        for (BaseMenu baseMenu : insertMenuList) {
            log.info("saveNewMenu:{}", baseMenu);
            AddMenuDTO addMenuDTO = new AddMenuDTO();
            BeanUtils.copyProperties(baseMenu, addMenuDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            ResultBody<Long> resultBody = OAuth2RequestUtils.postReq(targetConfig, CommonConstants.ADD_MENU_URL_SUFFIX, objectMapper.writeValueAsString(addMenuDTO), new ParameterizedTypeReference<ResultBody<Long>>() {
            });
            baseMenu.setMenuId(resultBody.getData());
            targetMenus.add(baseMenu);
            targetMenusMap.put(baseMenu.getMenuCode(), baseMenu);
            targetMenusMap.put(String.valueOf(baseMenu.getMenuId()), baseMenu);
        }
        //更新parentId
        for (BaseMenu baseMenu : insertMenuList) {
            if (baseMenu.getParentId().longValue() == -1L) {
                log.info("saveNewMenu update:{}", baseMenu);
                BaseMenu targetParentMenu = targetMenusMap.get(baseMenu.getMenuCode());
                baseMenu.setParentId(targetParentMenu.getMenuId());
                UpdateMenuDTO updateMenuDTO = new UpdateMenuDTO();
                BeanUtils.copyProperties(baseMenu, updateMenuDTO);
                ObjectMapper objectMapper = new ObjectMapper();
                ResultBody resultBody = OAuth2RequestUtils.postReq(targetConfig, CommonConstants.UPDATE_MENU_URL_SUFFIX, objectMapper.writeValueAsString(updateMenuDTO), new ParameterizedTypeReference<ResultBody>() {
                });
                if (resultBody != null && resultBody.getCode() == 0) {
                    //更新成功
                    targetMenusMap.put(baseMenu.getMenuCode(), baseMenu);
                    targetMenusMap.put(String.valueOf(baseMenu.getMenuId()), baseMenu);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleBeforInsert(List<BaseMenu> insertMenuList, Map<String, BaseMenu> targetMenusMap, Map<String, BaseMenu> sourceMenusMap) {
        for (BaseMenu baseMenu : insertMenuList) {
            Long parentId = baseMenu.getParentId();
            if (parentId.longValue() != 0L) {
                BaseMenu parentBaseMenu = sourceMenusMap.get(String.valueOf(parentId));
                if (parentBaseMenu == null) {
                    baseMenu.setParentId(0L);
                    continue;
                }
                BaseMenu targetParentMenu = targetMenusMap.get(parentBaseMenu.getMenuCode());
                if (targetParentMenu == null) {
                    baseMenu.setParentId(-1L);
                    baseMenu.setParentCode(parentBaseMenu.getMenuCode());
                } else {
                    baseMenu.setParentId(targetParentMenu.getMenuId());
                }
            }
        }
    }

    /**
     * 判断是否需要更新
     *
     * @param sourceMenu
     * @param targetMenu
     * @return
     */
    private boolean isUpdate(BaseMenu sourceMenu, BaseMenu targetMenu, Map<String, BaseMenu> sourceMenusMap, Map<String, BaseMenu> targetMenusMap) {
        boolean isUpdate = false;
        BaseMenu tempTargetMenu = new BaseMenu();
        BeanUtils.copyProperties(targetMenu, tempTargetMenu);
        //菜单名称
        if (!sourceMenu.getMenuName().equals(targetMenu.getMenuName())) {
            targetMenu.setMenuName(sourceMenu.getMenuName());
            log.info("菜单名称改变了");
            isUpdate = true;
        }
        //菜单状态
        if (sourceMenu.getStatus().intValue() != targetMenu.getStatus().intValue()) {
            targetMenu.setStatus(sourceMenu.getStatus());
            log.info("菜单状态改变了");
            isUpdate = true;
        }
        //菜单图标
        if (!sourceMenu.getIcon().equals(targetMenu.getIcon())) {
            targetMenu.setIcon(sourceMenu.getIcon());
            log.info("菜单图标改变了");
            isUpdate = true;
        }
        //菜单描述
        if (!sourceMenu.getMenuDesc().equals(targetMenu.getMenuDesc())) {
            targetMenu.setMenuDesc(sourceMenu.getMenuDesc());
            log.info("菜单描述改变了");
            isUpdate = true;
        }
        //父级ID
        if (sourceMenu.getParentId().longValue() == 0L && targetMenu.getParentId().longValue() == 0L) {
            //nothing to do
        } else if (sourceMenu.getParentId().longValue() == 0L && targetMenu.getParentId().longValue() != 0L) {
            log.info("父级ID改成0");
            targetMenu.setParentId(0L);
            isUpdate = true;
        } else {
            BaseMenu sourceParentMenu = sourceMenusMap.get(String.valueOf(sourceMenu.getParentId()));
            if (sourceParentMenu == null) {
                //数据有误,查找不到父级菜单数据,不处理
                return false;
            }
            String sourceParentMenuCode = sourceParentMenu.getMenuCode();
            BaseMenu targetParentMenu = targetMenusMap.get(String.valueOf(targetMenu.getParentId()));
            if (targetParentMenu == null) {
                log.info("父级ID未知");
                targetMenu.setParentId(-1L);
                targetMenu.setParentCode(sourceParentMenuCode);
                isUpdate = true;
            } else {
                if (!sourceParentMenuCode.equals(targetParentMenu.getMenuCode())) {
                    BaseMenu targetNewParentMenu = targetMenusMap.get(sourceParentMenuCode);
                    if (targetNewParentMenu == null) {
                        log.info("父级ID未知");
                        targetMenu.setParentId(-1L);
                        targetMenu.setParentCode(sourceParentMenuCode);
                    } else {
                        log.info("父级ID改成:{}", targetNewParentMenu.getMenuId());
                        targetMenu.setParentId(targetNewParentMenu.getMenuId());
                    }
                    isUpdate = true;
                }
            }
        }
        //请求路径
        if (!sourceMenu.getPath().equals(targetMenu.getPath())) {
            targetMenu.setPath(sourceMenu.getPath());
            log.info("请求路径改变了");
            isUpdate = true;
        }
        //优先级
        if (sourceMenu.getPriority().intValue() != targetMenu.getPriority().intValue()) {
            targetMenu.setPriority(sourceMenu.getPriority());
            log.info("优先级改变了");
            isUpdate = true;
        }
        //请求协议,可用值:/,http://,https://
        if (!sourceMenu.getScheme().equals(targetMenu.getScheme())) {
            log.info("请求协议改变了");
            targetMenu.setScheme(sourceMenu.getScheme());
            isUpdate = true;
        }
        //前端应用
        if (!sourceMenu.getServiceId().equals(targetMenu.getServiceId())) {
            targetMenu.setServiceId(sourceMenu.getServiceId());
            log.info("前端应用改变了");
            isUpdate = true;
        }
        //请求路径,可用值:_self,_blank
        if (!sourceMenu.getTarget().equals(targetMenu.getTarget())) {
            log.info("请求路径改变了");
            targetMenu.setTarget(sourceMenu.getTarget());
            isUpdate = true;
        }
        if (isUpdate) {
            log.info("isUpdate-targetMenu:{}", tempTargetMenu);
            log.info("isUpdate-sourceMenu:{}", sourceMenu);
        }
        return isUpdate;
    }

    /**
     * list 转 map
     *
     * @param targetMenus
     * @return
     */
    private Map<String, BaseMenu> getMenusMap(List<BaseMenu> targetMenus) {
        Map<String, BaseMenu> targetMenusMap = new HashMap<String, BaseMenu>(targetMenus.size());
        for (BaseMenu targetMenu : targetMenus) {
            targetMenusMap.put(targetMenu.getMenuCode(), targetMenu);
            targetMenusMap.put(String.valueOf(targetMenu.getMenuId()), targetMenu);
        }
        return targetMenusMap;
    }

    /**
     * 远程获取菜单信息
     *
     * @param config
     * @return
     */
    private List<BaseMenu> getMenusByIp(Config config) {
        ResultBody<List<BaseMenu>> resultBody = OAuth2RequestUtils.getReq(config, CommonConstants.GET_ALL_MENU_URL_SUFFIX, new ParameterizedTypeReference<ResultBody<List<BaseMenu>>>() {
        });
        return resultBody.getData();
    }

    public static void main(String[] args) {
        MenuServiceImpl service = new MenuServiceImpl();
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
