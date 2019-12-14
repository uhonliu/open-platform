package com.bsd.user.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bsd.user.server.constants.DeveloperConstants;
import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.model.dto.JsSdkSignDTO;
import com.bsd.user.server.service.DeveloperService;
import com.bsd.user.server.utils.redis.DistributedLocker;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.security.http.OpenRestTemplate;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientDetails;
import com.opencloud.common.security.oauth2.client.OpenOAuth2ClientProperties;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.UUID;

/**
 * 开放平台服务实现类
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 15:07
 */
@Slf4j
@Service
public class DeveloperServiceImpl implements DeveloperService {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DistributedLocker distributedLocker;

    @Autowired
    protected OpenOAuth2ClientProperties openOAuth2ClientProperties;

    @Autowired
    private OpenRestTemplate restTemplate;

    /**
     * 微信js-sdk授权地址签名
     *
     * @param url 授权URL地址
     * @return
     */
    @Override
    public JsSdkSignDTO makeWxJsSdkSign(String url) {
        //获取公众号配置信息
        OpenOAuth2ClientDetails config = getWxConfig("wechat-gzh");
        //获取微信jsapi_ticket
        String ticket = getWxJsApiTicket(config);
        //初始化
        JsSdkSignDTO jsSdkSignDTO = initJsSdkSignDTO(config, url);
        //URL授权签名
        String sign = makeSign(jsSdkSignDTO, ticket);
        if (StringUtils.isEmpty(sign)) {
            throw new OpenAlertException("URL签名授权失败,请稍后重试");
        }
        jsSdkSignDTO.setSignature(sign);
        return jsSdkSignDTO;
    }

    @Override
    public JSONObject authGetAccessToken(Integer platform) {
        JSONObject objToken = new JSONObject();
        if (!UserConstants.PLATFORM_MINIPROGRAM.equals(platform)) {
            throw new OpenAlertException("平台参数错误，请检查参数");
        }
        String redisToken = (String) redisUtils.get(DeveloperConstants.AUTH_GETACCESSTOKEN_REDIS_PREFIX + platform);
        if (StringUtils.isNotEmpty(redisToken)) {
            objToken.put("accessToken", redisToken);
            return objToken;
        }
        OpenOAuth2ClientDetails config = getWxConfig("wechat_gzh_xcx");
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(String.format(DeveloperConstants.AUTH_GETACCESSTOKEN_URL, config.getClientId(), config.getClientSecret()), JSONObject.class);
        JSONObject obj = responseEntity.getBody();
        log.info("获取小程序authGetAccessToken结果:{}", obj.toString());
        String accessToken = obj.getString("access_token");
        String expiresIn = obj.getString("expires_in");
        if (StringUtils.isNotEmpty(accessToken)) {
            //ticket有效期为7200秒,我方平台需要提前在过期之前重新获取刷新[提前5分钟过期]
            long redisTicketExpireTime = Long.valueOf(expiresIn) - 5 * 60;
            //缓存到redis
            redisUtils.set(DeveloperConstants.AUTH_GETACCESSTOKEN_REDIS_PREFIX + platform, accessToken, redisTicketExpireTime);
            objToken.put("accessToken", accessToken);
        } else {
            throw new OpenAlertException("获取accessToken失败，请稍后重试");
        }
        return objToken;
    }


    /**
     * 获取微信公众号配置信息
     *
     * @return
     */
    private OpenOAuth2ClientDetails getWxConfig(String platformName) {
        OpenOAuth2ClientDetails config = openOAuth2ClientProperties.getOauth2().get(platformName);
        if (config == null) {
            throw new OpenAlertException("配置信息尚未初始化,请稍后重试");
        }
        //第三方用户唯一凭证
        String appid = config.getClientId();
        if (StringUtils.isEmpty(appid)) {
            throw new OpenAlertException("appid未配置");
        }
        //第三方用户唯一凭证密钥
        String appsecret = config.getClientSecret();
        if (StringUtils.isEmpty(appsecret)) {
            throw new OpenAlertException("appsecret未配置");
        }
        return config;
    }

    /**
     * 创建签名
     *
     * @param jsSdkSignDTO
     * @return
     */
    private String makeSign(JsSdkSignDTO jsSdkSignDTO, String ticket) {
        //构建签名串
        StringBuilder sb = new StringBuilder();
        sb.append("jsapi_ticket=").append(ticket)
                .append("&noncestr=").append(jsSdkSignDTO.getNonceStr())
                .append("&timestamp=").append(jsSdkSignDTO.getTimestamp())
                .append("&url=").append(jsSdkSignDTO.getUrl());

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(sb.toString().getBytes("UTF-8"));
            return byteToHex(crypt.digest());
        } catch (Exception e) {
            //打印堆栈信息
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param config
     * @param url
     * @return
     */
    private JsSdkSignDTO initJsSdkSignDTO(OpenOAuth2ClientDetails config, String url) {
        JsSdkSignDTO jsSdkSignDTO = new JsSdkSignDTO();
        //公众号的唯一标识
        jsSdkSignDTO.setAppId(config.getClientId());
        //生成签名的随机串
        jsSdkSignDTO.setNonceStr(UUID.randomUUID().toString());
        //生成签名的时间戳
        jsSdkSignDTO.setTimestamp(Long.toString(System.currentTimeMillis() / 1000));
        //授权URL
        jsSdkSignDTO.setUrl(url);
        return jsSdkSignDTO;
    }

    /**
     * 获取微信jsapi_ticket
     *
     * @return
     */
    private String getWxJsApiTicket(OpenOAuth2ClientDetails config) {
        //缓存中获取ticket
        String ticket = (String) redisUtils.get(DeveloperConstants.APP_JSAPI_TICKET_REDIS_PREFIX + config.getClientId());
        if (StringUtils.isNotEmpty(ticket)) {
            return ticket;
        }
        //ticket过期,或者未获取过ticket,需要获取更新
        ticket = remoteGetJsApiTicket(config);
        if (StringUtils.isEmpty(ticket)) {
            throw new OpenAlertException("获取jsapi_ticket失败,请稍后重试");
        }
        return ticket;
    }

    /**
     * 请求获取ticket
     *
     * @param config
     * @return
     */
    private String remoteGetJsApiTicket(OpenOAuth2ClientDetails config) {
        try {
            //加锁
            distributedLocker.lock(DeveloperConstants.TICKET_LOCK_KEY, DeveloperConstants.TICKET_LOCK_RELEASE_TIME);
            //第二次判空处理，防止活跃的线程在等待获取的锁，在锁释放后就会再次竞争获取锁,导致重复请求刷新token/ticket
            String ticket = (String) redisUtils.get(DeveloperConstants.APP_JSAPI_TICKET_REDIS_PREFIX + config.getClientId());
            if (StringUtils.isNotEmpty(ticket)) {
                return ticket;
            }
            return getTicke(getWxJsApiToken(config.getClientId(), config.getClientSecret()), config.getClientId());
        } catch (OpenAlertException ex) {
            //自定义异常,抛出
            throw ex;
        } catch (Exception ex) {
            //未知异常,打印堆栈信息
            ex.printStackTrace();
            return null;
        } finally {
            //解锁
            distributedLocker.unlock(DeveloperConstants.TICKET_LOCK_KEY);
        }
    }

    /**
     * 请求微信获取ticket
     *
     * @param token
     * @return
     */
    private String getTicke(String token, String appid) {
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(String.format(DeveloperConstants.JSAPI_TICKET_URL, token), JSONObject.class);
        JSONObject obj = responseEntity.getBody();
        String ticket = obj.getString("ticket");
        String expiresIn = obj.getString("expires_in");
        if (StringUtils.isNotEmpty(ticket)) {
            //ticket有效期为7200秒,我方平台需要提前在过期之前重新获取刷新[提前5分钟过期]
            long redisTicketExpireTime = Long.valueOf(expiresIn) - 5 * 60;
            //缓存到redis
            redisUtils.set(DeveloperConstants.APP_JSAPI_TICKET_REDIS_PREFIX + appid, ticket, redisTicketExpireTime);
            return ticket;
        }
        //记录错误日志
        log.error("getTicke error :{}", responseEntity.getBody());
        throw new OpenAlertException("获取ticket失败");
    }

    /**
     * 请求微信获取token
     *
     * @param appid
     * @param appsecret
     * @return
     */
    private String getWxJsApiToken(String appid, String appsecret) {
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(String.format(DeveloperConstants.JSAPI_TOKEN_URL, appid, appsecret), JSONObject.class);
        JSONObject obj = responseEntity.getBody();
        String accessToken = obj.getString("access_token");
        if (StringUtils.isNotEmpty(accessToken)) {
            return accessToken;
        }
        //记录错误日志
        log.error("getWxJsApiToken error :{}", responseEntity.getBody());
        throw new OpenAlertException("获取token失败");
    }

    /**
     * byte数组转哈希
     *
     * @param hash
     * @return
     */
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
