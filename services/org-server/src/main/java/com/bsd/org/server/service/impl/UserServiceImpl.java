package com.bsd.org.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.org.server.mapper.DepartmentMapper;
import com.bsd.org.server.mapper.PositionMapper;
import com.bsd.org.server.mapper.UserMapper;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.entity.Position;
import com.bsd.org.server.model.entity.User;
import com.bsd.org.server.model.vo.UserDetailVO;
import com.bsd.org.server.service.UserService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员信息表（钉钉） 服务实现类
 *
 * @author lrx
 * @date 2019-08-14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 根据ID跟激活状态获取所有下级用户数据
     *
     * @param id
     * @return
     */
    @Override
    public List<User> getChildrenUsers(Long id) {
        return userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getParentId, id));
    }

    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    @Override
    public Boolean saveUser(User user) {
        if (!checkUser(user)) {
            return false;
        }

        if (user.getUserId().longValue() != 0) {
            User dbUser = getByUserId(user.getUserId());
            if (dbUser != null) {
                throw new OpenAlertException("该用户ID已经存在钉钉用户信息");
            }
        }

        int count = userMapper.insert(user);
        if (count <= 0) {
            throw new OpenAlertException("保存用户信息失败");
        }
        return true;
    }


    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @Override
    public Boolean updateUser(User user) {
        if (!checkUser(user)) {
            return false;
        }

        if (user.getUserId().longValue() != 0) {
            User dbUser = getByUserId(user.getUserId());
            if (dbUser == null) {
                throw new OpenAlertException("该用户ID不存在钉钉用户信息");
            }
        }

        int count = userMapper.update(user, Wrappers.<User>lambdaUpdate().eq(User::getDdUserid, user.getDdUserid()));
        if (count <= 0) {
            throw new OpenAlertException("更新用户数据失败");
        }
        return true;
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param id
     * @return
     */
    @Override
    public User getByUserId(Long id) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserId, id));
        return user;
    }

    /**
     * 根据用户ID删除用户信息
     *
     * @param id
     * @return
     */
    @Override
    public boolean delByUserId(Long id) {
        int count = userMapper.delete(Wrappers.<User>lambdaQuery().eq(User::getUserId, id));
        return count > 0;
    }

    @Override
    public Boolean changeActiveStatus(Long id, Boolean status) {
        //判断用户是否存在
        User user = getByUserId(id);
        if (user == null) {
            throw new OpenAlertException("人员信息(钉钉)不存在");
        }

        user.setActive(status);
        int effectCount = userMapper.update(user, Wrappers.<User>lambdaUpdate().eq(User::getUserId, user.getUserId()));
        if (effectCount <= 0) {
            throw new OpenAlertException("修改人员信息激活状态(钉钉)失败");
        }
        return true;
    }


    private Boolean checkUser(User user) {
        //校验用户信息
        User curUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getDdUserid, user.getDdUserid()));
        if (curUser == null) {
            throw new OpenAlertException("用户信息不存在");
        }

        //校验上级用户信息
        Long userId = user.getUserId();
        Long parentId = user.getParentId();
        if (parentId != null && parentId.longValue() != 0) {
            if (userId.longValue() == parentId.longValue()) {
                throw new OpenAlertException("不能设置当前用户为上级用户");
            }
            User parentUser = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserId, parentId));
            if (parentUser == null) {
                throw new OpenAlertException("上级用户信息不存在");
            }
        }
        //检查部门是否存在
        if (StringUtils.isEmpty(user.getDepartment())) {
            throw new OpenAlertException("部门ID列表不能为空");
        }
        String[] deptArr = user.getDepartment().split(",");
        List<Long> deptIdList = new ArrayList<>();
        for (String deptId : deptArr) {
            try {
                Long id = Long.valueOf(deptId);
                deptIdList.add(id);
            } catch (Exception ex) {
                throw new OpenAlertException("用户部门ID格式错误");
            }
        }
        List<Department> depts = departmentMapper.selectBatchIds(deptIdList);
        if (depts == null) {
            throw new OpenAlertException("用户部门ID不存在");
        }
        if (depts.size() != deptArr.length) {
            throw new OpenAlertException("用户部门列表有误,部分部门ID不存在");
        }

        //校验岗位信息
        Long positionId = user.getPositionId();
        Position position = positionMapper.selectById(positionId);
        if (position == null) {
            throw new OpenAlertException("岗位信息不存在");
        }
        user.setPosition(position.getPositionName());

        return true;
    }


    /**
     * 分页获取用户详情信息列表
     *
     * @param page
     * @param userDetailVO
     * @return
     */
    @Override
    public IPage<UserDetailVO> userDetailListPage(IPage<UserDetailVO> page, UserDetailVO userDetailVO) {
        Wrapper wrapper = creatWrapperByUserDetailVO(userDetailVO);
        if (userDetailVO != null && userDetailVO.getDepartmentId() != null) {
            return userMapper.userDetailListPage(page, wrapper);
        } else {
            return userMapper.userPage(page, wrapper);
        }
    }

    /**
     * 根据条件查询获取用户详情信息列表
     *
     * @param userDetailVO
     * @return
     */
    @Override
    public List<UserDetailVO> userDetailList(UserDetailVO userDetailVO) {
        Wrapper wrapper = creatWrapperByUserDetailVO(userDetailVO);
        if (userDetailVO != null && userDetailVO.getDepartmentId() != null) {
            return userMapper.userDetailList(wrapper);
        } else {
            return userMapper.userList(wrapper);
        }
    }

    /**
     * 递归获取所用用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserDetailVO> getCascadeChildren(Long userId) {
        List<UserDetailVO> childrens = new ArrayList<>();
        //查询用户信息
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setUserId(userId);
        List<UserDetailVO> users = userDetailList(userDetailVO);
        if (users == null || users.size() != 1) {
            return childrens;
        }
        //组织架构用户信息不多,一次性获取获取所有用户信息
        List<UserDetailVO> allUserInfo = userDetailList(null);
        if (allUserInfo == null || allUserInfo.size() == 0) {
            return childrens;
        }
        //list 转 map
        Map<Long, UserDetailVO> userMap = new HashMap<Long, UserDetailVO>(allUserInfo.size());
        for (UserDetailVO user : allUserInfo) {
            userMap.put(user.getUserId(), user);
        }
        //获取所有下级信息
        getChildrenByUserMap(userMap, userId, childrens);
        //加上自己的信息
        childrens.add(users.get(0));
        return childrens;
    }

    /**
     * 获取指定用户ID下的所有下级用户信息
     *
     * @param userMap
     * @param userId
     * @param childrens
     */
    private void getChildrenByUserMap(Map<Long, UserDetailVO> userMap, Long userId, List<UserDetailVO> childrens) {
        //当前层级当前点下的所有子节点
        List<UserDetailVO> childList = getChildNodes(userId, userMap);
        for (UserDetailVO node : childList) {
            //递归调用该方法
            getChildrenByUserMap(userMap, node.getUserId(), childrens);
        }
        if (childList != null && childList.size() > 0) {
            childrens.addAll(childList);
        }
    }

    /**
     * 获取下级用户节点
     *
     * @param nodeId
     * @param nodes
     * @return
     */
    public static List<UserDetailVO> getChildNodes(Long nodeId, Map<Long, UserDetailVO> nodes) {
        List<UserDetailVO> list = new ArrayList<>();
        for (Long key : nodes.keySet()) {
            if (nodes.get(key).getParentId().equals(nodeId)) {
                list.add(nodes.get(key));
            }
        }
        return list;
    }

    /**
     * VO转Wrapper查询条件
     *
     * @param userDetailVO
     * @return
     */
    private Wrapper creatWrapperByUserDetailVO(UserDetailVO userDetailVO) {
        QueryWrapper<UserDetailVO> queryWrapper = Wrappers.query();
        if (userDetailVO == null) {
            return queryWrapper;
        }
        //条件拼接
        queryWrapper.eq(userDetailVO.getCompanyId() != null, "TEMP.company_id", userDetailVO.getCompanyId());
        queryWrapper.eq(userDetailVO.getDepartmentId() != null, "TEMP.department_id", userDetailVO.getDepartmentId());
        queryWrapper.eq(userDetailVO.getPositionId() != null, "TEMP.position_id", userDetailVO.getPositionId());
        queryWrapper.eq(userDetailVO.getUserId() != null, "TEMP.user_id", userDetailVO.getUserId());
        queryWrapper.eq(userDetailVO.getParentId() != null, "TEMP.parent_id", userDetailVO.getParentId());
        queryWrapper.eq(StringUtils.isNotEmpty(userDetailVO.getPositionCode()), "TEMP.position_code", userDetailVO.getPositionCode());
        queryWrapper.eq(StringUtils.isNotEmpty(userDetailVO.getPositionName()), "TEMP.position_name", userDetailVO.getPositionName());
        queryWrapper.eq(StringUtils.isNotEmpty(userDetailVO.getName()), "TEMP.name", userDetailVO.getName());
        queryWrapper.eq(StringUtils.isNotEmpty(userDetailVO.getMobile()), "TEMP.mobile", userDetailVO.getMobile());
        queryWrapper.eq(userDetailVO.getActive() != null, "TEMP.active", userDetailVO.getActive());
        queryWrapper.eq(StringUtils.isNotEmpty(userDetailVO.getJobnumber()), "TEMP.jobnumber", userDetailVO.getJobnumber());

        return queryWrapper;
    }
}
