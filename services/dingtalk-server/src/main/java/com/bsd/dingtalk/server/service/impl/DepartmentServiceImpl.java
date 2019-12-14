package com.bsd.dingtalk.server.service.impl;

import com.bsd.dingtalk.server.mapper.DepartmentMapper;
import com.bsd.dingtalk.server.model.entity.Department;
import com.bsd.dingtalk.server.service.DepartmentService;
import com.bsd.dingtalk.server.util.ContactHelper;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门信息表 服务实现类
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Override
    public List<Long> allDepartment(Long parentDepartId, List<Long> list, String accessToken) {
        List<Long> deptListIds = ContactHelper.getDepartmentIdList(accessToken, parentDepartId.toString());
        if (deptListIds != null && deptListIds.size() > 0) {
            list.addAll(deptListIds);
            for (Long deptId : deptListIds) {
                allDepartment(deptId, list, accessToken);
            }
        }
        return list;
    }
}
