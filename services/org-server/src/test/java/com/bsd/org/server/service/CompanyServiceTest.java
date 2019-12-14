package com.bsd.org.server.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.CompanyMenuVO;
import com.bsd.org.server.model.vo.DepartmentMenuVO;
import com.opencloud.common.test.BaseTest;
import com.opencloud.common.utils.BeanConvertUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CompanyServiceTest extends BaseTest {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;

    @Test
    public void getAllCompanyMenu() {
        List<Company> companies = companyService.list();
        if (companies == null || companies.size() == 0) {
            //return null;
        }
        //公司信息
        List<CompanyMenuVO> companyMenus = BeanConvertUtils.copyList(companies, CompanyMenuVO.class);
        List<Long> companyIds = getCompanyIds(companies);
        List<Department> departments = departmentService.list(
                Wrappers.<Department>lambdaQuery()
                        .eq(Department::getStatus, 1)
                        .in(Department::getCompanyId, companyIds));
        for (CompanyMenuVO companyMenuVO : companyMenus) {
            //遍历加载部门菜单
            loadDepartmentMeun(companyMenuVO, departments);
        }
        System.out.println();
    }

    private void loadDepartmentMeun(CompanyMenuVO companyMenuVO, List<Department> departments) {
        Long companyId = companyMenuVO.getCompanyId();
        List<DepartmentMenuVO> departmentMenus = build(companyId, 0L, departments);
        companyMenuVO.setChildren(departmentMenus);
    }

    private List<DepartmentMenuVO> build(Long companyId, Long departmentParentId, List<Department> departments) {
        //获取下级部门菜单
        List<DepartmentMenuVO> children = getChildDepartmentMenu(companyId, departmentParentId, departments);
        for (DepartmentMenuVO departmentMenuVO : children) {
            List<DepartmentMenuVO> nextChildren = build(companyId, departmentMenuVO.getDepartmentId(), departments);
            if (nextChildren != null && nextChildren.size() > 0) {
                departmentMenuVO.setChildren(nextChildren);
            }
        }
        return children;
    }

    private List<DepartmentMenuVO> getChildDepartmentMenu(Long companyId, Long departmentParentId, List<Department> departments) {
        List<DepartmentMenuVO> children = new ArrayList<>();
        for (Department department : departments) {
            if (department.getCompanyId().longValue() == companyId && department.getParentId().longValue() == departmentParentId) {
                DepartmentMenuVO departmentMenu = new DepartmentMenuVO();
                BeanUtils.copyProperties(department, departmentMenu);
                children.add(departmentMenu);
            }
        }
        return children;
    }

    private List<Long> getCompanyIds(List<Company> companies) {
        if (companies == null || companies.isEmpty()) {
            return null;
        }
        List<Long> companyIds = new ArrayList<>(companies.size());
        for (Company company : companies) {
            Long companyId = company.getCompanyId();
            companyIds.add(companyId);
        }
        return companyIds;
    }
}