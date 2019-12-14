package com.bsd.dingtalk.server.service;

import com.bsd.dingtalk.server.model.entity.User;
import com.opencloud.common.mybatis.base.service.IBaseService;

/**
 * 人员信息表（钉钉） 服务类
 *
 * @author liujianhong
 * @date 2019-07-01
 */
public interface UserService extends IBaseService<User> {
    void importEmployeesByDingding(String userId);

    /**
     * 同步钉钉组织架构信息
     */
    void synUserInfoByDingding();

    /**
     * 根据钉钉userid查询用户信息
     *
     * @param userid
     * @return
     */
    User getUserInfoByDingdingUserId(String userid);
}
