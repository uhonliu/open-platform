package com.bsd.org.server.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bsd.org.server.model.entity.User;
import com.bsd.org.server.model.vo.UserDetailVO;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人员信息表（钉钉） Mapper 接口
 *
 * @author lrx
 * @date 2019-08-14
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {
    /**
     * 分页获取用户信息
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<UserDetailVO> userDetailListPage(IPage<UserDetailVO> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 根据条件获取用户列表
     *
     * @param wrapper
     * @return
     */
    List<UserDetailVO> userDetailList(@Param(Constants.WRAPPER) Wrapper wrapper);


    /**
     * 分页[部门列表拼接]
     *
     * @param page
     * @param wrapper
     * @return
     */
    IPage<UserDetailVO> userPage(IPage<UserDetailVO> page, @Param(Constants.WRAPPER) Wrapper wrapper);

    /**
     * 根据条件获取用户列表[部门列表拼接]
     *
     * @param wrapper
     * @return
     */
    List<UserDetailVO> userList(@Param(Constants.WRAPPER) Wrapper wrapper);

    List<User> getUserListByDepartmentId(Long departmentId);
}
