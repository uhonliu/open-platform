package com.bsd.dingtalk.server.controller;

import com.bsd.dingtalk.server.configuration.DingtalkProperties;
import com.bsd.dingtalk.server.service.UserService;
import com.bsd.dingtalk.server.util.AccessTokenUtil;
import com.bsd.dingtalk.server.util.ContactHelper;
import com.google.common.collect.Maps;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 企业内部E应用Quick-Start示例代码 实现了最简单的免密登录（免登）功能
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Api(tags = "钉钉服务接口")
@EnableConfigurationProperties({DingtalkProperties.class})
@RestController
public class IndexController {
    @Autowired
    private DingtalkProperties dingtalkProperties;

    @Autowired
    private UserService userService;

    /**
     * 获取钉钉登录配置
     *
     * @return
     */
    @ApiOperation(value = "获取钉钉登录配置", notes = "任何人都可访问")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", required = true, value = "公司ID", paramType = "form")
    })
    @GetMapping("/login/config")
    @ResponseBody
    public ResultBody getLoginOtherConfig() {
        Map<String, String> map = Maps.newHashMap();
        map.put("dingtalk", AccessTokenUtil.getAuthorizationUrl(dingtalkProperties.getAppkey(), "http://bsd.vaiwan.com/dingtalk/login"));
        return ResultBody.ok().data(map);
    }

    /**
     * 钉钉用户登录，显示当前登录用户的userId和名称
     *
     * @param authCode 免登临时code
     */
    @ApiOperation(value = "钉钉免登", notes = "钉钉用户登录，显示当前登录用户的userId和名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authCode", required = true, value = "临时code", paramType = "form")
    })
    @PostMapping(value = "/login")
    @ResponseBody
    public ResultBody login(@RequestParam(value = "authCode") String authCode) {
        try {
            //获取accessToken
            String accessToken = AccessTokenUtil.getToken(dingtalkProperties.getAppkey(), dingtalkProperties.getAppsecret());

            //查询得到当前用户的userId
            // 获得到userId之后应用应该处理应用自身的登录会话管理（session）,避免后续的业务交互（前端到应用服务端）每次都要重新获取用户身份，提升用户体验
            String userId = AccessTokenUtil.getUserId(accessToken, authCode);

            String userName = ContactHelper.getUserInfo(accessToken, userId).getName();

            //返回结果
            Map resultMap = Maps.newHashMap();
            resultMap.put("userId", userId);
            resultMap.put("userName", userName);
            return ResultBody.ok().data(resultMap);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 钉钉组织架构同步
     */
    @ApiOperation(value = "钉钉组织架构同步", notes = "同步钉钉部门和用户")
    @GetMapping(value = "/org/syn")
    public ResultBody synOrg() {
        userService.synUserInfoByDingding();
        return ResultBody.ok();
    }
}


