package com.bsd.user.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.util.Md5Utils;
import com.bsd.user.server.model.dto.CaptchaInitDTO;
import com.bsd.user.server.model.dto.CaptchaInitResultDTO;
import com.bsd.user.server.model.dto.CaptchaValidateDTO;
import com.bsd.user.server.model.dto.CaptchaValidateResultDTO;
import com.bsd.user.server.service.CaptchaService;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 15:02
 */
@Slf4j
@Api(tags = "行为验证")
@RestController
@RequestMapping("/user/captcha")
public class CaptchaController {
    /**
     * 初始化用户redis key 前缀
     */
    private static final String CAPTCHA_INIT_USER_PREFIX = "captcha:init:user:";

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 行为验证初始化
     *
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @param request    request
     * @return
     */
    @ApiOperation(value = "初始化", notes = "行为验证初始化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
            @ApiImplicitParam(name = "clientType", required = true, value = "客户端类型web(pc浏览器),h5(手机浏览器,包括webview),native(原生app),unknown(未知)", paramType = "form"),
    })
    @PostMapping("/init")
    public ResultBody init(@RequestParam(value = "userId") String userId,
                           @RequestParam(value = "clientType") String clientType,
                           HttpServletRequest request) {
        //业务参数
        CaptchaInitDTO captchaInitDTO = new CaptchaInitDTO();
        captchaInitDTO.setIp(WebUtils.getRemoteAddress(request));
        captchaInitDTO.setUserId(CAPTCHA_INIT_USER_PREFIX + Md5Utils.getMD5(userId, "UTF-8"));//用户ID MD5加密一下,避免泄露
        captchaInitDTO.setClientType(clientType);
        log.info("init:{}", captchaInitDTO);
        //调用初始化接口
        CaptchaInitResultDTO captchaInitResultDTO = captchaService.init(captchaInitDTO);
        //初始化数据存到redis中
        captchaInitDTO.setGtServerStatus(captchaInitResultDTO.getGtServerStatus());
        redisUtils.set(captchaInitDTO.getUserId(), JSON.toJSONString(captchaInitDTO), 60 * 60);
        return ResultBody.ok().data(captchaInitResultDTO);
    }


    /**
     * 行为验证二次验证
     *
     * @param chllenge
     * @param validate
     * @param seccode
     * @param request
     * @return
     */
    @ApiOperation(value = "二次验证", notes = "行为验证二次验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
            @ApiImplicitParam(name = "chllenge", required = true, value = "极验验证二次验证表单数据 chllenge", paramType = "form"),
            @ApiImplicitParam(name = "validate", required = true, value = "极验验证二次验证表单数据 validate", paramType = "form"),
            @ApiImplicitParam(name = "seccode", required = true, value = "极验验证二次验证表单数据 seccode", paramType = "form"),
    })
    @PostMapping("/validate")
    public ResultBody validate(@RequestParam(value = "userId") String userId,
                               @RequestParam(value = "chllenge") String chllenge,
                               @RequestParam(value = "validate") String validate,
                               @RequestParam(value = "seccode") String seccode,
                               HttpServletRequest request) {
        //获取session中的数据
        String initStr = (String) redisUtils.get(CAPTCHA_INIT_USER_PREFIX + Md5Utils.getMD5(userId, "UTF-8"));
        if (StringUtils.isEmpty(initStr)) {
            return ResultBody.failed().msg("二次验证之前未调用初始化接口");
        }
        CaptchaInitDTO captchaInitDTO = JSON.parseObject(initStr, CaptchaInitDTO.class);
        //业务数据
        CaptchaValidateDTO captchaValidateDTO = new CaptchaValidateDTO();
        captchaValidateDTO.setChllenge(chllenge);
        captchaValidateDTO.setValidate(validate);
        captchaValidateDTO.setSeccode(seccode);
        captchaValidateDTO.setIp(WebUtils.getRemoteAddress(request));
        captchaValidateDTO.setUserId(captchaInitDTO.getUserId());
        captchaValidateDTO.setClientType(captchaInitDTO.getClientType());
        captchaValidateDTO.setGtServerStatus(captchaInitDTO.getGtServerStatus());
        log.info("validate:{}", captchaValidateDTO);
        //二次验证请求
        CaptchaValidateResultDTO captchaValidateResultDTO = captchaService.validate(captchaValidateDTO);
        if ("success".equals(captchaValidateResultDTO.getStatus())) {
            return ResultBody.ok().data(captchaValidateResultDTO);
        }
        return ResultBody.failed().data(captchaValidateResultDTO);
    }
}
