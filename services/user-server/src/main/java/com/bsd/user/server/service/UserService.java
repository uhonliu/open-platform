package com.bsd.user.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.user.server.model.UserPo;
import com.bsd.user.server.model.entity.User;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 用户-基础信息 服务类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
public interface UserService extends IBaseService<User> {
    /**
     * 发送短信验证码
     *
     * @param mobile     手机号
     * @param type       类型,1-注册验证码 2-登录验证码 3-忘记密码 4-微信绑定 5-修改手机账号验证 6-新手机账号验证
     * @param signSource 短信签名来源 0、跨境知道 1、卖家成长
     */
    void sendSmsCode(String mobile, Integer type, Integer signSource);

    /**
     * 从redis中获取缓存的短信验证码
     *
     * @param mobile   手机号(key)
     * @param codeType 类型 1-注册验证码 2-登录验证码 3-忘记密码 4-微信绑定账号
     * @return
     */
    String getSmsCodeByRedis(String mobile, Integer codeType);

    /**
     * 通过手机注册用户
     *
     * @param user 用户信息对象
     */
    Integer registerByPhone(UserPo user);

    /**
     * 通过手机验证码登录
     *
     * @param user
     * @return
     */
    Map<String, Object> loginByMobileCode(UserPo user);

    /**
     * 通过手机+密码登录
     *
     * @param user
     * @return
     */
    Map<String, Object> loginByMobilePassword(UserPo user);

    /**
     * 获取用户信息
     *
     * @param mobile
     * @return
     */
    User getUserInfoByMobile(String mobile);

    /**
     * 检查账号是否存在
     *
     * @param mobile
     * @return
     */
    Boolean isExist(String mobile);

    /**
     * 登录token认证
     *
     * @param user
     * @return
     */
    Map<String, Object> authenticatingToken(UserPo user);

    /**
     * 修改密码
     *
     * @param user
     */
    void updatePassword(UserPo user);

    /**
     * 重置密码
     *
     * @param user
     */
    void resetPassword(UserPo user);

    /**
     * 旧系统登录认证
     *
     * @param mobile
     * @param password
     * @return
     */
    boolean loginByOrderSystem(String mobile, String password);

    /**
     * 修改用户信息
     *
     * @param user
     */
    void updateUserInfo(UserPo user);

    /**
     * 修改用户手机号-验证旧的手机信息
     *
     * @param po
     * @return
     */
    void verifyOldMobile(UserPo po);

    /**
     * 修改用户手机账号
     *
     * @param po
     */
    void updateMobile(UserPo po);


    /**
     * 获取分页数据
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param userPo  查询条件
     * @return
     */
    IPage<User> userPageList(int current, int size, UserPo userPo);

    /**
     * 批量推送用户信息
     *
     * @param asList
     * @return
     */
    boolean batchPush(List<String> asList, String authorization);

    /**
     * 批量修改用户状态
     *
     * @param asList
     * @return
     */
    boolean batchChangeStatus(List<String> asList, Integer status);

    /**
     * 获取用户列表
     *
     * @param userPo
     * @return
     */
    List<User> userList(UserPo userPo);

    /**
     * 手工录入保存用户数据
     *
     * @param userPo
     * @return
     */
    boolean manualSave(UserPo userPo);

    /**
     * 用户数据导出
     *
     * @param userPo
     * @return
     */
    List<User> exportMaxUserList(UserPo userPo);
}
