package com.bsd.dingtalk.server.mapper;

import com.bsd.dingtalk.server.model.entity.Department;
import com.opencloud.common.mybatis.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门信息表 Mapper 接口
 *
 * @author liujianhong
 * @date 2019-07-01
 */
@Mapper
public interface DepartmentMapper extends SuperMapper<Department> {
    /**
     * 批量保存部门信息
     */
//    void insertBatch(List<Department> departmentList);


}
