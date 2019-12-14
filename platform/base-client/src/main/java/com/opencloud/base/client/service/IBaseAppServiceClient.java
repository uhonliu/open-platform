package com.opencloud.base.client.service;

import com.opencloud.base.client.model.entity.BaseApp;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenClientDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author liuyadu
 */
public interface IBaseAppServiceClient {
    /**
     * 获取应用基础信息
     *
     * @param appId 应用Id
     * @return
     */
    @GetMapping("/app/{appId}/info")
    ResultBody<BaseApp> getApp(@PathVariable("appId") String appId);

    /**
     * 获取应用开发配置信息
     *
     * @param clientId
     * @return
     */
    @GetMapping("/app/client/{clientId}/info")
    ResultBody<OpenClientDetails> getAppClientInfo(@PathVariable("clientId") String clientId);
}
