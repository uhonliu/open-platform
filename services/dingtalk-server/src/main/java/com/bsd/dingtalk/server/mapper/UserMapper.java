package com.bsd.dingtalk.server.mapper;

import com.bsd.dingtalk.server.model.entity.User;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人员信息表（钉钉） Mapper 接口
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {
    /**
     * 批量保存用户
     * @param list
     */
//    void inserBatch(List<User> list);

}
