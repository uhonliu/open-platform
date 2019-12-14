package com.opencloud.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.Jackson2ArrayOrStringDeserializer;
import org.springframework.security.oauth2.provider.client.JacksonArrayOrStringDeserializer;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 自定义客户端信息
 *
 * @author: liuyadu
 * @date: 2019/5/30 18:07
 * @description:
 */
@org.codehaus.jackson.map.annotate.JsonSerialize(include = org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_DEFAULT)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class OpenClientDetails implements ClientDetails, Serializable {
    private static final long serialVersionUID = -4888527753331687039L;

    @org.codehaus.jackson.annotate.JsonProperty("client_id")
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;

    @org.codehaus.jackson.annotate.JsonProperty("client_secret")
    @com.fasterxml.jackson.annotation.JsonProperty("client_secret")
    private String clientSecret;

    @org.codehaus.jackson.map.annotate.JsonDeserialize(using = JacksonArrayOrStringDeserializer.class)
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> scope = Collections.emptySet();

    @org.codehaus.jackson.annotate.JsonProperty("resource_ids")
    @org.codehaus.jackson.map.annotate.JsonDeserialize(using = JacksonArrayOrStringDeserializer.class)
    @com.fasterxml.jackson.annotation.JsonProperty("resource_ids")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> resourceIds = Collections.emptySet();

    @org.codehaus.jackson.annotate.JsonProperty("authorized_grant_types")
    @org.codehaus.jackson.map.annotate.JsonDeserialize(using = JacksonArrayOrStringDeserializer.class)
    @com.fasterxml.jackson.annotation.JsonProperty("authorized_grant_types")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> authorizedGrantTypes = Collections.emptySet();

    @org.codehaus.jackson.annotate.JsonProperty("redirect_uri")
    @org.codehaus.jackson.map.annotate.JsonDeserialize(using = JacksonArrayOrStringDeserializer.class)
    @com.fasterxml.jackson.annotation.JsonProperty("redirect_uri")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> registeredRedirectUris;

    @org.codehaus.jackson.annotate.JsonProperty("autoapprove")
    @org.codehaus.jackson.map.annotate.JsonDeserialize(using = JacksonArrayOrStringDeserializer.class)
    @com.fasterxml.jackson.annotation.JsonProperty("autoapprove")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Jackson2ArrayOrStringDeserializer.class)
    private Set<String> autoApproveScopes;

    private List<GrantedAuthority> authorities = Collections.emptyList();

    @org.codehaus.jackson.annotate.JsonProperty("access_token_validity")
    @com.fasterxml.jackson.annotation.JsonProperty("access_token_validity")
    private Integer accessTokenValiditySeconds;

    @org.codehaus.jackson.annotate.JsonProperty("refresh_token_validity")
    @com.fasterxml.jackson.annotation.JsonProperty("refresh_token_validity")
    private Integer refreshTokenValiditySeconds;

    @org.codehaus.jackson.annotate.JsonProperty("additional_information")
    @com.fasterxml.jackson.annotation.JsonProperty("additional_information")
    private Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();

    public OpenClientDetails() {
    }

    public OpenClientDetails(ClientDetails prototype) {
        this();
        setAccessTokenValiditySeconds(prototype.getAccessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(prototype.getRefreshTokenValiditySeconds());
        setAuthorities(prototype.getAuthorities());
        setAuthorizedGrantTypes(prototype.getAuthorizedGrantTypes());
        setClientId(prototype.getClientId());
        setClientSecret(prototype.getClientSecret());
        setRegisteredRedirectUri(prototype.getRegisteredRedirectUri());
        setScope(prototype.getScope());
        setResourceIds(prototype.getResourceIds());
    }

    public OpenClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities) {
        this(clientId, resourceIds, scopes, grantTypes, authorities, null);
    }

    public OpenClientDetails(String clientId, String resourceIds, String scopes, String grantTypes, String authorities, String redirectUris) {
        this.clientId = clientId;

        if (StringUtils.hasText(resourceIds)) {
            Set<String> resources = StringUtils.commaDelimitedListToSet(resourceIds);
            if (!resources.isEmpty()) {
                this.resourceIds = resources;
            }
        }

        if (StringUtils.hasText(scopes)) {
            Set<String> scopeList = StringUtils.commaDelimitedListToSet(scopes);
            if (!scopeList.isEmpty()) {
                this.scope = scopeList;
            }
        }

        if (StringUtils.hasText(grantTypes)) {
            this.authorizedGrantTypes = StringUtils.commaDelimitedListToSet(grantTypes);
        } else {
            this.authorizedGrantTypes = new HashSet<String>(Arrays.asList("authorization_code", "refresh_token"));
        }

        if (StringUtils.hasText(authorities)) {
            this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
        }

        if (StringUtils.hasText(redirectUris)) {
            this.registeredRedirectUris = StringUtils.commaDelimitedListToSet(redirectUris);
        }
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAutoApproveScopes(Collection<String> autoApproveScopes) {
        this.autoApproveScopes = new HashSet<String>(autoApproveScopes);
    }

    @Override
    public boolean isAutoApprove(String scope) {
        if (autoApproveScopes == null) {
            return false;
        }
        for (String auto : autoApproveScopes) {
            if ("true".equals(auto) || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }

    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getAutoApproveScopes() {
        return autoApproveScopes;
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Collection<String> scope) {
        this.scope = scope == null ? Collections.<String>emptySet() : new LinkedHashSet<String>(scope);
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Collection<String> resourceIds) {
        this.resourceIds = resourceIds == null ? Collections.<String>emptySet() : new LinkedHashSet<String>(resourceIds);
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes) {
        this.authorizedGrantTypes = new LinkedHashSet<String>(authorizedGrantTypes);
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUris;
    }

    public void setRegisteredRedirectUri(Set<String> registeredRedirectUris) {
        this.registeredRedirectUris = registeredRedirectUris == null ? null : new LinkedHashSet<String>(registeredRedirectUris);
    }

    @org.codehaus.jackson.annotate.JsonProperty("authorities")
    @com.fasterxml.jackson.annotation.JsonProperty("authorities")
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return Collections.EMPTY_LIST;
        }
        return this.authorities;
    }

    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = new ArrayList<GrantedAuthority>(authorities);
    }

    @org.codehaus.jackson.annotate.JsonProperty("authorities")
    @com.fasterxml.jackson.annotation.JsonProperty("authorities")
    public void setAuthoritiesExt(Collection<OpenAuthority> authorities) {
        this.authorities = new ArrayList<GrantedAuthority>(authorities);
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    @org.codehaus.jackson.annotate.JsonIgnore
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setAdditionalInformation(Map<String, ?> additionalInformation) {
        this.additionalInformation = new LinkedHashMap<String, Object>(additionalInformation);
    }

    @org.codehaus.jackson.annotate.JsonAnySetter
    @com.fasterxml.jackson.annotation.JsonAnySetter
    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.unmodifiableMap(this.additionalInformation);
    }

    @org.codehaus.jackson.annotate.JsonAnySetter
    @com.fasterxml.jackson.annotation.JsonAnySetter
    public void addAdditionalInformation(String key, Object value) {
        this.additionalInformation.put(key, value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessTokenValiditySeconds == null) ? 0 : accessTokenValiditySeconds);
        result = prime * result + ((refreshTokenValiditySeconds == null) ? 0 : refreshTokenValiditySeconds);
        result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
        result = prime * result + ((authorizedGrantTypes == null) ? 0 : authorizedGrantTypes.hashCode());
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
        result = prime * result + ((registeredRedirectUris == null) ? 0 : registeredRedirectUris.hashCode());
        result = prime * result + ((resourceIds == null) ? 0 : resourceIds.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((additionalInformation == null) ? 0 : additionalInformation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OpenClientDetails other = (OpenClientDetails) obj;
        if (accessTokenValiditySeconds == null) {
            if (other.accessTokenValiditySeconds != null) {
                return false;
            }
        } else if (!accessTokenValiditySeconds.equals(other.accessTokenValiditySeconds)) {
            return false;
        }
        if (refreshTokenValiditySeconds == null) {
            if (other.refreshTokenValiditySeconds != null) {
                return false;
            }
        } else if (!refreshTokenValiditySeconds.equals(other.refreshTokenValiditySeconds)) {
            return false;
        }
        if (authorities == null) {
            if (other.authorities != null) {
                return false;
            }
        } else if (!authorities.equals(other.authorities)) {
            return false;
        }
        if (authorizedGrantTypes == null) {
            if (other.authorizedGrantTypes != null) {
                return false;
            }
        } else if (!authorizedGrantTypes.equals(other.authorizedGrantTypes)) {
            return false;
        }
        if (clientId == null) {
            if (other.clientId != null) {
                return false;
            }
        } else if (!clientId.equals(other.clientId)) {
            return false;
        }
        if (clientSecret == null) {
            if (other.clientSecret != null) {
                return false;
            }
        } else if (!clientSecret.equals(other.clientSecret)) {
            return false;
        }
        if (registeredRedirectUris == null) {
            if (other.registeredRedirectUris != null) {
                return false;
            }
        } else if (!registeredRedirectUris.equals(other.registeredRedirectUris)) {
            return false;
        }
        if (resourceIds == null) {
            if (other.resourceIds != null) {
                return false;
            }
        } else if (!resourceIds.equals(other.resourceIds)) {
            return false;
        }
        if (scope == null) {
            if (other.scope != null) {
                return false;
            }
        } else if (!scope.equals(other.scope)) {
            return false;
        }
        if (additionalInformation == null) {
            if (other.additionalInformation != null) {
                return false;
            }
        } else if (!additionalInformation.equals(other.additionalInformation)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BaseClientDetails [clientId=" + clientId + ", clientSecret="
                + clientSecret + ", scope=" + scope + ", resourceIds="
                + resourceIds + ", authorizedGrantTypes="
                + authorizedGrantTypes + ", registeredRedirectUris="
                + registeredRedirectUris + ", authorities=" + authorities
                + ", accessTokenValiditySeconds=" + accessTokenValiditySeconds
                + ", refreshTokenValiditySeconds="
                + refreshTokenValiditySeconds + ", additionalInformation="
                + additionalInformation + "]";
    }
}
