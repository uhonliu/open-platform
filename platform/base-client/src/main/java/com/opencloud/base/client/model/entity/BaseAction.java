package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 系统资源-功能操作
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@TableName("base_action")
public class BaseAction extends AbstractEntity {
    private static final long serialVersionUID = 1471599074044557390L;

    /**
     * 资源ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long actionId;

    /**
     * 资源编码
     */
    private String actionCode;

    /**
     * 资源名称
     */
    private String actionName;

    /**
     * 资源父节点
     */
    private Long menuId;

    /**
     * 优先级 越小越靠前
     */
    private Integer priority;

    /**
     * 资源描述
     */
    private String actionDesc;

    /**
     * 状态:0-无效 1-有效
     */
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    private Integer isPersist;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 获取资源ID
     *
     * @return action_id - 资源ID
     */
    public Long getActionId() {
        return actionId;
    }

    /**
     * 设置资源ID
     *
     * @param actionId 资源ID
     */
    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    /**
     * 获取资源编码
     *
     * @return action_code - 资源编码
     */
    public String getActionCode() {
        return actionCode;
    }

    /**
     * 设置资源编码
     *
     * @param actionCode 资源编码
     */
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    /**
     * 获取资源名称
     *
     * @return action_name - 资源名称
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * 设置资源名称
     *
     * @param actionName 资源名称
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * 获取资源父节点
     *
     * @return menu_id - 资源父节点
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * 设置资源父节点
     *
     * @param menuId 资源父节点
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * 获取优先级 越小越靠前
     *
     * @return priority - 优先级 越小越靠前
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * 设置优先级 越小越靠前
     *
     * @param priority 优先级 越小越靠前
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPersist() {
        return isPersist;
    }

    public void setIsPersist(Integer isPersist) {
        this.isPersist = isPersist;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
