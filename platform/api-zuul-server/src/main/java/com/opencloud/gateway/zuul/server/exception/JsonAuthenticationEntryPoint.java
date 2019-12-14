package com.opencloud.gateway.zuul.server.exception;

import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import com.opencloud.gateway.zuul.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关认证异常处理,记录日志
 *
 * @author liuyadu
 */
@Slf4j
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private AccessLogService accessLogService;

    public JsonAuthenticationEntryPoint(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        response.setStatus(resultBody.getHttpStatus());
        // 保存日志
        accessLogService.sendLog(request, response, exception);
        WebUtils.writeJson(response, resultBody);
    }
}
