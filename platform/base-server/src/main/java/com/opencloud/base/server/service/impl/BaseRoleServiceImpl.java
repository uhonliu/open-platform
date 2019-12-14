package com.opencloud.base.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.model.entity.BaseRole;
import com.opencloud.base.client.model.entity.BaseRoleUser;
import com.opencloud.base.client.model.entity.BaseUser;
import com.opencloud.base.server.mapper.BaseRoleMapper;
import com.opencloud.base.server.mapper.BaseRoleUserMapper;
import com.opencloud.base.server.service.BaseRoleService;
import com.opencloud.base.server.service.BaseUserService;
import com.opencloud.common.constants.CommonConstants;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author liuyadu
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseRoleServiceImpl extends BaseServiceImpl<BaseRoleMapper, BaseRole> implements BaseRoleService {
    @Autowired
    private BaseRoleMapper baseRoleMapper;
    @Autowired
    private BaseRoleUserMapper baseRoleUserMapper;
    @Autowired
    private BaseUserService baseUserService;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<BaseRole> findListPage(PageParams pageParams) {
        BaseRole query = pageParams.mapToObject(BaseRole.class);
        QueryWrapper<BaseRole> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleCode()), BaseRole::getRoleCode, query.getRoleCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getRoleName()), BaseRole::getRoleName, query.getRoleName());
        queryWrapper.orderByDesc("create_time");
        return baseRoleMapper.selectPage(pageParams, queryWrapper);
    }

    /**
     * 查询列表
     *
     * @return
     */
    @Override
    public List<BaseRole> findAllList() {
        List<BaseRole> list = baseRoleMapper.selectList(new QueryWrapper<>());
        return list;
    }

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    @Override
    public BaseRole getRole(Long roleId) {
        return baseRoleMapper.selectById(roleId);
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return
     */
    @Override
    public BaseRole addRole(BaseRole role) {
        if (isExist(role.getRoleCode())) {
            throw new OpenAlertException(String.format("%s编码已存在!", role.getRoleCode()));
        }
        if (role.getStatus() == null) {
            role.setStatus(BaseConstants.ENABLED);
        }
        if (role.getIsPersist() == null) {
            role.setIsPersist(BaseConstants.DISABLED);
        }
        role.setCreateTime(new Date());
        role.setUpdateTime(role.getCreateTime());
        baseRoleMapper.insert(role);
        return role;
    }

    /**
     * 更新角色
     *
     * @param role 角色
     * @return
     */
    @Override
    public BaseRole updateRole(BaseRole role) {
        BaseRole saved = getRole(role.getRoleId());
        if (role == null) {
            throw new OpenAlertException("信息不存在!");
        }
        if (!saved.getRoleCode().equals(role.getRoleCode())) {
            // 和原来不一致重新检查唯一性
            if (isExist(role.getRoleCode())) {
                throw new OpenAlertException(String.format("%s编码已存在!", role.getRoleCode()));
            }
        }
        role.setUpdateTime(new Date());
        baseRoleMapper.updateById(role);
        return role;
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return
     */
    @Override
    public void removeRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        BaseRole role = getRole(roleId);
        if (role != null && role.getIsPersist().equals(BaseConstants.ENABLED)) {
            throw new OpenAlertException(String.format("保留数据,不允许删除"));
        }
        int count = getCountByRole(roleId);
        if (count > 0) {
            throw new OpenAlertException("该角色下存在授权人员,不允许删除!");
        }
        baseRoleMapper.deleteById(roleId);
    }

    /**
     * 检测角色编码是否存在
     *
     * @param roleCode
     * @return
     */
    @Override
    public Boolean isExist(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            throw new OpenAlertException("roleCode不能为空!");
        }
        QueryWrapper<BaseRole> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRole::getRoleCode, roleCode);
        return baseRoleMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 用户添加角色
     *
     * @param userId
     * @param roles
     * @return
     */
    @Override
    public void saveUserRoles(Long userId, String... roles) {
        if (userId == null || roles == null) {
            return;
        }
        BaseUser user = baseUserService.getUserById(userId);
        if (user == null) {
            return;
        }
        if (CommonConstants.ROOT.equals(user.getUserName())) {
            throw new OpenAlertException("默认用户无需分配!");
        }
        // 先清空,在添加
        removeUserRoles(userId);
        if (roles.length > 0) {
            for (String roleId : roles) {
                BaseRoleUser roleUser = new BaseRoleUser();
                roleUser.setUserId(userId);
                roleUser.setRoleId(Long.parseLong(roleId));
                baseRoleUserMapper.insert(roleUser);
            }
            // 批量保存
        }
    }

    /**
     * 角色添加成员
     *
     * @param roleId
     * @param userIds
     */
    @Override
    public void saveRoleUsers(Long roleId, String... userIds) {
        if (roleId == null || userIds == null) {
            return;
        }
        // 先清空,在添加
        removeRoleUsers(roleId);
        if (userIds.length > 0) {
            for (String userId : userIds) {
                BaseRoleUser roleUser = new BaseRoleUser();
                roleUser.setUserId(Long.parseLong(userId));
                roleUser.setRoleId(roleId);
                baseRoleUserMapper.insert(roleUser);
            }
            // 批量保存
        }
    }

    /**
     * 查询角色成员
     *
     * @return
     */
    @Override
    public List<BaseRoleUser> findRoleUsers(Long roleId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getRoleId, roleId);
        return baseRoleUserMapper.selectList(queryWrapper);
    }

    /**
     * 查询角色成员
     *
     * @param roleId
     * @param roleCode
     * @return
     */
    @Override
    public List<BaseRoleUser> findRoleUsersByRoleIdOrRoleCode(Long roleId, String roleCode) {
        //设置查询条件
        QueryWrapper<BaseRole> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(StringUtils.isNotEmpty(roleCode), BaseRole::getRoleCode, roleCode);
        queryWrapper.lambda().eq(roleId != null, BaseRole::getRoleId, roleId);
        //查询角色信息
        BaseRole baseRole = baseRoleMapper.selectOne(queryWrapper);
        if (baseRole == null) {
            //角色不存在,直接返回
            return null;
        }
        //角色存在,查询角色下的用户列表
        return findRoleUsers(baseRole.getRoleId());
    }


    /**
     * 获取角色所有授权组员数量
     *
     * @param roleId
     * @return
     */
    @Override
    public int getCountByRole(Long roleId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getRoleId, roleId);
        int result = baseRoleUserMapper.selectCount(queryWrapper);
        return result;
    }

    /**
     * 获取组员角色数量
     *
     * @param userId
     * @return
     */
    @Override
    public int getCountByUser(Long userId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getUserId, userId);
        int result = baseRoleUserMapper.selectCount(queryWrapper);
        return result;
    }

    /**
     * 移除角色所有组员
     *
     * @param roleId
     * @return
     */
    @Override
    public void removeRoleUsers(Long roleId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getRoleId, roleId);
        baseRoleUserMapper.delete(queryWrapper);
    }

    /**
     * 移除组员的所有角色
     *
     * @param userId
     * @return
     */
    @Override
    public void removeUserRoles(Long userId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getUserId, userId);
        baseRoleUserMapper.delete(queryWrapper);
    }

    /**
     * 检测是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    @Override
    public Boolean isExist(Long userId, Long roleId) {
        QueryWrapper<BaseRoleUser> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BaseRoleUser::getRoleId, roleId);
        queryWrapper.lambda().eq(BaseRoleUser::getUserId, userId);
        baseRoleUserMapper.delete(queryWrapper);
        int result = baseRoleUserMapper.selectCount(queryWrapper);
        return result > 0;
    }


    /**
     * 获取组员角色
     *
     * @param userId
     * @return
     */
    @Override
    public List<BaseRole> getUserRoles(Long userId) {
        List<BaseRole> roles = baseRoleUserMapper.selectRoleUserList(userId);
        return roles;
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return baseRoleUserMapper.selectRoleUserIdList(userId);
    }
}
