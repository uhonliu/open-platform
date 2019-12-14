package com.opencloud.portal.uaa.server.service.impl;

import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.model.UserAccount;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenUserDetails;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientProperties;
import com.opencloud.portal.uaa.server.service.feign.BaseDeveloperServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Security用户信息获取实现类
 *
 * @author liuyadu
 */
@Slf4j
@Service("userDetailService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private BaseDeveloperServiceClient baseDeveloperServiceClient;
    @Autowired
    private OpenOAuth2ClientProperties clientProperties;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResultBody<UserAccount> resp = baseDeveloperServiceClient.developerLogin(username);
        UserAccount account = resp.getData();
        if (account == null || account.getAccountId() == null) {
            throw new UsernameNotFoundException("系统用户 " + username + " 不存在!");
        }
        String domain = account.getDomain();
        Long accountId = account.getAccountId();
        Long userId = account.getUserId();
        String password = account.getPassword();
        String nickName = account.getNickName();
        String avatar = account.getAvatar();
        String accountType = account.getAccountType();
        boolean accountNonLocked = account.getStatus().intValue() != BaseConstants.ACCOUNT_STATUS_LOCKED;
        boolean credentialsNonExpired = true;
        boolean enabled = account.getStatus().intValue() == BaseConstants.ACCOUNT_STATUS_NORMAL ? true : false;
        boolean accountNonExpired = true;
        OpenUserDetails userDetails = new OpenUserDetails();
        userDetails.setDomain(domain);
        userDetails.setAccountId(accountId);
        userDetails.setUserId(userId);
        userDetails.setUsername(username);
        userDetails.setPassword(password);
        userDetails.setNickName(nickName);
        userDetails.setAuthorities(account.getAuthorities());
        userDetails.setAvatar(avatar);
        userDetails.setAccountId(accountId);
        userDetails.setAccountNonLocked(accountNonLocked);
        userDetails.setAccountNonExpired(accountNonExpired);
        userDetails.setAccountType(accountType);
        userDetails.setCredentialsNonExpired(credentialsNonExpired);
        userDetails.setEnabled(enabled);
        userDetails.setClientId(clientProperties.getOauth2().get("portal").getClientId());
        return userDetails;
    }
}
