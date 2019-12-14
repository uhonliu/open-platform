package com.opencloud.base.server.mapper;

import com.opencloud.base.client.model.entity.BaseRole;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuyadu
 */
@Repository
public interface BaseRoleMapper extends SuperMapper<BaseRole> {
    List<BaseRole> selectRoleList(Map params);
}
