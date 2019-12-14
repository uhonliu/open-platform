package com.opencloud.base.server.mapper;

import com.opencloud.base.client.model.AuthorityAction;
import com.opencloud.base.client.model.AuthorityApi;
import com.opencloud.base.client.model.AuthorityMenu;
import com.opencloud.base.client.model.AuthorityResource;
import com.opencloud.base.client.model.entity.BaseAuthority;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import com.opencloud.common.security.OpenAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityMapper extends SuperMapper<BaseAuthority> {
    /**
     * 查询所有资源授权列表
     *
     * @return
     */
    List<AuthorityResource> selectAllAuthorityResource();

    /**
     * 查询已授权权限列表
     *
     * @param map
     * @return
     */
    List<OpenAuthority> selectAuthorityAll(Map map);


    /**
     * 获取菜单权限
     *
     * @param map
     * @return
     */
    List<AuthorityMenu> selectAuthorityMenu(Map map);

    /**
     * 获取操作权限
     *
     * @param map
     * @return
     */
    List<AuthorityAction> selectAuthorityAction(Map map);

    /**
     * 获取API权限
     *
     * @param map
     * @return
     */
    List<AuthorityApi> selectAuthorityApi(Map map);
}
