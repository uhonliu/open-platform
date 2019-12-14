package com.bsd.user.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.user.server.mapper.UserLoginLogsMapper;
import com.bsd.user.server.model.entity.UserLoginLogs;
import com.bsd.user.server.service.UserLoginLogsService;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户-登录日志 服务实现类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@Service
public class UserLoginLogsServiceImpl extends BaseServiceImpl<UserLoginLogsMapper, UserLoginLogs> implements UserLoginLogsService {
    @Resource
    private UserLoginLogsMapper userLoginLogsMapper;

    /**
     * 获取用户最后一次登录日志
     *
     * @param userId
     * @return
     */
    @Override
    public UserLoginLogs findUserLastLoginLog(Long userId) {
        LambdaQueryWrapper LambdaQueryWrapper = Wrappers.<UserLoginLogs>lambdaQuery()
                .eq(UserLoginLogs::getUserId, userId)//用户ID
                .orderByDesc(UserLoginLogs::getLoginTime)//登录时间排序
                .last("limit 1");//只取一条数据
        UserLoginLogs userLoginLog = userLoginLogsMapper.selectOne(LambdaQueryWrapper);
        return userLoginLog;
    }
}
