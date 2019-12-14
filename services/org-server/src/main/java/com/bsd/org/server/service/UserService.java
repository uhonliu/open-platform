package com.bsd.org.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.org.server.model.entity.User;
import com.bsd.org.server.model.vo.UserDetailVO;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 人员信息表（钉钉） 服务类
 *
 * @author lrx
 * @date 2019-08-14
 */
public interface UserService extends IBaseService<User> {
    /**
     * 根据ID跟激活状态获取所有下级用户数据
     *
     * @param id
     * @return
     */
    List<User> getChildrenUsers(Long id);

    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    Boolean saveUser(User user);

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    Boolean updateUser(User user);

    /**
     * 根据用户ID获取用户信息
     *
     * @param id
     * @return
     */
    User getByUserId(Long id);

    /**
     * 根据用户ID删除用户信息
     *
     * @param id
     * @return
     */
    boolean delByUserId(Long id);

    Boolean changeActiveStatus(Long id, Boolean status);

    /**
     * 用户详情分页信息
     *
     * @param page
     * @param userDetailVO
     * @return
     */
    IPage<UserDetailVO> userDetailListPage(IPage<UserDetailVO> page, UserDetailVO userDetailVO);

    /**
     * 获取所有用户详情数据
     *
     * @param userDetailVO
     * @return
     */
    List<UserDetailVO> userDetailList(UserDetailVO userDetailVO);

    /**
     * 根据部门ID获取用户ID列表
     *
     * @param userDetailVO
     * @return java.util.List<java.lang.String>
     * @author zhangzz
     * @date 2019/12/9
     */
    List<String> userIdList(UserDetailVO userDetailVO);

    /**
     * 递归获取所有下级用户信息
     *
     * @param userId
     * @return
     */
    List<UserDetailVO> getCascadeChildren(Long userId);

    List<User> getUserListByDepartmentId(Long departmentId);
}
