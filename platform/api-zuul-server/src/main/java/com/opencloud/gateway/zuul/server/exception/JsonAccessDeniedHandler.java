package com.opencloud.gateway.zuul.server.exception;

import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import com.opencloud.gateway.zuul.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关权限异常处理,记录日志
 *
 * @author liuyadu
 */
@Slf4j
public class JsonAccessDeniedHandler implements AccessDeniedHandler {
    private AccessLogService accessLogService;

    public JsonAccessDeniedHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        response.setStatus(resultBody.getHttpStatus());
        // 保存日志
        accessLogService.sendLog(request, response, exception);
        WebUtils.writeJson(response, resultBody);
    }
}
