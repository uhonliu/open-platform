package com.opencloud.gateway.spring.server.locator;

import com.google.common.collect.Lists;
import com.opencloud.base.client.model.RateLimitApi;
import com.opencloud.base.client.model.entity.GatewayRoute;
import com.opencloud.common.event.RemoteRefreshRouteEvent;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义动态路由加载器
 *
 * @author liuyadu
 */
@Slf4j
public class JdbcRouteDefinitionLocator implements ApplicationListener<RemoteRefreshRouteEvent>, ApplicationEventPublisherAware {
    private JdbcTemplate jdbcTemplate;
    private ApplicationEventPublisher publisher;
    private InMemoryRouteDefinitionRepository repository;

    private final static String SELECT_ROUTES = "SELECT * FROM gateway_route WHERE status = 1";

    private final static String SELECT_LIMIT_PATH = "SELECT\n" +
            "        i.policy_id,\n" +
            "        p.limit_quota,\n" +
            "        p.interval_unit,\n" +
            "        p.policy_name,\n" +
            "        a.api_id,\n" +
            "        a.api_code,\n" +
            "        a.api_name,\n" +
            "        a.api_category,\n" +
            "        a.service_id,\n" +
            "        a.path,\n" +
            "        r.url\n" +
            "    FROM gateway_rate_limit_api AS i\n" +
            "    INNER JOIN gateway_rate_limit AS p ON i.policy_id = p.policy_id\n" +
            "    INNER JOIN base_api AS a ON i.api_id = a.api_id\n" +
            "    INNER JOIN gateway_route AS r ON a.service_id = r.route_name\n" +
            "    WHERE p.policy_type = 'url'";


    public JdbcRouteDefinitionLocator(JdbcTemplate jdbcTemplate, InMemoryRouteDefinitionRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    /**
     * 刷新路由
     *
     * @return
     */
    public Mono<Void> refresh() {
        this.loadRoutes();
        // 触发默认路由刷新事件,刷新缓存路由
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    /**
     * bus刷新事件
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(RemoteRefreshRouteEvent event) {
        refresh();
    }

    protected String getFullPath(List<GatewayRoute> routeList, String serviceId, String path) {
        final String s = path.startsWith("/") ? path : "/" + path;
        final String[] fullPath = {s};
        if (routeList != null) {
            routeList.forEach(route -> {
                if (StringUtils.isNotBlank(route.getRouteName()) && route.getRouteName().equals(serviceId)) {
                    fullPath[0] = route.getPath().replace("/**", s);
                }
            });
        }
        return fullPath[0];
    }

    /**
     * 动态加载路由
     * * 示例
     * id: uaa-admin-server
     * uri: lb://uaa-admin-server
     * predicates:
     * - Path=/admin/**
     * - Name=平台后台管理服务
     * filters:
     * #转发去掉前缀,总要否则swagger无法加载
     * - StripPrefix=1
     *
     * @return
     */
    private Mono<Void> loadRoutes() {
        //从数据库拿到路由配置
        try {
            List<GatewayRoute> routeList = jdbcTemplate.query(SELECT_ROUTES, (rs, i) -> {
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
            });
            List<RateLimitApi> limitApiList = jdbcTemplate.query(SELECT_LIMIT_PATH, (rs, i) -> {
                RateLimitApi result = new RateLimitApi();
                result.setPolicyId(rs.getLong("policy_id"));
                result.setPolicyName(rs.getString("policy_name"));
                result.setServiceId(rs.getString("service_id"));
                result.setPath(rs.getString("path"));
                result.setApiId(rs.getLong("api_id"));
                result.setApiCode(rs.getString("api_code"));
                result.setApiName(rs.getString("api_name"));
                result.setApiCategory(rs.getString("api_category"));
                result.setLimitQuota(rs.getLong("limit_quota"));
                result.setIntervalUnit(rs.getString("interval_unit"));
                result.setUrl(rs.getString("url"));
                return result;
            });
            if (limitApiList.size() > 0) {
                // 加载限流
                limitApiList.forEach(item -> {
                    long[] array = ResourceLocator.getIntervalAndQuota(item.getIntervalUnit());
                    Long refreshInterval = array[0];
                    Long quota = array[1];
                    // 允许用户每秒处理多少个请求
                    long replenishRate = item.getLimitQuota() / refreshInterval;
                    replenishRate = replenishRate < 1 ? 1 : refreshInterval;
                    // 令牌桶的容量，允许在一秒钟内完成的最大请求数
                    long burstCapacity = replenishRate * 2;
                    RouteDefinition definition = new RouteDefinition();
                    List<PredicateDefinition> predicates = Lists.newArrayList();
                    List<FilterDefinition> filters = Lists.newArrayList();
                    definition.setId(item.getApiId().toString());
                    PredicateDefinition predicatePath = new PredicateDefinition();
                    String fullPath = getFullPath(routeList, item.getServiceId(), item.getPath());
                    Map<String, String> predicatePathParams = new HashMap<>(8);
                    predicatePath.setName("Path");
                    predicatePathParams.put("pattern", fullPath);
                    predicatePathParams.put("pathPattern", fullPath);
                    predicatePathParams.put("_rateLimit", "1");
                    predicatePath.setArgs(predicatePathParams);
                    predicates.add(predicatePath);

                    // 服务地址
                    URI uri = UriComponentsBuilder.fromUriString(StringUtils.isNotBlank(item.getUrl()) ? item.getUrl() : "lb://" + item.getServiceId()).build().toUri();

                    // 路径去前缀
                    FilterDefinition stripPrefixDefinition = new FilterDefinition();
                    Map<String, String> stripPrefixParams = new HashMap<>(8);
                    stripPrefixDefinition.setName("StripPrefix");
                    stripPrefixParams.put(NameUtils.GENERATED_NAME_PREFIX + "0", "1");
                    stripPrefixDefinition.setArgs(stripPrefixParams);
                    filters.add(stripPrefixDefinition);
                    // 限流
                    FilterDefinition rateLimiterDefinition = new FilterDefinition();
                    Map<String, String> rateLimiterParams = new HashMap<>(8);
                    rateLimiterDefinition.setName("RequestRateLimiter");
                    //令牌桶流速
                    rateLimiterParams.put("redis-rate-limiter.replenishRate", String.valueOf(replenishRate));
                    //令牌桶容量
                    rateLimiterParams.put("redis-rate-limiter.burstCapacity", String.valueOf(burstCapacity));
                    // 限流策略(#{@BeanName})
                    rateLimiterParams.put("key-resolver", "#{@pathKeyResolver}");
                    rateLimiterDefinition.setArgs(rateLimiterParams);
                    filters.add(rateLimiterDefinition);

                    definition.setPredicates(predicates);
                    definition.setFilters(filters);
                    definition.setUri(uri);
                    this.repository.save(Mono.just(definition)).subscribe();
                });
            }
            if (routeList.size() > 0) {
                // 最后加载路由
                routeList.forEach(gatewayRoute -> {
                    RouteDefinition definition = new RouteDefinition();
                    List<PredicateDefinition> predicates = Lists.newArrayList();
                    List<FilterDefinition> filters = Lists.newArrayList();
                    definition.setId(gatewayRoute.getRouteName());
                    // 路由地址
                    PredicateDefinition predicatePath = new PredicateDefinition();
                    Map<String, String> predicatePathParams = new HashMap<>(8);
                    predicatePath.setName("Path");
                    predicatePathParams.put("name", StringUtils.isBlank(gatewayRoute.getRouteName()) ? gatewayRoute.getRouteId().toString() : gatewayRoute.getRouteName());
                    predicatePathParams.put("pattern", gatewayRoute.getPath());
                    predicatePathParams.put("pathPattern", gatewayRoute.getPath());
                    predicatePath.setArgs(predicatePathParams);
                    predicates.add(predicatePath);
                    // 服务地址
                    URI uri = UriComponentsBuilder.fromUriString(StringUtils.isNotBlank(gatewayRoute.getUrl()) ? gatewayRoute.getUrl() : "lb://" + gatewayRoute.getServiceId()).build().toUri();

                    FilterDefinition stripPrefixDefinition = new FilterDefinition();
                    Map<String, String> stripPrefixParams = new HashMap<>(8);
                    stripPrefixDefinition.setName("StripPrefix");
                    stripPrefixParams.put(NameUtils.GENERATED_NAME_PREFIX + "0", "1");
                    stripPrefixDefinition.setArgs(stripPrefixParams);
                    filters.add(stripPrefixDefinition);

                    definition.setPredicates(predicates);
                    definition.setFilters(filters);
                    definition.setUri(uri);
                    this.repository.save(Mono.just(definition)).subscribe();
                });
            }
            log.info("=============加载动态路由:{}==============", routeList.size());
            log.info("=============加载动态限流:{}==============", limitApiList.size());
        } catch (Exception e) {
            log.error("加载动态路由错误:", e);
        }
        return Mono.empty();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
