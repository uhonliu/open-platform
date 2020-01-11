package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 系统权限-菜单权限、操作权限、API权限
 *
 * @author liuyadu
 */
@TableName("base_authority")
public class BaseAuthority extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 菜单资源ID
     */
    private Long menuId;

    /**
     * API资源ID
     */
    private Long apiId;

    /**
     * 操作资源ID
     */
    private Long actionId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * @return authority_id
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    /**
     * @param authorityId
     */
    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * 获取权限标识
     *
     * @return authority - 权限标识
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * 设置权限标识
     *
     * @param authority 权限标识
     */
    public void setAuthority(String authority) {
        this.authority = authority == null ? null : authority.trim();
    }

    /**
     * 获取菜单资源ID
     *
     * @return menu_id - 菜单资源ID
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * 设置菜单资源ID
     *
     * @param menuId 菜单资源ID
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * 获取API资源ID
     *
     * @return api_id - API资源ID
     */
    public Long getApiId() {
        return apiId;
    }

    /**
     * 设置API资源ID
     *
     * @param apiId API资源ID
     */
    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
