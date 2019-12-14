package com.opencloud.gateway.zuul.server.locator;

import com.google.common.collect.Maps;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.common.event.RemoteRefreshRouteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 自定义动态路由加载器
 *
 * @author: liuyadu
 * @date: 2018/10/23 10:31
 * @description:
 */
@Slf4j
public class JdbcRouteLocator extends SimpleRouteLocator implements ApplicationListener<RemoteRefreshRouteEvent> {
    private JdbcTemplate jdbcTemplate;
    private ZuulProperties properties;
    private List<GatewayRoute> routeList;
    private ApplicationEventPublisher publisher;

    public JdbcRouteLocator(String servletPath, ZuulProperties properties, JdbcTemplate jdbcTemplate, ApplicationEventPublisher publisher) {
        super(servletPath, properties);
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        this.publisher = publisher;
    }

    @Override
    public void doRefresh() {
        super.doRefresh();
        // 发布本地刷新事件, 更新相关本地缓存, 解决动态加载完,新路由映射无效的问题
        publisher.publishEvent(new RoutesRefreshedEvent(this));
    }

    /**
     * 加载路由配置
     *
     * @return
     */
    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = Maps.newLinkedHashMap();
        routesMap.putAll(super.locateRoutes());
        //从db中加载路由信息
        routesMap.putAll(loadRoutes());
        //优化一下配置
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = Maps.newLinkedHashMap();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    /**
     * 加载路由配置
     */
    public Map<String, ZuulRoute> loadRoutes() {
        Map<String, ZuulProperties.ZuulRoute> routes = Maps.newLinkedHashMap();
        routeList = new CopyOnWriteArrayList<>();
        try {
            routeList = jdbcTemplate.query("SELECT * FROM gateway_route WHERE status = 1", new RowMapper<GatewayRoute>() {
                @Override
                public GatewayRoute mapRow(ResultSet rs, int i) throws SQLException {
                    GatewayRoute result = new GatewayRoute();
                    result.setRouteId(rs.getLong("route_id"));
                    result.setPath(rs.getString("path"));
                    result.setServiceId(rs.getString("service_id"));
                    result.setUrl(rs.getString("url"));
                    result.setStatus(rs.getInt("status"));
                    result.setRetryable(rs.getInt("retryable"));
                    result.setStripPrefix(rs.getInt("strip_prefix"));
                    result.setIsPersist(rs.getInt("is_persist"));
                    result.setRouteName(rs.getString("route_name"));
                    result.setRouteType(rs.getString("route_type"));
                    return result;
                }
            });
            if (routeList != null && routeList.size() > 0) {
                for (GatewayRoute result : routeList) {
                    if (StringUtils.isEmpty(result.getPath())) {
                        continue;
                    }
                    if (StringUtils.isEmpty(result.getServiceId()) && StringUtils.isEmpty(result.getUrl())) {
                        continue;
                    }
                    ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();

                    BeanUtils.copyProperties(result, zuulRoute);
                    zuulRoute.setId(result.getRouteName());
                    routes.put(zuulRoute.getPath(), zuulRoute);
                }
            }
            log.info("=============加载动态路由:{}==============", routeList.size());
        } catch (Exception e) {
            log.error("加载动态路由错误:", e);
        }
        return routes;
    }

    public List<GatewayRoute> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<GatewayRoute> routeList) {
        this.routeList = routeList;
    }

    /**
     * 远程刷新事件
     *
     * @param gatewayRemoteRefreshRouteEvent
     */
    @Override
    public void onApplicationEvent(RemoteRefreshRouteEvent gatewayRemoteRefreshRouteEvent) {
        doRefresh();
    }
}
