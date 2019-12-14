package com.opencloud.gateway.zuul.server.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opencloud.common.exception.OpenGlobalExceptionHandler;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.common.utils.WebUtils;
import com.opencloud.gateway.zuul.server.service.AccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * zuul错误过滤器
 *
 * @author liuyadu
 */
@Slf4j
public class ZuulErrorFilter extends ZuulFilter {
    private AccessLogService accessLogService;

    public ZuulErrorFilter(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        // 代理错误日志记录
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();
        Exception exception = new Exception(ctx.getThrowable());
        if (StringUtils.toBoolean(ctx.get("rateLimitExceeded"))) {
            exception = new Exception(HttpStatus.TOO_MANY_REQUESTS.name());
        }
        accessLogService.sendLog(request, response, exception);
        ResultBody resultBody = OpenGlobalExceptionHandler.resolveException(exception, request.getRequestURI());
        WebUtils.writeJson(response, resultBody);
        return null;
    }
}
