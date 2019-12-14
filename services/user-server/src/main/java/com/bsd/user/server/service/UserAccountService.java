package com.bsd.user.server.service;

import com.bsd.user.server.model.UserAccountPo;
import com.bsd.user.server.model.entity.UserAccount;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 用户-第三方账号 服务类
 *
 * @author lisongmao
 * @date 2019-06-26
 */
public interface UserAccountService extends IBaseService<UserAccount> {
    /**
     * 验证qq账号是否绑定了用户中心账号
     *
     * @param code qq回调返回的code码，可以通过此code获取access_token
     * @return
     */
    Map<String, Object> isBindingsByQq(String code);

    /**
     * 验证微信账号是否绑定了用户中心账号
     *
     * @param code 微信回调返回的code码，可以通过此code获取access_token
     * @return true 已绑定 fals未绑定
     */
    boolean isBindingsByWechat(String code, int platform);

    /**
     * 第三方登录服务处理
     *
     * @param accoutPo
     * @return
     */
    Map<String, Object> loginByThirdPlatform(UserAccountPo accoutPo);

    /**
     * 根据条件获取第三方账号信息
     *
     * @return
     */
    List<UserAccount> getUserAccount(String openId, Integer platform, String unionid);

    /**
     * 根据userId+platform获取第三方账号信息
     *
     * @param userId
     * @param platform
     * @return
     */
    UserAccount getUserAccoutByUserId(Long userId, Integer platform);

    /**
     * 绑定第三方账号
     *
     * @param accountPo
     */
    void bindingsUser(UserAccountPo accountPo);

    /**
     * 解绑第三方账号
     *
     * @param mobile   解绑的手机号
     * @param platform 第三方平台类型
     */
    void unbindingsUser(String mobile, Integer platform);

    /**
     * 根据用户ID列表查询用户第三方账号信息
     *
     * @param idList
     * @return
     */
    List<UserAccount> selectBatchUserIds(List<String> idList);
}
