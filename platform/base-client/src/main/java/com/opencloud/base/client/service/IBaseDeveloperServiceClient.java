package com.opencloud.base.client.service;

import com.opencloud.base.client.model.UserAccount;
import com.opencloud.common.model.ResultBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author liuyadu
 */
public interface IBaseDeveloperServiceClient {
    /**
     * 开发者登录
     *
     * @param username
     * @return
     */
    @PostMapping("/developer/login")
    ResultBody<UserAccount> developerLogin(@RequestParam(value = "username") String username);


    /**
     * 注册第三方系统登录账号
     *
     * @param account
     * @param password
     * @param accountType
     * @return
     */
    @PostMapping("/developer/register/thirdParty")
    ResultBody addDeveloperThirdParty(
            @RequestParam(value = "account") String account,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "accountType") String accountType,
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "avatar") String avatar
    );
}
