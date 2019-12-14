package com.bsd.org.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.DepartmentVO;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 部门信息表 服务类
 *
 * @author lrx
 * @date 2019-08-14
 */
public interface DepartmentService extends IBaseService<Department> {
    /**
     * 获取可用的部门列表
     *
     * @return
     */
    List<DepartmentVO> availableList();

    /**
     * 更新部门数据
     *
     * @param department
     * @return
     */
    Boolean updateDepartment(Department department);

    /**
     * 保存部门信息
     *
     * @param department
     * @return
     */
    Boolean saveDepartment(Department department);

    /**
     * 根据部门编码查询部门信息
     *
     * @param departmentCode
     * @return
     */
    Department findByDepartmentCode(String departmentCode);

    /**
     * 根据部门名称查询部门信息
     *
     * @param departmentName
     * @return
     */
    Department findByDepartmentName(String departmentName);

    /**
     * 根据父ID获取下级部门列表
     *
     * @param id
     * @return
     */
    List<DepartmentVO> getChildrenDepartments(Long id, Boolean status);

    /**
     * 禁用/启用部门
     *
     * @param id
     * @return
     */
    Boolean changeStatus(Long id, Boolean status);

    /**
     * 分页查询部门数据
     *
     * @param pageConf
     * @param departmentVO
     * @return
     */
    IPage<DepartmentVO> pageByParam(Page pageConf, DepartmentVO departmentVO);

    /**
     * 获取部门数据
     *
     * @return
     */
    List<DepartmentVO> listByParam(DepartmentVO departmentVO);

    /**
     * 获取所有部门select数据
     *
     * @param departmentVO
     * @return
     */
    List<DepartmentVO> listSelectDepartments(DepartmentVO departmentVO);
}
