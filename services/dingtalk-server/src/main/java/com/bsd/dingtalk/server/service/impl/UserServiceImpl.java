package com.bsd.dingtalk.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsd.dingtalk.server.constants.Constant;
import com.bsd.dingtalk.server.mapper.DepartmentMapper;
import com.bsd.dingtalk.server.mapper.UserMapper;
import com.bsd.dingtalk.server.model.entity.Department;
import com.bsd.dingtalk.server.model.entity.Dingtalk;
import com.bsd.dingtalk.server.model.entity.User;
import com.bsd.dingtalk.server.service.DepartmentService;
import com.bsd.dingtalk.server.service.DingtalkService;
import com.bsd.dingtalk.server.service.UserService;
import com.bsd.dingtalk.server.util.AccessTokenUtil;
import com.bsd.dingtalk.server.util.ContactHelper;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiUserListResponse;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.RedisUtils;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 人员信息表（钉钉） 服务实现类
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    /**
     * 部门状态:0-禁用 1-启用
     */
    private static final Integer STATUS1 = 1;
    private static final Integer STATUS0 = 0;
    /**
     * 所属企业ID 0、必胜道
     */
    private static final Integer COMPANY_ID = 0;

    /**
     * 用户是否已激活:1已激活，0未激活
     */
    private static final Integer ACTIVE1 = 1;
    private static final Integer ACTIVE0 = 0;

    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DingtalkService dingtalkService;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public void importEmployeesByDingding(String userId) {

    }

    @Override
    @Async
    public void synUserInfoByDingding() {
        List<Dingtalk> dingtalks = dingtalkService.list();
        for (Dingtalk dingtalk : dingtalks) {
            log.info("sync start :{}", dingtalk);
            syncDataFromDingTalk(dingtalk);
            log.info("sync end :{}", dingtalk);
        }
        //删除缓存的菜单
        redisUtils.del(Constant.MEUN_CACHE_KEY);
    }

    private void syncDataFromDingTalk(Dingtalk dingtalk) {
        try {
            //获取accessToken
            String accessToken = AccessTokenUtil.getToken(dingtalk.getAppKey(), dingtalk.getAppSecret());
            //获取组织结构
            List<Long> list = new ArrayList<Long>();
            List<Long> deptListIds = departmentService.allDepartment(1L, list, accessToken);
            if (deptListIds == null || deptListIds.size() == 0) {
                return;
            }
            for (Long deptId : deptListIds) {
                //获取部门详情信息
                OapiDepartmentGetResponse deptDetail = ContactHelper.getDepartmentDetail(accessToken, deptId.toString());
                if (deptDetail == null || deptDetail.getId() == null) {
                    continue;
                }
                //判断部门是否存在，存在更新部门信息，不存在则存储部门信息
                Department department = departmentMapper.selectById(deptDetail.getId());
                boolean departmentFlag = true;
                if (department == null) {
                    department = new Department();
                    departmentFlag = false;
                }
                int parentId = deptDetail.getParentid().intValue();
                department.setParentId(parentId == 1 ? 0 : parentId);
                department.setDepartmentCode(deptDetail.getSourceIdentifier());
                department.setDepartmentName(deptDetail.getName());
                department.setSeq(deptDetail.getOrder());
                department.updateTime = new Date();
                if (departmentFlag) {
                    departmentMapper.updateById(department);
                } else {
                    //所属企业
                    department.setCompanyId(dingtalk.getCompanyId());
                    department.setDepartmentId(deptDetail.getId());
                    department.setStatus(STATUS1);
                    department.createTime = new Date();
                    departmentMapper.insert(department);
                }
                //获取部门下所有的员工信息
                List<OapiUserListResponse.Userlist> userList = ContactHelper.getDepartmentUserList(accessToken, deptDetail.getId(), 0L, 100L);
                if (userList == null || userList.size() == 0) {
                    continue;
                }
                for (OapiUserListResponse.Userlist dingdingUserInfo : userList) {
                    log.info("union:{},user:{},name:{}", dingdingUserInfo.getUnionid(), dingdingUserInfo.getUserid(), dingdingUserInfo.getName());
                    if (dingdingUserInfo.getUserid() == null) {
                        continue;
                    }
                    //查询是否为同步过的用户
                    User userEntity = getUserInfoByDingdingUserId(dingdingUserInfo.getUserid());
                    boolean flag = true;
                    if (userEntity == null) {
                        userEntity = new User();
                        flag = false;
                    }
                    userEntity.setAvatar(dingdingUserInfo.getAvatar());
                    //获取用户最上层用户ID
                    userEntity.setDepartment(mergeDepts(dingdingUserInfo.getDepartment(), userEntity.getDepartment()));
                    userEntity.setEmail(dingdingUserInfo.getEmail());
                    if (dingdingUserInfo.getHiredDate() != null) {
                        userEntity.setHiredDate(dingdingUserInfo.getHiredDate());
                    }
                    userEntity.setJobnumber(dingdingUserInfo.getJobnumber());
                    userEntity.setMobile(dingdingUserInfo.getMobile());
                    userEntity.setName(dingdingUserInfo.getName());
                    userEntity.setOrgEmail(dingdingUserInfo.getOrgEmail());
                    userEntity.setPosition(dingdingUserInfo.getPosition());
                    userEntity.setWorkPlace(dingdingUserInfo.getWorkPlace());
                    userEntity.setTel(dingdingUserInfo.getTel());
                    userEntity.setRemark(dingdingUserInfo.getRemark());
                    userEntity.updateTime = new Date();
                    //判断员工是否存在，存在则更新员工信息，不存在则存储员工信息
                    if (flag) {
                        //修改用户信息
                        userMapper.updateById(userEntity);
                    } else {
                        // 保存用户信息
                        userEntity.setDdUserid(dingdingUserInfo.getUserid());
                        userEntity.setUnionid(dingdingUserInfo.getUnionid());
                        userEntity.setActive(ACTIVE1);
                        userEntity.createTime = new Date();
                        userMapper.insert(userEntity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("钉钉同步组织架构异常:{}", e.getMessage());
        }
    }

    /**
     * 合并部门列表
     *
     * @param dingTalkDepts
     * @param dbDepts
     * @return
     */
    private String mergeDepts(String dingTalkDepts, String dbDepts) {
        //数据库中保存的部门数组
        List<String> deptArr = new ArrayList<>();
        if (!StringUtils.isEmpty(dbDepts)) {
            Collections.addAll(deptArr, dbDepts.split(","));
        }
        //合并钉钉传过来的部门列表
        Collections.addAll(deptArr, convertToOrgDeptsFormat(dingTalkDepts).split(","));
        //去除重复的部门列表
        Set<String> depts = getDeptSet(deptArr);
        String deptStr = "";
        for (String dept : depts) {
            deptStr = deptStr + dept + ",";
        }
        return deptStr.substring(0, deptStr.length() - 1);
    }

    /**
     * 获取去重的部门
     *
     * @param deptArr
     * @return
     */
    private Set getDeptSet(List<String> deptArr) {
        Set depts = new HashSet();
        for (int i = 0; i < deptArr.size(); i++) {
            if ("1".equals(deptArr.get(i))) {
                continue;
            }
            depts.add(deptArr.get(i));
        }
        return depts;
    }

    /**
     * 钉钉部门列表格式转成平台的部门列表格式
     *
     * @param deptStrs
     * @return
     */
    private String convertToOrgDeptsFormat(String deptStrs) {
        JSONArray arr = JSONArray.parseArray(deptStrs);
        String deptStr = "";
        for (int i = 0; i < arr.size(); i++) {
            deptStr = deptStr + arr.getString(i) + ",";
        }
        return deptStr.substring(0, deptStr.length() - 1);
    }

    @Override
    public User getUserInfoByDingdingUserId(String userid) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.lambda().eq(User::getDdUserid, userid);
        User user = userMapper.selectOne(wrapper);
        return user;
    }
}
