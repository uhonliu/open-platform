package com.opencloud.base.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.entity.BaseRole;
import com.opencloud.base.client.model.entity.BaseRoleUser;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 角色管理
 *
 * @author liuyadu
 */
public interface BaseRoleService extends IBaseService<BaseRole> {
    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    IPage<BaseRole> findListPage(PageParams pageParams);

    /**
     * 查询列表
     *
     * @return
     */
    List<BaseRole> findAllList();

    /**
     * 获取角色信息
     *
     * @param roleId
     * @return
     */
    BaseRole getRole(Long roleId);

    /**
     * 添加角色
     *
     * @param role 角色
     * @return
     */
    BaseRole addRole(BaseRole role);

    /**
     * 更新角色
     *
     * @param role 角色
     * @return
     */
    BaseRole updateRole(BaseRole role);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return
     */
    void removeRole(Long roleId);

    /**
     * 检测角色编码是否存在
     *
     * @param roleCode
     * @return
     */
    Boolean isExist(String roleCode);

    /**
     * 用户添加角色
     *
     * @param userId
     * @param roles
     * @return
     */
    void saveUserRoles(Long userId, String... roles);

    /**
     * 角色添加成员
     *
     * @param roleId
     * @param userIds
     */
    void saveRoleUsers(Long roleId, String... userIds);

    /**
     * 查询角色成员
     *
     * @return
     */
    List<BaseRoleUser> findRoleUsers(Long roleId);

    /**
     * 获取角色所有授权组员数量
     *
     * @param roleId
     * @return
     */
    int getCountByRole(Long roleId);

    /**
     * 获取组员角色数量
     *
     * @param userId
     * @return
     */
    int getCountByUser(Long userId);

    /**
     * 移除角色所有组员
     *
     * @param roleId
     * @return
     */
    void removeRoleUsers(Long roleId);

    /**
     * 移除组员的所有角色
     *
     * @param userId
     * @return
     */
    void removeUserRoles(Long userId);

    /**
     * 检测是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    Boolean isExist(Long userId, Long roleId);

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    List<BaseRole> getUserRoles(Long userId);

    /**
     * 获取用户角色ID列表
     *
     * @param userId
     * @return
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 查询角色成员
     *
     * @param roleId
     * @param roleCode
     * @return
     */
    List<BaseRoleUser> findRoleUsersByRoleIdOrRoleCode(Long roleId, String roleCode);
}
