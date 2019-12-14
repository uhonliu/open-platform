package com.bsd.user.server.utils.redis;

import java.util.concurrent.TimeUnit;

/**
 * 锁
 *
 * @Author: linrongxin
 * @Date: 2019/9/19 18:15
 */
public interface DistributedLocker {
    void lock(String lockKey);

    void unlock(String lockKey);

    void lock(String lockKey, int timeout);

    void lock(String lockKey, TimeUnit unit, int timeout);
}
