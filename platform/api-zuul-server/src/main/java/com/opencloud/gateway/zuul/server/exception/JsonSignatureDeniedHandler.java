package com.opencloud.gateway.zuul.server.exception;

import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import com.opencloud.gateway.zuul.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义签名错误处理器
 *
 * @author liuyadu
 */
@Slf4j
public class JsonSignatureDeniedHandler implements SignatureDeniedHandler {
    private AccessLogService accessLogService;

    public JsonSignatureDeniedHandler(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        response.setStatus(resultBody.getHttpStatus());
        // 保存日志
        accessLogService.sendLog(request, response, exception);
        WebUtils.writeJson(response, resultBody);
    }
}