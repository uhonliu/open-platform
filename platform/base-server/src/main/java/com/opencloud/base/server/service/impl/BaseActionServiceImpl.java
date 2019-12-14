package com.opencloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.constants.ResourceType;
import com.opencloud.base.client.model.entity.BaseAction;
import com.opencloud.base.server.mapper.BaseActionMapper;
import com.opencloud.base.server.service.BaseActionService;
import com.opencloud.base.server.service.BaseAuthorityService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class BaseActionServiceImpl extends BaseServiceImpl<BaseActionMapper, BaseAction> implements BaseActionService {
    @Autowired
    private BaseActionMapper baseActionMapper;

    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Value("${spring.application.name}")
    private String DEFAULT_SERVICE_ID;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<BaseAction> findListPage(PageParams pageParams) {
        BaseAction query = pageParams.mapToObject(BaseAction.class);
        QueryWrapper<BaseAction> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getActionCode()), BaseAction::getActionCode, query.getActionCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getActionName()), BaseAction::getActionName, query.getActionName());
        queryWrapper.orderByDesc("create_time");
        return baseActionMapper.selectPage(new Page(pageParams.getPage(), pageParams.getLimit()), queryWrapper);
    }

    /**
     * 查询菜单下所有操作
     *
     * @param menuId
     * @return
     */
    @Override
    public List<BaseAction> findListByMenuId(Long menuId) {
        QueryWrapper<BaseAction> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseAction::getMenuId, menuId);
        List<BaseAction> list = baseActionMapper.selectList(queryWrapper);
        //根据优先级从小到大排序
        list.sort((BaseAction h1, BaseAction h2) -> h1.getPriority().compareTo(h2.getPriority()));
        return list;
    }

    /**
     * 根据主键获取Action
     *
     * @param actionId
     * @return
     */
    @Override
    public BaseAction getAction(Long actionId) {
        return baseActionMapper.selectById(actionId);
    }


    /**
     * 检查Action编码是否存在
     *
     * @param acitonCode
     * @return
     */
    @Override
    public Boolean isExist(String acitonCode) {
        QueryWrapper<BaseAction> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(BaseAction::getActionCode, acitonCode);
        int count = baseActionMapper.selectCount(queryWrapper);
        return count > 0 ? true : false;
    }

    /**
     * 添加Action操作
     *
     * @param aciton
     * @return
     */
    @Override
    public BaseAction addAction(BaseAction aciton) {
        if (isExist(aciton.getActionCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", aciton.getActionCode()));
        }
        if (aciton.getMenuId() == null) {
            aciton.setMenuId(0L);
        }
        if (aciton.getPriority() == null) {
            aciton.setPriority(0);
        }
        if (aciton.getStatus() == null) {
            aciton.setStatus(BaseConstants.ENABLED);
        }
        if (aciton.getIsPersist() == null) {
            aciton.setIsPersist(BaseConstants.DISABLED);
        }
        aciton.setCreateTime(new Date());
        aciton.setServiceId(DEFAULT_SERVICE_ID);
        aciton.setUpdateTime(aciton.getCreateTime());
        baseActionMapper.insert(aciton);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(aciton.getActionId(), ResourceType.action);
        return aciton;
    }

    /**
     * 修改Action操作
     *
     * @param aciton
     * @return
     */
    @Override
    public BaseAction updateAction(BaseAction aciton) {
        BaseAction saved = getAction(aciton.getActionId());
        if (saved == null) {
            throw new OpenAlertException(String.format("%s信息不存在", aciton.getActionId()));
        }
        if (!saved.getActionCode().equals(aciton.getActionCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(aciton.getActionCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", aciton.getActionCode()));
            }
        }
        if (aciton.getMenuId() == null) {
            aciton.setMenuId(0L);
        }
        if (aciton.getPriority() == null) {
            aciton.setPriority(0);
        }
        aciton.setUpdateTime(new Date());
        baseActionMapper.updateById(aciton);
        // 同步权限表里的信息
        baseAuthorityService.saveOrUpdateAuthority(aciton.getActionId(), ResourceType.action);
        return aciton;
    }

    /**
     * 移除Action
     *
     * @param actionId
     * @return
     */
    @Override
    public void removeAction(Long actionId) {
        BaseAction aciton = getAction(actionId);
        if (aciton != null && aciton.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除"));
        }
        baseAuthorityService.removeAuthorityAction(actionId);
        baseAuthorityService.removeAuthority(actionId, ResourceType.action);
        baseActionMapper.deleteById(actionId);
    }

    /**
     * 移除菜单相关资源
     *
     * @param menuId
     */
    @Override
    public void removeByMenuId(Long menuId) {
        List<BaseAction> actionList = findListByMenuId(menuId);
        if (actionList != null && actionList.size() > 0) {
            for (BaseAction action : actionList) {
                removeAction(action.getActionId());
            }
        }
    }
}
