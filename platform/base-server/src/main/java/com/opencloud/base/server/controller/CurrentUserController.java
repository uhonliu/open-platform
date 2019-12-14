package com.opencloud.base.server.controller;

import com.opencloud.base.client.model.AuthorityMenu;
import com.opencloud.base.client.model.entity.BaseUser;
import com.opencloud.base.server.service.BaseAppService;
import com.opencloud.base.server.service.BaseAuthorityService;
import com.opencloud.base.server.service.BaseUserService;
import com.opencloud.common.constants.CommonConstants;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import com.opencloud.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: liuyadu
 * @date: 2019/5/24 13:31
 * @description:
 */
@Api(tags = "当前登陆用户")
@RestController
public class CurrentUserController {
    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private BaseAuthorityService baseAuthorityService;
    @Autowired
    private BaseAppService baseAppService;
    @Autowired
    private RedisTokenStore redisTokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 修改当前登录用户密码
     *
     * @return
     */
    @ApiOperation(value = "修改当前登录用户密码", notes = "修改当前登录用户密码")
    @PostMapping("/current/user/rest/password")
    public ResultBody restPassword(
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "confirmPassword") String confirmPassword
    ) {
        OpenUserDetails user = OpenHelper.getUser();
        Assert.notNull(user, "登录过期，请重新登录");
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword) || !confirmPassword.equals(password)) {
            throw new OpenAlertException("新密码与确认密码不一致");
        }
        if (StringUtils.isBlank(oldPassword) || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new OpenAlertException("旧密码输入错误");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new OpenAlertException("新密码与旧密码不能相同");
        }
        return baseUserService.updatePassword(user.getUserId(), password) ? ResultBody.ok().msg("修改密码成功") : ResultBody.failed().msg("修改密码失败");
    }

    /**
     * 修改当前登录用户基本信息
     *
     * @param nickName
     * @param userDesc
     * @param avatar
     * @return
     */
    @ApiOperation(value = "修改当前登录用户基本信息", notes = "修改当前登录用户基本信息")
    @PostMapping("/current/user/update")
    public ResultBody updateUserInfo(
            @RequestParam(value = "nickName") String nickName,
            @RequestParam(value = "userDesc", required = false) String userDesc,
            @RequestParam(value = "avatar", required = false) String avatar
    ) {
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        Assert.notNull(openUserDetails, "登录过期，请重新登录");
        BaseUser user = new BaseUser();
        user.setUserId(openUserDetails.getUserId());
        user.setNickName(nickName);
        if (userDesc != null && !"".equals(userDesc)) {
            user.setUserDesc(userDesc);
        }
        if (avatar != null && !"".equals(avatar)) {
            user.setAvatar(avatar);
        }
        baseUserService.updateUser(user);
        openUserDetails.setNickName(nickName);
        openUserDetails.setAvatar(avatar);
        OpenHelper.updateOpenUser(redisTokenStore, openUserDetails);
        return ResultBody.ok();
    }

    /**
     * 获取登陆用户已分配权限
     *
     * @return
     */
    @ApiOperation(value = "获取当前登录用户已分配菜单权限", notes = "获取当前登录用户已分配菜单权限")
    @GetMapping("/current/user/menu")
    public ResultBody<List<AuthorityMenu>> findAuthorityMenu(@RequestParam(value = "serviceId") String serviceId) {
        OpenUserDetails user = OpenHelper.getUser();
        Assert.notNull(user, "登录过期，请重新登录");
        if (serviceId == null || "".equals(serviceId)) {
            // modify, add search menu with serviceId(appNameEn)
            serviceId = baseAppService.getAppClientInfo(user.getClientId()).getAdditionalInformation().get("appNameEn").toString();
        }
        serviceId = serviceId.trim();
        List<AuthorityMenu> result = baseAuthorityService.findAuthorityMenuByUser(user.getUserId(), CommonConstants.ROOT.equals(user.getUsername()), serviceId);
        return ResultBody.ok().data(result);
    }
}
