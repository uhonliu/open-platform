package com.bsd.user.server.filter;

import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.utils.JwtTokenUtils;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.utils.RedisUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录token拦截器
 * 拦截用户中心某些需要获取用户信息接口功能
 *
 * @author lisongmao
 * 2019年6月29日
 */
@Component
@Slf4j
public class LoginTokenHandlerInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtils<Object> redisUtils;

    /**
     * 执行handler完成后执行
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) {

    }

    /**
     * 进入handler方法之后返回modelAndView之前执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) {
        System.out.println("HandlerInterceptor1-->postHandle");
    }

    /**
     * 进入handler方法之前执行
     * 用于身份认证/授权，如果身份认证不通过，需要此方法拦截不在向下执行
     * 返回false表示拦截不向下执行，返回true表示向下执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) {
        String jwtToken = request.getHeader(UserConstants.TOKEN_NAME);
        String sessionId = request.getHeader(UserConstants.SESSIONID);
        log.info("{}被拦截了", request.getRequestURI());
        String realToken = redisUtils.get(sessionId);
        if (realToken == null || !realToken.equals(jwtToken)) {
            throw new OpenAlertException("token错误");
        }
        //解析客户端传入的token
        Claims jwtClaims = JwtTokenUtils.parseToken(UserConstants.PUBLIC_KEY_SALT_VUALE, jwtToken);
        request.setAttribute(UserConstants.LOGIN_MOBILE, jwtClaims.get(JwtTokenUtils.JWT_ATTRIBUTE_MOBLIE).toString());
        Map<String, Object> loginInfoMap = new HashMap<>();
        loginInfoMap.put(UserConstants.LOGIN_MOBILE, jwtClaims.get(JwtTokenUtils.JWT_ATTRIBUTE_MOBLIE).toString());
        loginInfoMap.put(UserConstants.SESSIONID, jwtClaims.get(JwtTokenUtils.JWT_ATTRIBUTE_SESSIONID).toString());
        loginInfoMap.put(UserConstants.TOKEN_NAME, realToken);
        request.setAttribute(UserConstants.LOGIN_INFO, loginInfoMap);
        log.info("{}被拦截了,入参sessionId：{}，入参token：{}，token解析结果：loginMobile：{}，loginInfo：{}",
                request.getRequestURI(), sessionId, realToken, request.getAttribute(UserConstants.LOGIN_MOBILE), loginInfoMap.toString());
        return true;
    }
}
