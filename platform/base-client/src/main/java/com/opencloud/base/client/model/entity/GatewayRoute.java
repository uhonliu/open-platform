package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 网关动态路由
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@TableName("gateway_route")
public class GatewayRoute extends AbstractEntity {
    private static final long serialVersionUID = -2952097064941740301L;

    /**
     * 路由ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long routeId;

    /**
     * 路由名称
     */
    private String routeName;

    /**
     * 路由类型:service-负载均衡 url-反向代理
     */
    private String routeType;

    /**
     * 路径
     */
    private String path;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 完整地址
     */
    private String url;

    /**
     * 忽略前缀
     */
    private Integer stripPrefix;

    /**
     * 0-不重试 1-重试
     */
    private Integer retryable;

    /**
     * 状态:0-无效 1-有效
     */
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    private Integer isPersist;

    /**
     * 路由说明
     */
    private String routeDesc;

    /**
     * 获取路径
     *
     * @return path - 路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置路径
     *
     * @param path 路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取服务ID
     *
     * @return service_id - 服务ID
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * 设置服务ID
     *
     * @param serviceId 服务ID
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * 获取完整地址
     *
     * @return url - 完整地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置完整地址
     *
     * @param url 完整地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取忽略前缀
     *
     * @return strip_prefix - 忽略前缀
     */
    public Integer getStripPrefix() {
        return stripPrefix;
    }

    /**
     * 设置忽略前缀
     *
     * @param stripPrefix 忽略前缀
     */
    public void setStripPrefix(Integer stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    /**
     * 获取0-不重试 1-重试
     *
     * @return retryable - 0-不重试 1-重试
     */
    public Integer getRetryable() {
        return retryable;
    }

    /**
     * 设置0-不重试 1-重试
     *
     * @param retryable 0-不重试 1-重试
     */
    public void setRetryable(Integer retryable) {
        this.retryable = retryable;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Integer getIsPersist() {
        return isPersist;
    }

    public void setIsPersist(Integer isPersist) {
        this.isPersist = isPersist;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }
}
