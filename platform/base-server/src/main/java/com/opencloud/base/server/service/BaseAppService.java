package com.opencloud.base.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.opencloud.base.client.model.entity.BaseApp;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.IBaseService;
import com.opencloud.common.security.OpenClientDetails;

/**
 * 应用信息管理
 *
 * @author liuyadu
 */
public interface BaseAppService extends IBaseService<BaseApp> {
    /**
     * 查询应用列表
     *
     * @param pageParams
     * @return
     */
    IPage<BaseApp> findListPage(PageParams pageParams);

    /**
     * 获取app信息
     *
     * @param appId
     * @return
     */
    BaseApp getAppInfo(String appId);

    /**
     * 获取app和应用信息
     *
     * @param clientId
     * @return
     */
    OpenClientDetails getAppClientInfo(String clientId);


    /**
     * 更新应用开发新型
     *
     * @param client
     */
    void updateAppClientInfo(OpenClientDetails client);

    /**
     * 添加应用
     *
     * @param app 应用
     * @return 应用信息
     */
    BaseApp addAppInfo(BaseApp app);

    /**
     * 修改应用
     *
     * @param app 应用
     * @return 应用信息
     */
    BaseApp updateInfo(BaseApp app);


    /**
     * 重置秘钥
     *
     * @param appId
     * @return
     */
    String restSecret(String appId);

    /**
     * 删除应用
     *
     * @param appId
     * @return
     */
    void removeApp(String appId);
}
