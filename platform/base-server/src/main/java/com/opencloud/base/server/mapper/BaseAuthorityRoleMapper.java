package com.opencloud.base.server.mapper;

import com.opencloud.base.client.model.AuthorityMenu;
import com.opencloud.base.client.model.entity.BaseAuthorityRole;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import com.opencloud.common.security.OpenAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityRoleMapper extends SuperMapper<BaseAuthorityRole> {
    /**
     * 获取角色已授权权限
     *
     * @param roleId
     * @return
     */
    List<OpenAuthority> selectAuthorityByRole(@Param("roleId") Long roleId);

    /**
     * 获取角色菜单权限
     *
     * @param roleId
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenuByRole(@Param("roleId") Long roleId, @Param("serviceId") String serviceId);
}
