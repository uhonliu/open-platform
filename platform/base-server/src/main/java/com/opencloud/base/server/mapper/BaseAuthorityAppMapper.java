package com.opencloud.base.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opencloud.base.client.model.entity.BaseAuthorityApp;
import com.opencloud.common.security.OpenAuthority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuyadu
 */
@Repository
public interface BaseAuthorityAppMapper extends BaseMapper<BaseAuthorityApp> {
    /**
     * 获取应用已授权权限
     *
     * @param appId
     * @return
     */
    List<OpenAuthority> selectAuthorityByApp(@Param("appId") String appId);
}
