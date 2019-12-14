package com.opencloud.base.server.mapper;

import com.opencloud.base.client.model.AuthorityMenu;
import com.opencloud.base.client.model.entity.BaseAuthorityUser;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import com.opencloud.common.security.OpenAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityUserMapper extends SuperMapper<BaseAuthorityUser> {
    /**
     * 获取用户已授权权限
     *
     * @param userId
     * @return
     */
    List<OpenAuthority> selectAuthorityByUser(@Param("userId") Long userId);

    /**
     * 获取用户已授权权限完整信息
     *
     * @param userId
     * @param serviceId
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenuByUser(@Param("userId") Long userId, @Param("serviceId") String serviceId);
}
