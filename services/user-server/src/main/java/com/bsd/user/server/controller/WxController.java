package com.bsd.user.server.controller;

import com.bsd.user.server.constants.DeveloperConstants;
import com.bsd.user.server.model.dto.JsSdkSignDTO;
import com.bsd.user.server.service.DeveloperService;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.StringUtils;
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

/**
 * 开放平台
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 12:02
 */
@Slf4j
@Api(tags = "开放平台")
@RestController
@RequestMapping("/wx")
public class WxController {
    @Autowired
    private DeveloperService developerService;

    @ApiOperation(value = "获取微信JS-SDK权限验证配置", notes = "获取微信JS-SDK权限验证配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", required = true, value = "需要获取权限的URL地址", example = "http://www.xxx.com", paramType = "form")
    })
    @PostMapping("/js/sdk/conf")
    public ResultBody<JsSdkSignDTO> init(@RequestParam(value = "url") String url) {
        //简单校验URL地址
        if (StringUtils.isEmpty(url)) {
            return ResultBody.failed().msg("URL地址不能为空!");
        }
        if (!url.startsWith(DeveloperConstants.HTTP_PREFIX) && !url.startsWith(DeveloperConstants.HTTPS_PREFIX)) {
            return ResultBody.failed().msg("URL地址有误,请确认地址正确性!");
        }
        //获取签名授权
        JsSdkSignDTO jsSdkSignDTO = developerService.makeWxJsSdkSign(url);
        return ResultBody.ok().data(jsSdkSignDTO);
    }
}
