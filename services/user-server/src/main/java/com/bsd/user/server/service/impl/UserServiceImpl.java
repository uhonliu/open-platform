package com.bsd.user.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.mapper.UserLoginLogsMapper;
import com.bsd.user.server.mapper.UserMapper;
import com.bsd.user.server.model.UserLoginLogsPo;
import com.bsd.user.server.model.UserPo;
import com.bsd.user.server.model.entity.User;
import com.bsd.user.server.service.UserService;
import com.bsd.user.server.service.feign.GatewayServiceClient;
import com.bsd.user.server.service.feign.SmsRemoteApiService;
import com.bsd.user.server.utils.JwtTokenUtils;
import com.bsd.user.server.utils.RegexUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.security.http.OpenRestTemplate;
import com.opencloud.common.utils.RandomValueUtils;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.common.utils.ValidateCodeUtils;
import com.opencloud.msg.client.model.SmsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户-基础信息 服务实现类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SmsRemoteApiService smsRemoteApiService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserLoginLogsMapper userLoginLogsMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OpenRestTemplate restTemplate;

    /**
     * 负载均衡
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private GatewayServiceClient gatewayServiceClient;

    @Override
    public void sendSmsCode(String mobile, Integer type, Integer signSource) {
        log.info("发送验证码业务方法入参：mobile={}，type={}，signSource={}", mobile, type, signSource);
        if (!Arrays.asList(UserConstants.MOBILE_CODE_TYPE_ALL).contains(type)) {
            throw new OpenAlertException("请选择正确类型的验证码");
        }
        if (!Arrays.asList(UserConstants.SIGNSOURCE).contains(signSource)) {
            throw new OpenAlertException("请指定正确的短信签名参数");
        }
        if (type.equals(UserConstants.MOBILE_CODE_TYPE2) || type.equals(UserConstants.MOBILE_CODE_TYPE3)
                || type.equals(UserConstants.MOBILE_CODE_TYPE5)) {
            //如果是登录验证码或者是找回密码验证码必须验证用户是否存在
            User userInfo = getUserInfoByMobile(mobile);
            if (userInfo == null) {
                throw new OpenAlertException("您暂未注册账号,请先注册后再操作");
            }
        }
        //生成验证码
        String cacheKey = getMobileCodeByRedisKey(mobile, type);
        String code = (String) redisUtils.get(cacheKey);
        if (code == null) {
            code = ValidateCodeUtils.getTextCode(ValidateCodeUtils.TYPE_NUM_ONLY, 6, null);
            if (!redisUtils.set(cacheKey, code, UserConstants.MOBILE_CODE_TIME)) {
                throw new OpenAlertException("系统繁忙，请稍后重试");
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        String templateCode = null;
        switch (type) {
            case UserConstants.MOBILE_CODE_TYPE1:
                templateCode = UserConstants.REGISTER_TEMPLATECODE;
                break;
            case UserConstants.MOBILE_CODE_TYPE2:
                templateCode = UserConstants.LOGIN_TEMPLATECODE;
                break;
            case UserConstants.MOBILE_CODE_TYPE3:
                templateCode = UserConstants.FORGET_PASSWORD_TEMPLATECODE;
                break;
            case UserConstants.MOBILE_CODE_TYPE4:
                templateCode = UserConstants.BINDINGS_TEMPLATECODE;
                break;
            case UserConstants.MOBILE_CODE_TYPE5:
                templateCode = UserConstants.BINDINGS_TEMPLATECODE;
                break;
            case UserConstants.MOBILE_CODE_TYPE6:
                templateCode = UserConstants.BINDINGS_TEMPLATECODE;
                break;
            default:
                break;
        }
        String signName = "";
        if (signSource.equals(UserConstants.SIGNSOURCE0)) {
            signName = UserConstants.SIGNNAME0;
        }
        if (signSource.equals(UserConstants.SIGNSOURCE1)) {
            signName = UserConstants.SIGNNAME1;
        }
        //发送验证码到手机
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setPhoneNum(mobile);
        smsMessage.setTplCode(templateCode);
        smsMessage.setSignName(signName);
        smsMessage.setTplParams(obj.toString());
        log.info("用户服务调用短信服务发送短信入参:手机号:{},短信模板code:{},签名:{},短信内容参数:{}",
                smsMessage.getPhoneNum(), smsMessage.getTplCode(), smsMessage.getSignName(), smsMessage.getTplParams());
        try {
            smsRemoteApiService.feignSendSms(smsMessage);
            log.info("发送短信验证码，手机号：{}，验证码类型type={},短信验证码：{},发送成功!", mobile, type, code);
        } catch (Exception e) {
            try {
                log.info("第2次用户服务调用短信服务发送短信入参:手机号:{},短信模板code:{},签名:{},短信内容参数:{}",
                        smsMessage.getPhoneNum(), smsMessage.getTplCode(), smsMessage.getSignName(), smsMessage.getTplParams());
                smsRemoteApiService.feignSendSms(smsMessage);
                log.info("发送短信验证码，手机号：{}，验证码类型type={},短信验证码：{},发送成功!", mobile, type, code);
            } catch (Exception e1) {
                log.info("发送短信验证码，手机号：{}，验证码类型type={},短信验证码：{},发送失败!,失败原因:{}", mobile, type, code, e);
                e.printStackTrace();
                throw new OpenAlertException("短信发送失败，稍后请重试");
            }
        }
    }

    @Override
    public String getSmsCodeByRedis(String mobile, Integer codeType) {
        return (String) redisUtils.get(getMobileCodeByRedisKey(mobile, codeType));
    }


    @Override
    public Integer registerByPhone(UserPo user) {
        //参数验证
        if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            throw new OpenAlertException("密码不符合要求");
        }
        Integer result = -1;
        String code = (String) redisUtils.get(getMobileCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE1));
        if (StringUtils.isBlank(user.getInputCode()) || !user.getInputCode().equals(code)) {
            throw new OpenAlertException("验证码错误");
        }
        //查询数据库是否有记录
        if (isExist(user.getMobile())) {
            throw new OpenAlertException("用户已存在");
        }
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setRegisterTime(new Date());
        user.setStatus(UserConstants.USER_STATUS[1]);
        user.setUserCode(RandomValueUtils.uuid());
        user.setUserType(UserConstants.USER_TYPE[0]);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUpdateFlag(UserConstants.UPDATEFLAG1);
        result = userMapper.insert(user);
        delMobleCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE1);
        log.info("短信验证码注册成功，用户：{}", user.getMobile());
        return result;
    }

    @Override
    public Map<String, Object> loginByMobileCode(UserPo user) {
        log.info("短信验证码登录，用户手机：{}", user.getMobile());
        //验证code
        String code = (String) redisUtils.get(getMobileCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE2));
        if (user.getInputCode() == null || !user.getInputCode().equals(code)) {
            throw new OpenAlertException("用户不存在或验证码错误");
        }
        //查询用户信息
        User userInfo = getUserInfoByMobile(user.getMobile());
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在或验证码错误");
        }
        if (!UserConstants.USER_STATUS1.equals(userInfo.getStatus())) {
            throw new OpenAlertException("您的账号已被锁定或禁用，请联系官方客服");
        }
        //插入登录记录
        UserLoginLogsPo logs = new UserLoginLogsPo(null, userInfo.getUserId(), null, user.getLoginIp(),
                new Date(), "ios", 1, userInfo.getMobile(), UserConstants.LOGIN_TYPE2);
        userLoginLogsMapper.insert(logs);
        //生成token信息
        Map<String, Object> map = JwtTokenUtils.getClaims(userInfo.getMobile());
        String access_token = JwtTokenUtils.createToken(UserConstants.PUBLIC_KEY_SALT_VUALE, map);
        //存储token到redis
        String sessionId = map.get(JwtTokenUtils.JWT_ATTRIBUTE_SESSIONID).toString();
        redisUtils.set(sessionId, access_token, UserConstants.LOGIN_TOKEN_TIME);
        Map<String, Object> result = JwtTokenUtils.resultToken(access_token, sessionId, UserConstants.LOGIN_TOKEN_TIME);
        delMobleCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE2);
        log.info("短信验证码登录成功，用户手机：{}", user.getMobile());
        return result;
    }

    @Override
    public User getUserInfoByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.lambda().eq(User::getMobile, mobile);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }


    @Override
    public Boolean isExist(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(User::getMobile, mobile);
        int count = userMapper.selectCount(queryWrapper);
        return count > 0 ? true : false;
    }


    @Override
    public Map<String, Object> authenticatingToken(UserPo user) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isEmpty(user.getLoginMobile())) {
            throw new OpenAlertException("参数错误");
        }
        User userInfo = getUserInfoByMobile(user.getLoginMobile());
        if (userInfo != null) {
            userInfo.setPassword(null);
        }
        result.putIfAbsent("userInfo", userInfo);
        log.info("入参：{}，返回结果：{}", user.getLoginMobile(), userInfo.toString());
        return result;
    }


    @Override
    public void updatePassword(UserPo user) {
        // 查询用户信息
        User userInfo = getUserInfoByMobile(user.getLoginMobile());
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在或码错误");
        }
        if (!passwordEncoder.matches(user.getOldPassword(), userInfo.getPassword())) {
            throw new OpenAlertException("用户不存在或码错误");
        }
        userInfo.setPassword(passwordEncoder.encode(user.getPassword()));
        userInfo.setUpdateTime(new Date());
        userMapper.updateById(userInfo);
    }

    @Override
    public Map<String, Object> loginByMobilePassword(UserPo user) {
        //查询用户信息
        User userInfo = getUserInfoByMobile(user.getMobile());
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在或密码错误");
        }
        if (false && UserConstants.UPDATEFLAG0.equals(userInfo.getUpdateFlag())) {
            //调用旧系统登录认证
            boolean rs = loginByOrderSystem(user.getMobile(), user.getPassword());
            if (!rs) {
                throw new OpenAlertException("用户不存在或密码错误");
            }
            //认证成功更新用户信息
            userInfo.setPassword(passwordEncoder.encode(user.getPassword()));
            userInfo.setUpdateFlag(UserConstants.UPDATEFLAG1);
            userInfo.setUpdateTime(new Date());
            userMapper.updateById(userInfo);
        }
        if (!passwordEncoder.matches(user.getPassword(), userInfo.getPassword())) {
            throw new OpenAlertException("用户不存在或密码错误");
        }
        if (!UserConstants.USER_STATUS1.equals(userInfo.getStatus())) {
            throw new OpenAlertException("您的账号已被锁定或禁用，请联系官方客服");
        }
        //插入登录记录
        UserLoginLogsPo logs = new UserLoginLogsPo(null, userInfo.getUserId(), null, user.getLoginIp(),
                new Date(), "ios", 1, userInfo.getMobile(), UserConstants.LOGIN_TYPE1);
        userLoginLogsMapper.insert(logs);
        //生成登录token信息
        Map<String, Object> map = JwtTokenUtils.getClaims(userInfo.getMobile());
        String accessToken = JwtTokenUtils.createToken(UserConstants.PUBLIC_KEY_SALT_VUALE, map);
        //存储token到redis
        String sessionId = map.get(JwtTokenUtils.JWT_ATTRIBUTE_SESSIONID).toString();
        redisUtils.set(sessionId, accessToken, UserConstants.LOGIN_TOKEN_TIME);
        Map<String, Object> result = JwtTokenUtils.resultToken(accessToken, sessionId, UserConstants.LOGIN_TOKEN_TIME);
        return result;
    }

    @Override
    public void resetPassword(UserPo user) {
        // 验证code
        String code = (String) redisUtils.get(getMobileCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE3));
        System.out.println(code + "==" + user.getInputCode());
        if (!user.getInputCode().equals(code)) {
            throw new OpenAlertException("验证码错误");
        }
        // 查询用户信息
        User userInfo = getUserInfoByMobile(user.getMobile());
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在或验证码错误");
        }
        userInfo.setPassword(passwordEncoder.encode(user.getPassword()));
        userInfo.setUpdateFlag(UserConstants.UPDATEFLAG1);
        userInfo.setUpdateTime(new Date());
        userMapper.updateById(userInfo);
        delMobleCodeByRedisKey(user.getMobile(), UserConstants.MOBILE_CODE_TYPE3);
    }

    @Override
    public boolean loginByOrderSystem(String mobile, String password) {
        boolean result = false;
        Map<String, Object> map = Maps.newHashMap();
        map.put("mobile", mobile);
        map.put("pwd", password);
        String body = restTemplate.postForObject(getUrlByRoute("kjapi", "/v1/user/pwd"), map, String.class);
        log.info("旧系统登录返回结果：{}", body);
        if (StringUtils.isNotEmpty(body)) {
            JSONObject obj = JSONObject.parseObject(body);
            JSONObject data = obj.getJSONObject("data");
            if (obj.getInteger("code") == 0 && data.getBoolean("safe") == true) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void updateUserInfo(UserPo user) {
        User userInfo = getUserInfoByMobile(user.getLoginMobile());
        if (userInfo == null) {
            throw new OpenAlertException("用户不存在");
        }
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            userInfo.setAvatar(user.getAvatar());
        }
        if (StringUtils.isNotEmpty(user.getNickname())) {
            userInfo.setNickname(user.getNickname());
        }
        if (StringUtils.isNotEmpty(user.getEmail())) {
            userInfo.setEmail(user.getEmail());
        }
        if (StringUtils.isNotEmpty(user.getUserDesc())) {
            userInfo.setUserDesc(user.getUserDesc());
        }
        userInfo.setUpdateTime(new Date());
        userMapper.updateById(userInfo);
    }

    @Override
    public void verifyOldMobile(UserPo po) {
        //验证手机验证码
        String code = getSmsCodeByRedis(po.getLoginMobile(), UserConstants.MOBILE_CODE_TYPE5);
        if (code == null || !code.equals(po.getInputCode())) {
            throw new OpenAlertException("验证码错误");
        }
        //验证手机账号密码
        User userInfo = getUserInfoByMobile(po.getLoginMobile());
        if (userInfo == null
                || !passwordEncoder.matches(po.getOldPassword(), userInfo.getPassword())) {
            throw new OpenAlertException("用户不存在或密码错误");
        }
        setVerifyOldMobileInfoByRedis(po.getLoginMobile());
        delMobleCodeByRedisKey(po.getLoginMobile(), UserConstants.MOBILE_CODE_TYPE5);
    }


    @Override
    public void updateMobile(UserPo po) {
        //验证手机验证码
        String code = getSmsCodeByRedis(po.getMobile(), UserConstants.MOBILE_CODE_TYPE6);
        if (code == null || !code.equals(po.getInputCode())) {
            throw new OpenAlertException("验证码错误");
        }
        if (getVerifyOldMobileInfoByRedis(po.getLoginMobile()) == null) {
            throw new OpenAlertException("请完成第一步用户验证操作");
        }
        if (getUserInfoByMobile(po.getMobile()) != null) {
            throw new OpenAlertException("该手机号码已被其他账号绑定");
        }
        User userInfo = getUserInfoByMobile(po.getLoginMobile());
        userInfo.setMobile(po.getMobile());
        userMapper.updateById(userInfo);
        delMobleCodeByRedisKey(po.getMobile(), UserConstants.MOBILE_CODE_TYPE6);
        //清理当前登录用户token
        delLoginTokenByRedis(po.getSessionId());
    }


    /**
     * 手机验证码存储在redis中key
     *
     * @param mobile
     * @param type
     * @return
     */
    private String getMobileCodeByRedisKey(String mobile, Integer type) {
        return UserConstants.MOBILE_CODE_PRE + type + "-" + mobile;
    }

    /**
     * 删除redis中的手机验证码
     *
     * @param mobile
     * @param type
     */
    private void delMobleCodeByRedisKey(String mobile, Integer type) {
        try {
            redisUtils.del(getMobileCodeByRedisKey(mobile, type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改用户手机号-验证旧用户信息存储redis中
     *
     * @param mobile
     */
    private void setVerifyOldMobileInfoByRedis(String mobile) {
        redisUtils.set(UserConstants.TOLD_MOBILE_PRE + mobile, mobile, UserConstants.OLD_MOBILE_TIME);
    }

    /**
     * 修改用户手机号-从redis中获取存入的旧用户信息
     *
     * @param mobile
     * @return
     */
    private String getVerifyOldMobileInfoByRedis(String mobile) {
        return (String) redisUtils.get(UserConstants.TOLD_MOBILE_PRE + mobile);
    }

    /**
     * 修改用户手机号-从redis中删除存入的旧用户信息
     *
     * @param mobile
     */
    private void delVerifyOldMobileInfoByRedis(String mobile) {
        try {
            redisUtils.del(UserConstants.TOLD_MOBILE_PRE + mobile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将登陆的token信息存放到redis中
     *
     * @param sessionId
     * @param token
     * @param time
     */
    private void setLoginTokenToRedis(String sessionId, String token, Long time) {
        redisUtils.set(sessionId, token, time);
    }

    /**
     * 将登陆的token从redis中删除
     *
     * @param sessionId
     */
    private void delLoginTokenByRedis(String sessionId) {
        redisUtils.del(sessionId);
    }


    /**
     * 分页获取用户数据
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param userPo  查询条件
     * @return
     */
    @Override
    public IPage<User> userPageList(int current, int size, UserPo userPo) {
        //根据PO对象创建查询条件LambdaQueryWrapper
        LambdaQueryWrapper lambdaQueryWrapper = createQueryWrapperByUserPo(userPo);
        //查询
        IPage<User> pageConfig = new Page<User>(current, size);
        IPage<User> result = userMapper.selectPage(pageConfig, lambdaQueryWrapper);
        return result;
    }

    @Override
    public List<User> userList(UserPo userPo) {
        //根据PO对象创建查询条件LambdaQueryWrapper
        LambdaQueryWrapper lambdaQueryWrapper = createQueryWrapperByUserPo(userPo);
        //查询返回结果
        return userMapper.selectList(lambdaQueryWrapper);
    }


    /**
     * 根据PO对象创建查询条件LambdaQueryWrapper
     *
     * @param userPo
     * @return
     */
    private LambdaQueryWrapper createQueryWrapperByUserPo(UserPo userPo) {
        //检查用户状态
        Integer status = userPo.getStatus();
        if (status != null && !Arrays.asList(UserConstants.USER_STATUS).contains(status)) {
            throw new OpenAlertException("用户状态参数值错误");
        }

        //检查用户来源
        Integer source = userPo.getSource();
        if (source != null && !Arrays.asList(UserConstants.USER_SOURCE).contains(source)) {
            throw new OpenAlertException("用户来源参数值错误");
        }

        //检查输入搜索内容
        String searchContent = userPo.getSearchContent();
        if (StringUtils.isNotEmpty(searchContent) && searchContent.length() > 50) {
            //超过用户昵称/手机号/用户ID的长度
            throw new OpenAlertException("搜索内容长度过长");
        }


        //设置查询条件
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.<User>lambdaQuery();
        lambdaQueryWrapper.eq(status != null, User::getStatus, status)
                .eq(source != null, User::getSource, source);
        if (StringUtils.isNotEmpty(searchContent)) {
            lambdaQueryWrapper.and(
                    item -> item.eq(User::getNickname, searchContent).or()
                            .eq(User::getMobile, searchContent).or()
                            .eq(User::getUserId, searchContent));
        }
        lambdaQueryWrapper.orderByDesc(User::getCreateTime);
        return lambdaQueryWrapper;
    }


    /**
     * 批量推送用户信息
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public boolean batchPush(List<String> ids, String authorization) {
        //判断一下批量修改的用户数
        if (ids == null || ids.isEmpty()) {
            throw new OpenAlertException("用户ID列表参数值错误");
        }
        if (ids.size() > 50) {
            throw new OpenAlertException("用户ID列表大于50个");
        }
        //判断用户ID是否为Long
        for (String id : ids) {
            try {
                Long.parseLong(id);
            } catch (Exception ex) {
                log.error("用户ID转化失败");
                throw new OpenAlertException("用户ID列表存在错误");
            }
        }

        //查询用户信息
        List<User> users = userMapper.selectBatchIds(ids);
        if (users == null || users.size() == 0) {
            throw new OpenAlertException("未找到用户信息");
        }
        if (users.size() != ids.size()) {
            throw new OpenAlertException("未找到所有的用户信息");
        }

        //推送任务处理
        boolean isSuc = true; // pushUserToCustomerSystem(users, authorization);
        if (!isSuc) {
            throw new OpenAlertException("推送用户信息失败");
        }

        //批量修改用户类型
        for (User user : users) {
            user.setUserType(UserConstants.USER_TYPE_CUSTOMER);
        }

        boolean isUpdateSuc = updateBatchById(users, users.size());
        if (isSuc && !isUpdateSuc) {
            //推送成功,更新用户状态失败,记录一下日志
            log.error("推送成功,修改用户状态失败,ids:{}", ids);
            throw new OpenAlertException("推送成功,修改用户状态失败");
        }

        return true;
    }

    /**
     * 推送用户信息到客户系统
     *
     * @param users
     * @return
     */
    private boolean pushUserToCustomerSystem(List<User> users, String authorization) {
        //请求PHP接口,推送用户数据到客户系统中
        JSONArray customers = new JSONArray();
        users.forEach(x -> {
            if (UserConstants.USER_TYPE_CUSTOMER.equals(x.getUserType())) {
                throw new OpenAlertException("用户列表中存在已经推送过的用户ID");
            }
            JSONObject customer = new JSONObject();
            customer.put("customer_id", String.valueOf(x.getUserId()));//用户ID
            customer.put("customer_source", convertToCustomerSource(x.getSource()));//用户来源
            customer.put("customer_tel", x.getMobile());//用户号码
            customer.put("customer_sex", x.getSex());//用户性别
            customer.put("customer_name", x.getUsername());//用户名字
            customers.add(customer);
        });
        log.info("customers:{}", customers.toJSONString());
        String url = getUrlByRoute("khgl", "/push/add_customer");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", authorization);
        HttpEntity<String> entity = new HttpEntity<String>(customers.toJSONString(), headers);
        String result = restTemplate.postForEntity(url, entity, String.class).getBody();
        log.info("推送用户数据到客户系统,返回信息:{}", result);
        if (StringUtils.isEmpty(result)) {
            throw new OpenAlertException("推送用户给客户系统失败");
        }
        try {
            JSONObject resultObj = JSONObject.parseObject(result);
            String code = resultObj.getString("code");
            if (StringUtils.isNotEmpty(code) && "0".equals(code)) {
                return true;
            }
        } catch (Exception ex) {
            log.error("客户系统返回数据反序列化失败");
            throw new OpenAlertException("推送用户给客户系统失败");
        }
        return false;
    }

    /**
     * 把source转换成客户系统中source
     *
     * @param source
     * @return
     */
    private String convertToCustomerSource(Integer source) {
        if (source.intValue() == UserConstants.USER_SOURCE0) {
            return "kjzd";
        }

        if (source.intValue() == UserConstants.USER_SOURCE1) {
            return "mjcz";
        }

        if (source.intValue() == UserConstants.USER_SOURCE3) {
            return "rglr";
        }
        throw new OpenAlertException("用户来源source转换异常");
    }


    /**
     * 批量修改用户状态
     *
     * @param ids
     * @param status
     * @return
     */
    @Override
    @Transactional
    public boolean batchChangeStatus(List<String> ids, Integer status) {
        //用户状态
        if (status == null || !Arrays.asList(UserConstants.USER_STATUS).contains(status)) {
            throw new OpenAlertException("用户状态参数值错误");
        }
        //判断一下批量修改的用户数
        if (ids == null || ids.isEmpty()) {
            throw new OpenAlertException("用户ID列表参数值错误");
        }
        if (ids.size() > 50) {
            throw new OpenAlertException("用户ID列表大于50个");
        }

        //批量修改用户状态
        List<User> users = new ArrayList<User>();
        for (String id : ids) {
            User user = new User();
            user.setStatus(status);
            try {
                user.setUserId(Long.valueOf(id));
            } catch (Exception ex) {
                log.error("用户ID转化失败");
                throw new OpenAlertException("用户ID列表存在错误");
            }
            users.add(user);
        }
        return updateBatchById(users, users.size());
    }


    /**
     * 手工录入保存数据
     *
     * @param userPo
     * @return
     */
    @Override
    public boolean manualSave(UserPo userPo) {
        //检查性别参数值
        int sex = userPo.getSex();
        if (UserConstants.GENDER_SECRECY != sex && UserConstants.GENDER_MAN != sex && UserConstants.GENDER_FEMALE != sex) {
            throw new OpenAlertException("用户性别参数值错误");
        }
        //检查号码
        String mobile = userPo.getMobile();
        //判断格式
        boolean isMobile = RegexUtils.isMobileExact(mobile);
        if (!isMobile) {
            throw new OpenAlertException("手机号码格式错误");
        }
        //判断号码是否存在
        User user = getUserInfoByMobile(mobile);
        if (user != null) {
            //用户存在,直接返回
            userPo.setUserId(user.getUserId());
            return true;
        }
        //保存数据
        userPo.setCreateTime(new Date());
        userPo.setUpdateTime(new Date());
        userPo.setRegisterTime(new Date());
        int count = userMapper.insert(userPo);
        return count > 0;
    }

    @Override
    public List<User> exportMaxUserList(UserPo userPo) {
        //根据PO对象创建查询条件LambdaQueryWrapper
        LambdaQueryWrapper lambdaQueryWrapper = createQueryWrapperByUserPo(userPo);
        //限制最大条数
        lambdaQueryWrapper.last("limit 10000");
        return userMapper.selectList(lambdaQueryWrapper);
    }

    private String getUrlByRoute(String name, String path) {
        List<GatewayRoute> routes = getApiRouteList();
        for (GatewayRoute route : routes) {
            if (route.getRouteName().equals(name)) {
                if (BaseConstants.ROUTE_TYPE_URL.equalsIgnoreCase(route.getRouteType())) {
                    if (route.getUrl().endsWith("/")) {
                        return route.getUrl() + path.replaceFirst("/", "");
                    }
                    return route.getUrl() + path;
                } else if (BaseConstants.ROUTE_TYPE_SERVICE.equalsIgnoreCase(route.getRouteType())) {
                    ServiceInstance serviceInstance = loadBalancerClient.choose(name);
                    // 获取服务实例
                    if (serviceInstance == null) {
                        throw new RuntimeException(String.format("%s服务暂不可用", name));
                    }
                    return String.format("%s%s", serviceInstance.getUri(), path);
                }
            }
        }
        throw new RuntimeException(String.format("%s服务暂不可用", name));
    }

    public List<GatewayRoute> getApiRouteList() {
        List<GatewayRoute> routes = redisUtils.getList(BaseConstants.ROUTE_LIST_CACHE_KEY);
        if (routes.isEmpty()) {
            ResultBody<List<GatewayRoute>> resultBody = gatewayServiceClient.getApiRouteList();
            routes = resultBody.getData();
            if (!routes.isEmpty()) {
                redisUtils.setList(BaseConstants.ROUTE_LIST_CACHE_KEY, routes, BaseConstants.ROUTE_LIST_CACHE_TIME);
            }
        }
        return routes;
    }
}