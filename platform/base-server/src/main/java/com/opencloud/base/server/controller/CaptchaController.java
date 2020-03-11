package com.opencloud.base.server.controller;

import com.google.common.collect.Maps;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import com.wf.captcha.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * 图形验证码
 *
 * @author liujianhong
 */
@Api(tags = "图形验证码")
@RestController
public class CaptchaController {
    @Autowired
    private RedisUtils<String> redisUtil;

    /**
     * 获取png验证码
     *
     * @return
     */
    @ApiOperation(value = "获取png验证码", notes = "获取png验证码")
    @GetMapping("/captcha")
    public ResultBody specCaptcha() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String verCode = captcha.text();
        String key = "captcha-" + UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        Map<String, String> map = Maps.newHashMap();
        map.put("verKey", key);
        map.put("image", captcha.toBase64());
        return ResultBody.ok().data(map);
    }

    /**
     * 获取gif验证码
     *
     * @return
     */
    @ApiOperation(value = "获取gif验证码", notes = "获取gif验证码")
    @GetMapping("/captcha/gif")
    public ResultBody gifCaptcha() {
        GifCaptcha captcha = new GifCaptcha(130, 48, 4);
        String verCode = captcha.text();
        String key = "captcha-" + UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        Map<String, String> map = Maps.newHashMap();
        map.put("verKey", key);
        map.put("image", captcha.toBase64());
        return ResultBody.ok().data(map);
    }

    /**
     * 获取中文验证码
     *
     * @return
     */
    @ApiOperation(value = "获取中文验证码", notes = "获取中文验证码")
    @GetMapping("/captcha/chinese")
    public ResultBody chineseCaptcha() {
        ChineseCaptcha captcha = new ChineseCaptcha(130, 48, 4);
        String verCode = captcha.text();
        String key = "captcha-" + UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        Map<String, String> map = Maps.newHashMap();
        map.put("verKey", key);
        map.put("image", captcha.toBase64());
        return ResultBody.ok().data(map);
    }

    /**
     * 获取中文gif验证码
     *
     * @return
     */
    @ApiOperation(value = "获取中文gif验证码", notes = "获取中文gif验证码")
    @GetMapping("/captcha/chineseGif")
    public ResultBody chineseGifCaptcha() {
        ChineseGifCaptcha captcha = new ChineseGifCaptcha(130, 48, 4);
        String verCode = captcha.text();
        String key = "captcha-" + UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        Map<String, String> map = Maps.newHashMap();
        map.put("verKey", key);
        map.put("image", captcha.toBase64());
        return ResultBody.ok().data(map);
    }

    /**
     * 获取算术验证码
     *
     * @return
     */
    @ApiOperation(value = "获取算术验证码", notes = "获取算术验证码")
    @GetMapping("/captcha/arithmetic")
    public ResultBody arithmeticCaptcha() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48, 3);
        captcha.getArithmeticString();
        String verCode = captcha.text();
        String key = "captcha-" + UUID.randomUUID().toString();
        // 存入redis并设置过期时间为30分钟
        redisUtil.set(key, verCode, 1800);
        // 将key和base64返回给前端
        Map<String, String> map = Maps.newHashMap();
        map.put("verKey", key);
        map.put("image", captcha.toBase64());
        return ResultBody.ok().data(map);
    }

    /**
     * 校验验证码
     *
     * @return
     */
    @ApiOperation(value = "校验验证码", notes = "校验验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "verKey", required = true, value = "验证码键值", paramType = "form"),
            @ApiImplicitParam(name = "verCode", required = true, value = "验证码", paramType = "form")
    })
    @PostMapping("/captcha/verify")
    public ResultBody verify(@RequestParam(value = "verKey") String verKey,
                             @RequestParam(value = "verCode") String verCode) {
        // 获取redis中的验证码
        String redisCode = redisUtil.get(verKey);
        if (StringUtils.isEmpty(redisCode)) {
            return ResultBody.failed().msg("验证码已失效");
        }
        // 判断验证码
        if (StringUtils.isBlank(verCode) || !redisCode.equals(verCode.trim().toLowerCase())) {
            redisUtil.del(verKey);
            return ResultBody.failed().msg("验证码输入有误");
        }
        redisUtil.del(verKey);
        return ResultBody.ok();
    }
}
