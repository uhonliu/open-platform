package com.bsd.org.server.constants;

/**
 * @Author: linrongxin
 * @Date: 2019/9/21 16:45
 */
public class BaseUserConst {
    /**
     * 用户状态 禁用
     */
    public final static int USER_STATUS_FORBIDDEN = 0;
    /**
     * 用户状态 正常
     */
    public final static int USER_STATUS_NORMAL = 1;
    /**
     * 用户状态 锁定
     */
    public final static int USER_STATUS_LOCK = 2;
    /**
     * 用户类型 超级管理员
     */
    public final static String USER_TYPE_SUPER = "super";
    /**
     * 用户类型 普通管理员
     */
    public final static String USER_TYPE_NORMAL = "normal";
    /**
     * 菜单缓存redis key
     */
    public final static String MEUN_CACHE_KEY = "org:meun";
}
