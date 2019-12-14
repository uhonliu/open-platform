package com.bsd.user.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.mapper.UserAccountMapper;
import com.bsd.user.server.mapper.UserLoginLogsMapper;
import com.bsd.user.server.mapper.UserMapper;
import com.bsd.user.server.model.UserAccountPo;
import com.bsd.user.server.model.UserLoginLogsPo;
import com.bsd.user.server.model.UserPo;
import com.bsd.user.server.model.entity.User;
import com.bsd.user.server.model.entity.UserAccount;
import com.bsd.user.server.service.UserAccountService;
import com.bsd.user.server.service.UserService;
import com.bsd.user.server.service.WechatAuthService;
import com.bsd.user.server.utils.JwtTokenUtils;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.security.oauth2.client.OpenOAuth2Service;
import com.opencloud.common.utils.RandomValueUtils;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户-第三方账号 服务实现类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserAccountServiceImpl extends BaseServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserLoginLogsMapper userLoginLogsMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    @Qualifier("qqAuthServiceImpl")
    private OpenOAuth2Service qqAuthService;

    @Autowired
    private Map<String, WechatAuthService> wechatAuthServiceMap;

    /**
     * 获取第三方用户信息
     *
     * @param code
     * @param service
     * @return
     */
    private JSONObject getOpentUserInfo(String code, OpenOAuth2Service service) {
        // 通过code获取access_token
        String accessToken = service.getAccessToken(code);
        if (accessToken == null) {
            throw new OpenAlertException("code错误，请使用正确的code");
        }
        // 根据access_token获取openId
        String openId = service.getOpenId(accessToken);
        if (StringUtils.isEmpty(openId)) {
            throw new OpenAlertException("未获取正确的用户信息，请核对信息稍后重试");
        }
        // 根据openId获取qq用户信息
        JSONObject userInfoJson = service.getUserInfo(accessToken, openId);
        return userInfoJson;
    }

    @Override
    public Map<String, Object> isBindingsByQq(String code) {
        Map<String, Object> result = new HashMap<String, Object>();
        //通过code获取access_token
        String accessToken = qqAuthService.getAccessToken(code);
        if (accessToken == null) {
            throw new OpenAlertException("code错误，请使用正确的code");
        }
        //根据access_token获取openId
        String openId = qqAuthService.getOpenId(accessToken);
        if (StringUtils.isEmpty(openId)) {
            throw new OpenAlertException("未获取正确的用户信息，请核对信息稍后重试");
        }
        //根据openId获取qq用户信息
        JSONObject userInfoJson = qqAuthService.getUserInfo(accessToken, openId);
        //根据openId获取用户中心第三方账号用户信息
        UserAccount userAccout = null;
        if (userAccout == null) {
            //首次登陆，创建第三方账号信息
            UserAccountPo insertPo = new UserAccountPo(null, UserConstants.PLATFORM_QQ, null, openId,
                    null, userInfoJson.getString("figureurl"), userInfoJson.getString("nickname"), 0,
                    "language", "city", "province", "country", "countryCode", "mobile", null, null);
            userAccountMapper.insert(insertPo);
            result.put("code", -1);
            result.put("message", "未绑定用户中心账号，请绑定");
        } else {
            //非首次登陆，更新第三方账号信息
            UserAccountPo insertPo = new UserAccountPo(userAccout.getAccountId(), UserConstants.PLATFORM_QQ, userAccout.getUserId(), openId,
                    null, userInfoJson.getString("figureurl"), userInfoJson.getString("nickname"), 0, "language", "city", "province", "country",
                    "countryCode", "mobile", null, null);
            userAccountMapper.updateById(insertPo);
            if (userAccout.getUserId() == null) {
                result.put("code", -2);
                result.put("message", "未绑定用户中心账号，请绑定");
            } else {
                result.put("code", 0);
                result.put("message", "已绑定用户中心账号");
            }
        }
        if (!redisUtils.set(UserConstants.THIRD_LOGIN_CODE_PRE + UserConstants.PLATFORM_QQ + "-" + code, openId, UserConstants.THIRD_LOGIN_CODE_TIME)) {
            throw new OpenAlertException("系统异常，请稍后重试");
        }
        return result;
    }

    @Override
    public boolean isBindingsByWechat(String code, int platform) {
        //根据platform获取服务
        WechatAuthService service = wechatAuthServiceMap.entrySet().stream().filter(x -> x.getValue().getPlatform() == platform).findFirst().get().getValue();
        if (service == null) {
            throw new OpenAlertException("未找到对应的微信授权服务");
        }
        boolean isBinding = false;
        JSONObject accessTokenReslt = null;
        String openId = "";
        String unionid = "";
        UserAccountPo insertPo = null;
        if (UserConstants.PLATFORM_MINIPROGRAM.equals(platform)) {
            //微信小程序
            accessTokenReslt = service.authCode2Session(code);
            log.info("通过code获取authCode2Session:{}", (accessTokenReslt == null ? null : accessTokenReslt.toString()));
            if (accessTokenReslt == null) {
                throw new OpenAlertException("code错误，请使用正确的code");
            }
            openId = accessTokenReslt.getString(UserConstants.WECHAT_ACCESSTOKEN_OPENID_NAME);
            unionid = accessTokenReslt.getString(UserConstants.WECHAT_ACCESSTOKEN_UNIONID_NAME);
            if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(unionid)) {
                throw new OpenAlertException("未获取正确的用户信息，请核对信息稍后重试");
            }
            insertPo = new UserAccountPo(null, platform, null, openId,
                    unionid, null, null,
                    null, null, null,
                    null, null, null, null, null, new Date());
        } else {
            //非微信小程序
            accessTokenReslt = service.getAccessTokenResult(code);
            log.info("通过code获取access_token_info:{}", (accessTokenReslt == null ? null : accessTokenReslt.toString()));
            if (accessTokenReslt == null) {
                throw new OpenAlertException("code错误，请使用正确的code");
            }
            //根据access_token获取openId
            String accessToken = accessTokenReslt.getString(UserConstants.WECHAT_ACCESSTOKEN_NAME);
            openId = accessTokenReslt.getString(UserConstants.WECHAT_ACCESSTOKEN_OPENID_NAME);
            unionid = accessTokenReslt.getString(UserConstants.WECHAT_ACCESSTOKEN_UNIONID_NAME);
            if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(openId) || StringUtils.isEmpty(unionid)) {
                throw new OpenAlertException("未获取正确的用户信息，请核对信息稍后重试");
            }
            //根据openId获取微信用户信息
            JSONObject userInfoJson = service.getUserInfo(accessToken, openId);
            insertPo = new UserAccountPo(null, platform, null, openId,
                    unionid, userInfoJson.getString("headimgurl"), userInfoJson.getString("nickname"),
                    userInfoJson.getInteger("sex"), userInfoJson.getString("language"), userInfoJson.getString("city"),
                    userInfoJson.getString("province"), userInfoJson.getString("country"), null, null, null, new Date());
        }
        //根据unionid获取用户中心数据库中第三方账号用户信息
        List<UserAccount> listUserAccout = getUserAccount(null, null, unionid);
        log.info("数据库第三方账号信息:{}", (listUserAccout == null ? null : listUserAccout.toString()));
        //当前登录的微信账号，在数据库中对应的记录
        UserAccount loginUserAccount = null;
        //当前登录的微信账号在其他主体上已经绑定用户中心账号的记录
        UserAccount alreadyBindingUserAccount = null;
        if (listUserAccout != null && listUserAccout.size() > 0) {
            //循环遍历，找出是否有匹配当前微信账号的记录，以及当前登录微信的绑定记录
            for (UserAccount userAccount : listUserAccout) {
                if (openId.equals(userAccount.getOpenid())) {
                    loginUserAccount = userAccount;
                }
                if (userAccount.getUserId() != null && userAccount.getUserId() != 0) {
                    alreadyBindingUserAccount = userAccount;
                }
            }
        }
        if (alreadyBindingUserAccount != null) {
            //已绑定账号
            isBinding = true;
            //如果有其他主体绑定了用户中心账号，当前主体直接绑定用户中心账号
            insertPo.setUserId(alreadyBindingUserAccount.getUserId());
        }
        if (loginUserAccount == null) {
            //当前主体首次登陆，创建第三方账号信息
            insertPo.setCreateTime(new Date());
        }
        if (loginUserAccount != null) {
            //当前主体非首次登陆，则更新
            insertPo.setAccountId(loginUserAccount.getAccountId());
            if (loginUserAccount.getUserId() != null && loginUserAccount.getUserId() != 0) {
                //已绑定账号
                isBinding = true;
            }
        }
        //不管绑定与否，都需要更新同步微信信息
        this.saveOrUpdate(insertPo);
        saveThirdLoginCodeByRedis(code, accessTokenReslt.toString(), platform);
        return isBinding;
    }

    @Override
    public Map<String, Object> loginByThirdPlatform(UserAccountPo accountPo) {
        //根据code从redis中获取openId
        String tokenResultStr = getThirdLoginCodeByRedis(accountPo.getCode(), accountPo.getPlatform());
        if (StringUtils.isEmpty(tokenResultStr)) {
            throw new OpenAlertException("code错误或过期");
        }
        JSONObject tokenResult = JSONObject.parseObject(tokenResultStr);
        String openId = tokenResult.getString(UserConstants.WECHAT_ACCESSTOKEN_OPENID_NAME);
        String unionid = tokenResult.getString(UserConstants.WECHAT_ACCESSTOKEN_UNIONID_NAME);
        //验证第三方账号信息是否存在
        List<UserAccount> listUserAccout = getUserAccount(null, null, unionid);
        //当前登录的微信账号，在数据库中对应的记录
        UserAccount userAccout = null;
        for (UserAccount uAccount : listUserAccout) {
            if (unionid.equals(uAccount.getUnionid()) && uAccount.getUserId() != null && uAccount.getUserId() != 0) {
                userAccout = uAccount;
            }
        }
        if (userAccout == null || userAccout.getUserId() == null) {
            throw new OpenAlertException("您暂未绑定用户中心账号，请绑定后再重试");
        }
        //查询用户信息
        User us = userMapper.selectById(userAccout.getUserId());
        if (us == null) {
            throw new OpenAlertException("您暂未绑定用户中心账号，请绑定后再重试");
        }
        if (StringUtils.isEmpty(us.getAvatar()) || StringUtils.isEmpty(us.getNickname())) {
            //如果用户中心表中头像或者是昵称为空，则从第三方账号同步
            if (StringUtils.isEmpty(us.getAvatar()) && StringUtils.isNotEmpty(userAccout.getAvatar())) {
                us.setAvatar(userAccout.getAvatar());
            }
            if (StringUtils.isEmpty(us.getNickname()) && StringUtils.isNotEmpty(userAccout.getNickname())) {
                us.setNickname(userAccout.getNickname());
            }
            userMapper.updateById(us);
        }
        //插入登录记录
        UserLoginLogsPo logs = new UserLoginLogsPo(null, userAccout.getUserId(), userAccout.getAccountId(), accountPo.getLoginIp(),
                new Date(), "ios", 1, openId, UserConstants.LOGIN_TYPE3);
        userLoginLogsMapper.insert(logs);
        //生成登录token信息
        Map<String, Object> map = JwtTokenUtils.getClaims(us.getMobile());
        String accessToken = JwtTokenUtils.createToken(UserConstants.PUBLIC_KEY_SALT_VUALE, map);
        //存储token到redis
        String sessionId = map.get(JwtTokenUtils.JWT_ATTRIBUTE_SESSIONID).toString();
        redisUtils.set(sessionId, accessToken, UserConstants.LOGIN_TOKEN_TIME);
        Map<String, Object> result = JwtTokenUtils.resultToken(accessToken, sessionId, UserConstants.LOGIN_TOKEN_TIME);
        //删除redis中第三方信息
        removeThirdLoginCodeByRedis(accountPo.getCode(), accountPo.getPlatform());
        return result;
    }

    @Override
    public List<UserAccount> getUserAccount(String openId, Integer platform, String unionid) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<UserAccount>();
        queryWrapper.lambda()
                .eq(ObjectUtils.isNotEmpty(openId), UserAccount::getOpenid, openId)
                .eq(ObjectUtils.isNotEmpty(platform), UserAccount::getPlatform, platform)
                .eq(ObjectUtils.isNotEmpty(unionid), UserAccount::getUnionid, unionid);
        List<UserAccount> result = userAccountMapper.selectList(queryWrapper);
        return result;
    }

    @Override
    public UserAccount getUserAccoutByUserId(Long userId, Integer platform) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<UserAccount>();
        queryWrapper.lambda().eq(UserAccount::getUserId, userId).eq(UserAccount::getPlatform, platform);
        List<UserAccount> result = userAccountMapper.selectList(queryWrapper);
        return (result != null && result.size() > 0) ? result.get(0) : null;
    }

    @Override
    public void bindingsUser(UserAccountPo accountPo) {
        log.info("入参：{}", accountPo.toString());
        //根据code从redis中获取openId
        String accessTokenReslt = getThirdLoginCodeByRedis(accountPo.getCode(), accountPo.getPlatform());
        String mobileCode = userService.getSmsCodeByRedis(accountPo.getMobile(), UserConstants.MOBILE_CODE_TYPE4);
        if (StringUtils.isEmpty(accessTokenReslt)) {
            log.info("code错误或code过期");
            throw new OpenAlertException("操作过时");
        }
        if (StringUtils.isEmpty(mobileCode) || !accountPo.getMobileCode().equals(mobileCode)) {
            throw new OpenAlertException("验证码错误或过期");
        }
        JSONObject tokenResult = JSONObject.parseObject(accessTokenReslt);
        String openId = tokenResult.getString(UserConstants.WECHAT_ACCESSTOKEN_OPENID_NAME);
        String unionid = tokenResult.getString(UserConstants.WECHAT_ACCESSTOKEN_UNIONID_NAME);
        //验证第三方账号信息是否存在
        List<UserAccount> listUserAccout = getUserAccount(null, null, unionid);
        //当前登录的微信账号，在数据库中对应的记录
        UserAccount userAccout = null;
        for (UserAccount uAccount : listUserAccout) {
            if (openId.equals(uAccount.getOpenid())) {
                userAccout = uAccount;
            }
        }
        if (userAccout == null) {
            throw new OpenAlertException("系统异常，请联系客服人员");
        }
        if (userAccout.getUserId() != null && userAccout.getUserId() != 0) {
            //已经绑定过账号，无需再次绑定
            throw new OpenAlertException("您已绑定过账号，请解绑后再绑定");
        }
        User user = userService.getUserInfoByMobile(accountPo.getMobile());
        if (user != null && !passwordEncoder.matches(accountPo.getPassword(), user.getPassword())
                && UserConstants.UPDATEFLAG1.equals(user.getUpdateFlag())) {
            throw new OpenAlertException("手机账号或密码错误");
        }
        if (user == null) {
            //用户不存在，新增用户然后再绑定微信
            UserPo userPo = new UserPo(null, RandomValueUtils.uuid(), null, passwordEncoder.encode(accountPo.getPassword()), null, null, null, null,
                    accountPo.getMobile(), UserConstants.USER_TYPE[0], null, accountPo.getLoginIp(), new Date(), null, UserConstants.USER_STATUS[1],
                    null, UserConstants.UPDATEFLAG1, new Date(), new Date());
            if (userAccout != null) {
                userPo.setSex(userAccout.getGender());
                userPo.setAvatar(userAccout.getAvatar());
                userPo.setNickname(userAccout.getNickname());
            }
            int count = userMapper.insert(userPo);
            if (count <= 0) {
                throw new OpenAlertException("系统异常，请联系客服人员");
            }
            userAccout.setUserId(userPo.getUserId());
        }
        if (user != null) {
            userAccout.setUserId(user.getUserId());
        }
        //绑定第三方账号
        userAccountMapper.updateById(userAccout);
    }

    @Override
    public void unbindingsUser(String mobile, Integer platform) {
        User userInfo = userService.getUserInfoByMobile(mobile);
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在");
        }
        //根据userid获取所有的第三方绑定用户信息
        UserAccount userAccount = getUserAccoutByUserId(userInfo.getUserId(), platform);
        if (userAccount == null) {
            throw new OpenAlertException("未绑定该平台账号");
        }
        userAccount.setUserId(0L);
        userAccountMapper.updateById(userAccount);
    }

    @Override
    public List<UserAccount> selectBatchUserIds(List<String> idList) {
        return userAccountMapper.selectList(Wrappers.<UserAccount>lambdaQuery().in(UserAccount::getUserId, idList));
    }

    /**
     * 第三方登录相关信息存储redis中
     *
     * @param code             回调code
     * @param accessTokenReslt 微信用户信息
     * @param platform         平台
     */
    private void saveThirdLoginCodeByRedis(String code, String accessTokenReslt, Integer platform) {
        if (!redisUtils.set(UserConstants.THIRD_LOGIN_CODE_PRE + platform + "-" + code, accessTokenReslt, UserConstants.THIRD_LOGIN_CODE_TIME)) {
            throw new OpenAlertException("系统异常，请稍后重试");
        }
    }

    /**
     * 从redis中获取第三方登录相关信息
     *
     * @param code
     * @param platform
     * @return
     */
    private String getThirdLoginCodeByRedis(String code, Integer platform) {
        String openId = (String) redisUtils.get(UserConstants.THIRD_LOGIN_CODE_PRE + platform + "-" + code);
        return openId;
    }

    /**
     * 从redis中删除第三方登录相关信息
     *
     * @param code
     * @param platform
     */
    private void removeThirdLoginCodeByRedis(String code, Integer platform) {
        String openId = (String) redisUtils.get(UserConstants.THIRD_LOGIN_CODE_PRE + platform + "-" + code);
        if (StringUtils.isNotEmpty(openId)) {
            redisUtils.del(UserConstants.THIRD_LOGIN_CODE_PRE + platform + "-" + code);
        }
    }
}
