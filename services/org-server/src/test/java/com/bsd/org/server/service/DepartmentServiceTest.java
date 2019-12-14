package com.bsd.org.server.service;

import com.bsd.org.server.model.vo.DepartmentVO;
import com.opencloud.common.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DepartmentServiceTest extends BaseTest {
    @Autowired
    private DepartmentService departmentService;

    @Test
    public void getChildrenDepartments() {
        // 根据父ID获取下级部门列表 如输入一级部门ID只能获取二级部门，获取不到三级部门数据
        List<DepartmentVO> list = departmentService.getChildrenDepartments(87187160l, true);
        System.out.println(list);
    }

    @Test
    public void listSelectDepartments() {
        // 获取所有部门数据 不区分层级
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setDepartmentId(87187160l);
        List<DepartmentVO> list = departmentService.listSelectDepartments(departmentVO);
        System.out.println(list);
    }
}