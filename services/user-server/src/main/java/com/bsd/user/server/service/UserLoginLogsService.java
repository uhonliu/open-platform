package com.bsd.user.server.service;

import com.bsd.user.server.model.entity.UserLoginLogs;
import com.opencloud.common.mybatis.base.service.IBaseService;

/**
 * 用户-登录日志 服务类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
public interface UserLoginLogsService extends IBaseService<UserLoginLogs> {
    /**
     * 获取用户最后一次登录日志
     *
     * @param userId
     * @return
     */
    UserLoginLogs findUserLastLoginLog(Long userId);
}
