package com.opencloud.gateway.spring.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * 转发路由前置过滤器
 *
 * @author liuyadu
 */
@Slf4j
public class RouteToUrlFilter implements GlobalFilter, Ordered {
    static final Pattern schemePattern = Pattern.compile("[a-zA-Z]([a-zA-Z]|\\d|\\+|\\.|-)*:.*");

    static boolean hasAnotherScheme(URI uri) {
        return schemePattern.matcher(uri.getSchemeSpecificPart()).matches() && uri.getHost() == null && uri.getRawPath() == null;
    }

    @Override
    public int getOrder() {
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return chain.filter(exchange);
        } else {
            log.trace("RouteToRequestUrlFilter start");
            URI uri = exchange.getRequest().getURI();
            boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
            URI routeUri = route.getUri();
            if (hasAnotherScheme(routeUri)) {
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR, routeUri.getScheme());
                routeUri = URI.create(routeUri.getSchemeSpecificPart());
            }

            if ("lb".equalsIgnoreCase(routeUri.getScheme()) && routeUri.getHost() == null) {
                throw new IllegalStateException("Invalid host: " + routeUri.toString());
            } else {
                URI mergedUrl = UriComponentsBuilder.fromUriString(routeUri.toString() + uri.getPath()).query(uri.getRawQuery()).build(encoded).toUri();
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, mergedUrl);
                return chain.filter(exchange);
            }
        }
    }
}
