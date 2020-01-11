package com.opencloud.base.client.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.opencloud.common.mybatis.base.entity.AbstractEntity;

/**
 * 系统资源-API接口
 *
 * @author: liuyadu
 * @date: 2018/10/24 16:21
 * @description:
 */
@TableName("base_api")
public class BaseApi extends AbstractEntity {
    private static final long serialVersionUID = -9099562653030770650L;

    /**
     * 资源ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long apiId;

    /**
     * 资源编码
     */
    private String apiCode;

    /**
     * 资源名称
     */
    private String apiName;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 接口分类
     */
    private String apiCategory;
    /**
     * 资源路径
     */
    private String path;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 资源描述
     */
    private String apiDesc;

    /**
     * 状态:0-无效 1-有效
     */
    private Integer status;

    /**
     * 保留数据0-否 1-是 不允许删除
     */
    private Integer isPersist;

    /**
     * 安全认证:0-否 1-是 默认:1
     */
    private Integer isAuth;

    /**
     * 是否公开访问: 0-内部的 1-公开的
     */
    private Integer isOpen;
    /**
     * 请求方式
     */
    private String requestMethod;
    /**
     * 响应类型
     */
    private String contentType;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 获取资源ID
     *
     * @return api_id - 资源ID
     */
    public Long getApiId() {
        return apiId;
    }

    /**
     * 设置资源ID
     *
     * @param apiId 资源ID
     */
    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    /**
     * 获取资源编码
     *
     * @return api_code - 资源编码
     */
    public String getApiCode() {
        return apiCode;
    }

    /**
     * 设置资源编码
     *
     * @param apiCode 资源编码
     */
    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    /**
     * 获取资源名称
     *
     * @return api_name - 资源名称
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * 设置资源名称
     *
     * @param apiName 资源名称
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    /**
     * 获取服务ID
     *
     * @return server_id - 服务ID
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取优先级
     *
     * @return priority - 优先级
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * 设置优先级
     *
     * @param priority 优先级
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApiDesc() {
        return apiDesc;
    }

    public void setApiDesc(String apiDesc) {
        this.apiDesc = apiDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApiCategory() {
        return apiCategory;
    }

    public void setApiCategory(String apiCategory) {
        this.apiCategory = apiCategory;
    }

    public Integer getIsPersist() {
        return isPersist;
    }

    public void setIsPersist(Integer isPersist) {
        this.isPersist = isPersist;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }
}
