package com.opencloud.gateway.zuul.server.fallback;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencloud.common.constants.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.ResourceBundle;

/**
 * @author liujianhong
 */
public class BlockFallbackProvider implements ZuulBlockFallbackProvider {
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public BlockResponse fallbackResponse(String route, Throwable throwable) {
        if (throwable instanceof BlockException) {
            return new BlockResponse(HttpStatus.TOO_MANY_REQUESTS.value(), i18n(ErrorCode.TOO_MANY_REQUESTS.getMessage()), route);
        } else {
            return new BlockResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), i18n(ErrorCode.ERROR.getMessage()), route);
        }
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private String i18n(String message) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("error");
        return resourceBundle.containsKey(message) ? resourceBundle.getString(message) : message;
    }
}
