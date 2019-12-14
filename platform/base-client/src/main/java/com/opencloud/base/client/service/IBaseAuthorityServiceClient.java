package com.opencloud.base.client.service;

import com.opencloud.base.client.model.AuthorityMenu;
import com.opencloud.base.client.model.AuthorityResource;
import com.opencloud.common.model.ResultBody;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 权限控制API接口
 *
 * @author liuyadu
 */
public interface IBaseAuthorityServiceClient {
    /**
     * 获取所有访问权限列表
     *
     * @return
     */
    @GetMapping("/authority/access")
    ResultBody<List<AuthorityResource>> findAuthorityResource();

    /**
     * 获取菜单权限列表
     *
     * @return
     */
    @GetMapping("/authority/menu")
    ResultBody<List<AuthorityMenu>> findAuthorityMenu();
}
