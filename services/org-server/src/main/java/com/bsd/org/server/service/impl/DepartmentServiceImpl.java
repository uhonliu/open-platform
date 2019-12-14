package com.bsd.org.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.mapper.CompanyMapper;
import com.bsd.org.server.mapper.DepartmentMapper;
import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.DepartmentVO;
import com.bsd.org.server.service.DepartmentService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门信息表 服务实现类
 *
 * @author lrx
 * @date 2019-08-14
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private CompanyMapper companyMapper;


    /**
     * 可用状态部门列表
     *
     * @return
     */
    @Override
    public List<DepartmentVO> availableList() {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setStatus(true);
        return listByParam(departmentVO);
    }

    /**
     * 更新部门数据
     *
     * @param department
     * @return
     */
    @Override
    public Boolean updateDepartment(Department department) {
        if (!checkDepartment(department)) {
            return false;
        }
        int count = departmentMapper.updateById(department);
        if (count <= 0) {
            throw new OpenAlertException("更新部门数据失败");
        }
        return true;
    }


    /**
     * 保存部门数据
     *
     * @param department
     * @return
     */
    @Override
    public Boolean saveDepartment(Department department) {
        if (!checkDepartment(department)) {
            return false;
        }
        int count = departmentMapper.insert(department);
        if (count <= 0) {
            throw new OpenAlertException("保存部门信息失败");
        }
        return true;
    }


    /**
     * 根据部门编码查询部门信息
     *
     * @param departmentCode
     * @return
     */
    @Override
    public Department findByDepartmentCode(String departmentCode) {
        return departmentMapper.selectOne(Wrappers.<Department>lambdaQuery().eq(Department::getDepartmentCode, departmentCode));
    }

    /**
     * 根据部门名称查询部门信息
     *
     * @param departmentName
     * @return
     */
    @Override
    public Department findByDepartmentName(String departmentName) {
        return departmentMapper.selectOne(Wrappers.<Department>lambdaQuery().eq(Department::getDepartmentName, departmentName));
    }

    /**
     * 根基ID获取未禁用的下级部门
     *
     * @param id
     * @return
     */
    @Override
    public List<DepartmentVO> getChildrenDepartments(Long id, Boolean status) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setStatus(status);
        departmentVO.setParentId(id);
        return listByParam(departmentVO);
    }

    /**
     * 禁用/启用部门信息
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean changeStatus(Long id, Boolean status) {
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            throw new OpenAlertException("部门不存在");
        }

        //是否需要级联修改所有下级部门的状态？
        department.setStatus(status);
        int effectCount = departmentMapper.updateById(department);
        if (effectCount <= 0) {
            throw new OpenAlertException("部门禁用失败");
        }
        return true;
    }

    /**
     * 部门分页数据
     *
     * @param pageConf
     * @param departmentVO
     * @return
     */
    @Override
    public IPage<DepartmentVO> pageByParam(Page pageConf, DepartmentVO departmentVO) {
        Wrapper wrapper = creatWrapperByDepartmentVO(departmentVO);
        return departmentMapper.pageByParam(pageConf, wrapper);
    }

    @Override
    public List<DepartmentVO> listByParam(DepartmentVO departmentVO) {
        return departmentMapper.listByParam(creatWrapperByDepartmentVO(departmentVO));
    }

    @Override
    public List<DepartmentVO> listSelectDepartments(DepartmentVO departmentVO) {
        QueryWrapper<DepartmentVO> queryWrapper = Wrappers.query();
        queryWrapper.eq(departmentVO.getCompanyId() != null, "A.company_id", departmentVO.getCompanyId());
        queryWrapper.ne(departmentVO.getDepartmentId() != null, "A.department_id", departmentVO.getDepartmentId());
        return departmentMapper.listByParam(queryWrapper);
    }


    /**
     * 根据departmentVO对象创建Wrapper查询条件
     *
     * @param departmentVO
     * @return
     */
    private Wrapper creatWrapperByDepartmentVO(DepartmentVO departmentVO) {
        QueryWrapper<DepartmentVO> queryWrapper = Wrappers.query();
        if (departmentVO == null) {
            return queryWrapper;
        }
        //条件拼接
        queryWrapper.eq(StringUtils.isNotEmpty(departmentVO.getCompanyName()), "B.company_name", departmentVO.getCompanyName());
        queryWrapper.eq(StringUtils.isNotEmpty(departmentVO.getDepartmentCode()), "A.department_code", departmentVO.getDepartmentCode());
        queryWrapper.eq(StringUtils.isNotEmpty(departmentVO.getDepartmentName()), "A.department_name", departmentVO.getDepartmentName());
        queryWrapper.eq(StringUtils.isNotEmpty(departmentVO.getParentName()), "C.department_name", departmentVO.getParentName());
        queryWrapper.eq(departmentVO.getStatus() != null, "A.status", departmentVO.getStatus());
        queryWrapper.eq(departmentVO.getCompanyId() != null, "A.company_id", departmentVO.getCompanyId());
        queryWrapper.eq(departmentVO.getDepartmentId() != null, "A.department_id", departmentVO.getDepartmentId());
        queryWrapper.eq(departmentVO.getParentId() != null, "A.parent_id", departmentVO.getParentId());
        return queryWrapper;
    }


    /**
     * 检查参数
     *
     * @param department
     * @return
     */
    private Boolean checkDepartment(Department department) {
        Department dbDepartment = null;
        Long departmentId = department.getDepartmentId();
        //编辑时候验证编辑部门存不存在
        if (departmentId != null) {
            dbDepartment = departmentMapper.selectById(departmentId);
            if (dbDepartment == null) {
                throw new OpenAlertException("编辑部门不存在");
            }
        }
        //校验公司信息
        Long companyId = department.getCompanyId();
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new OpenAlertException("公司信息不存在");
        }

        //校验上级部门信息
        Long parentId = department.getParentId();
        if (parentId != null && parentId != 0) {
            if (departmentId != null && departmentId.longValue() == parentId.longValue()) {
                //编辑时候,上级部门ID不能跟当前部门ID一样
                throw new OpenAlertException("不能设置当前部门为上级部门");
            }
            dbDepartment = departmentMapper.selectById(parentId);
            if (dbDepartment == null) {
                throw new OpenAlertException("上级部门不存在");
            }
        }

        dbDepartment = this.findByDepartmentCode(department.getDepartmentCode());
        if (dbDepartment != null) {
            if (departmentId == null) {
                throw new OpenAlertException("部门编码已经存在");
            }
            if (dbDepartment.getDepartmentId().longValue() != departmentId.longValue()) {
                throw new OpenAlertException("部门编码已经存在");
            }
        }

        dbDepartment = this.findByDepartmentName(department.getDepartmentName());
        if (dbDepartment != null) {
            if (departmentId == null) {
                throw new OpenAlertException("部门名称已经存在");
            }
            if (dbDepartment.getDepartmentId().longValue() != departmentId.longValue()) {
                throw new OpenAlertException("部门名称已经存在");
            }
        }
        return true;
    }
}
