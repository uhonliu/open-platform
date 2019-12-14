package com.bsd.user.server.mapper;

import com.bsd.user.server.model.entity.User;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-基础信息 Mapper 接口
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {

}
