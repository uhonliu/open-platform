package com.bsd.dingtalk.server.service;

import com.bsd.dingtalk.server.model.entity.Department;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 部门信息表 服务类
 *
 * @author liujianhong
 * @date 2019-07-01
 */
public interface DepartmentService extends IBaseService<Department> {
    /**
     * 递归回去所有的部门的id
     *
     * @param parentDepartId
     * @param list
     * @param accessToken
     * @return
     */
    List<Long> allDepartment(Long parentDepartId, List<Long> list, String accessToken);
}
