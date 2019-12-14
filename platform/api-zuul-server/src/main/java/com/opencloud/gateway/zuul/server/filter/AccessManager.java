package com.opencloud.gateway.zuul.server.filter;

import com.opencloud.base.client.model.AuthorityResource;
import com.opencloud.common.constants.CommonConstants;
import com.opencloud.common.constants.ErrorCode;
import com.opencloud.common.security.OpenAuthority;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.gateway.zuul.server.configuration.ApiProperties;
import com.opencloud.gateway.zuul.server.locator.ResourceLocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 访问权限控制管理类
 *
 * @author liuyadu
 */
@Slf4j
@Component
public class AccessManager {
    private ResourceLocator resourceLocator;

    private ApiProperties apiProperties;

    private static final AntPathMatcher pathMatch = new AntPathMatcher();

    private Set<String> permitAll = new HashSet<>();

    private Set<String> authorityIgnores = new HashSet<>();


    public AccessManager(ResourceLocator resourceLocator, ApiProperties apiProperties) {
        this.apiProperties = apiProperties;
        this.resourceLocator = resourceLocator;
        // 默认放行
        permitAll.add("/");
        permitAll.add("/error");
        permitAll.add("/favicon.ico");
        if (apiProperties != null) {
            if (apiProperties.getPermitAll() != null) {
                permitAll.addAll(apiProperties.getPermitAll());
            }
            if (apiProperties.getApiDebug()) {
                permitAll.add("/**/v2/api-docs/**");
                permitAll.add("/**/swagger-resources/**");
                permitAll.add("/webjars/**");
                permitAll.add("/doc.html");
                permitAll.add("/swagger-ui.html");
            }
            if (apiProperties.getAuthorityIgnores() != null) {
                authorityIgnores.addAll(apiProperties.getAuthorityIgnores());
            }
        }
    }

    /**
     * 权限验证
     *
     * @param request
     * @param authentication
     * @return
     */
    public boolean check(HttpServletRequest request, Authentication authentication) {
        if (!apiProperties.getAccessControl()) {
            return true;
        }
        String requestPath = getRequestPath(request);
        // 是否直接放行
        if (permitAll(requestPath)) {
            return true;
        }
        return checkAuthorities(request, authentication, requestPath);
    }

    /**
     * 始终放行
     *
     * @param requestPath
     * @return
     */
    public boolean permitAll(String requestPath) {
        boolean permit = permitAll.stream()
                .filter(r -> pathMatch.match(r, requestPath)).findFirst().isPresent();
        if (permit) {
            return true;
        }
        // 动态权限列表
        return resourceLocator.getAuthorityResources().stream()
                .filter(res -> StringUtils.isNotBlank(res.getPath()))
                .filter(res -> {
                    boolean isAuth = res.getIsAuth() != null && res.getIsAuth().intValue() == 1 ? true : false;
                    // 无需认证,返回true
                    return pathMatch.match(res.getPath(), requestPath) && !isAuth;
                }).findFirst().isPresent();
    }

    /**
     * 获取资源状态
     *
     * @param requestPath
     * @return
     */
    public AuthorityResource getResource(String requestPath) {
        // 动态权限列表
        return resourceLocator.getAuthorityResources()
                .stream()
                .filter(r -> StringUtils.isNotBlank(r.getPath()))
                .filter(r -> !"/**".equals(r.getPath()))
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .findFirst().orElse(null);
    }

    /**
     * 忽略鉴权
     *
     * @param requestPath
     * @return
     */
    private boolean authorityIgnores(String requestPath) {
        return authorityIgnores.stream()
                .filter(r -> pathMatch.match(r, requestPath))
                .findFirst().isPresent();
    }

    /**
     * 检查权限
     *
     * @param request
     * @param authentication
     * @param requestPath
     * @return
     */
    private boolean checkAuthorities(HttpServletRequest request, Authentication authentication, String requestPath) {
        Object principal = authentication.getPrincipal();
        // 已认证身份
        if (principal != null) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                //check if this uri can be access by anonymous
                //return
            }
            if (authorityIgnores(requestPath)) {
                // 认证通过,并且无需权限
                return true;
            }
            return mathAuthorities(request, authentication, requestPath);
        }
        return false;
    }

    /**
     * 权限验证
     *
     * @param request
     * @param authentication
     * @param requestPath
     * @return
     */
    public boolean mathAuthorities(HttpServletRequest request, Authentication authentication, String requestPath) {
        Collection<ConfigAttribute> attributes = getAttributes(requestPath);
        int result = 0;
        int expires = 0;
        if (authentication == null) {
            return false;
        } else {
            if (CommonConstants.ROOT.equals(authentication.getName())) {
                // 默认超级管理员账号,直接放行
                return true;
            }
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator var6 = attributes.iterator();
            while (var6.hasNext()) {
                ConfigAttribute attribute = (ConfigAttribute) var6.next();
                Iterator var8 = authorities.iterator();
                while (var8.hasNext()) {
                    GrantedAuthority authority = (GrantedAuthority) var8.next();
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
                        result++;
                        if (authority instanceof OpenAuthority) {
                            OpenAuthority customer = (OpenAuthority) authority;
                            if (customer.getIsExpired() != null && customer.getIsExpired()) {
                                // 授权过期数
                                expires++;
                            }
                        }
                    }
                }
            }
            log.debug("mathAuthorities result[{}] expires[{}]", result, expires);
            if (expires > 0) {
                // 授权已过期
                throw new AccessDeniedException(ErrorCode.ACCESS_DENIED_AUTHORITY_EXPIRED.getMessage());
            }
            return result > 0;
        }
    }

    /**
     * 获取请求资源所需权限列表
     *
     * @param requestPath
     * @return
     */
    private Collection<ConfigAttribute> getAttributes(String requestPath) {
        // 匹配动态权限
        AtomicReference<Collection<ConfigAttribute>> attributes = new AtomicReference<>();
        resourceLocator.getConfigAttributes().keySet().stream()
                .filter(r -> !"/**".equals(r))
                .filter(r -> pathMatch.match(r, requestPath))
                .findFirst().ifPresent(r -> {
            attributes.set(resourceLocator.getConfigAttributes().get(r));
        });
        if (attributes.get() != null) {
            return attributes.get();
        }
        return SecurityConfig.createList("AUTHORITIES_REQUIRED");
    }

    /**
     * IP黑名单验证
     *
     * @param requestPath
     * @param ipAddress
     * @param origin
     * @return
     */
    public boolean matchIpOrOriginBlacklist(String requestPath, String ipAddress, String origin) {
        return resourceLocator.getIpBlacks().stream()
                .filter(r -> StringUtils.isNotEmpty(r.getPath()))
                .filter(r -> r.getIpAddressSet() != null && !r.getIpAddressSet().isEmpty())
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .filter(r -> matchIpOrOrigin(r.getIpAddressSet(), ipAddress, origin))
                .findFirst().isPresent();
    }

    /**
     * 白名单验证
     *
     * @param requestPath
     * @param ipAddress
     * @param origin
     * @return [hasWhiteList, allow]
     */
    public Boolean[] matchIpOrOriginWhiteList(String requestPath, String ipAddress, String origin) {
        final Boolean[] result = {false, false};
        resourceLocator.getIpWhites().stream()
                .filter(r -> StringUtils.isNotEmpty(r.getPath()))
                .filter(r -> r.getIpAddressSet() != null && !r.getIpAddressSet().isEmpty())
                .filter(r -> pathMatch.match(r.getPath(), requestPath))
                .findFirst().ifPresent(r -> {
            result[0] = true;
            result[1] = matchIpOrOrigin(r.getIpAddressSet(), ipAddress, origin);
        });
        return result;
    }

    /**
     * 匹配IP或域名
     *
     * @param values
     * @param ipAddress
     * @param origin
     * @return
     */
    public boolean matchIpOrOrigin(Set<String> values, String ipAddress, String origin) {
        IpAddressMatcher ipAddressMatcher = null;
        for (String value : values) {
            if (StringUtils.matchIp(value)) {
                ipAddressMatcher = new IpAddressMatcher(value);
                if (ipAddressMatcher.matches(ipAddress)) {
                    return true;
                }
            } else {
                if (StringUtils.matchDomain(value) && StringUtils.isNotBlank(origin) && origin.contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }


    public String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.isNotBlank(url) ? url + pathInfo : pathInfo;
        }
        return url;
    }

    public ApiProperties getApiProperties() {
        return apiProperties;
    }

    public void setApiProperties(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }
}
