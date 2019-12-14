package com.bsd.org.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.org.server.constants.BaseUserConst;
import com.bsd.org.server.mapper.CompanyMapper;
import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.entity.User;
import com.bsd.org.server.model.vo.CompanyMenuVO;
import com.bsd.org.server.model.vo.DepartmentMenuVO;
import com.bsd.org.server.service.CompanyService;
import com.bsd.org.server.service.DepartmentService;
import com.bsd.org.server.service.UserService;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import com.opencloud.common.utils.BeanConvertUtils;
import com.opencloud.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 企业信息 服务实现类
 *
 * @author lrx
 * @date 2019-08-14
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyServiceImpl extends BaseServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private DepartmentService departmentService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    /**
     * 删除企业信息
     *
     * @param companyId
     * @return
     */
    @Override
    public Boolean deleteCompany(Long companyId) {
        //查询企业信息
        Company company = companyMapper.selectById(companyId);
        if (company == null) {
            throw new OpenAlertException("企业信息不存在");
        }

        int row = companyMapper.deleteById(companyId);
        if (row <= 0) {
            throw new OpenAlertException("删除企业信息失败");
        }

        return true;
    }

    @Override
    public List<CompanyMenuVO> getAllCompanyMenu() {
        //使用redisTemplate,避免使用stringRedisTemplate序列化异常
        RedisUtils redisUtils = new RedisUtils(redisTemplate);

        //redis缓存中查询菜单信息
        List<CompanyMenuVO> companyMenus = (List<CompanyMenuVO>) redisUtils.get(BaseUserConst.MEUN_CACHE_KEY);
        if (companyMenus != null && companyMenus.size() > 0) {
            //缓存中存在,直接返回
            log.info("get meun from cache");
            return companyMenus;
        }

        List<Company> companies = this.selectAll();
        if (companies == null || companies.size() == 0) {
            return null;
        }
        //公司信息
        companyMenus = BeanConvertUtils.copyList(companies, CompanyMenuVO.class);
        List<Long> companyIds = getCompanyIds(companies);
        //获取公司下所有未禁用的部门信息
        List<Department> departments = departmentService.list(
                Wrappers.<Department>lambdaQuery()
                        .eq(Department::getStatus, 1)
                        .in(Department::getCompanyId, companyIds));
        for (CompanyMenuVO companyMenuVO : companyMenus) {
            //遍历加载部门菜单
            loadDepartmentMeun(companyMenuVO, departments);
        }

        //缓存菜单信息到redis中
        redisUtils.set(BaseUserConst.MEUN_CACHE_KEY, companyMenus);
        return companyMenus;
    }

    /**
     * 移除菜单缓存
     */
    @Override
    public void removeMeunCache() {
        //使用redisTemplate,避免使用stringRedisTemplate序列化异常
        RedisUtils redisUtils = new RedisUtils(redisTemplate);
        redisUtils.del(BaseUserConst.MEUN_CACHE_KEY);
    }

    /**
     * 加载部门菜单数据
     *
     * @param companyMenuVO
     * @param departments
     */
    private void loadDepartmentMeun(CompanyMenuVO companyMenuVO, List<Department> departments) {
        Long companyId = companyMenuVO.getCompanyId();
        List<DepartmentMenuVO> departmentMenus = build(companyId, 0L, departments);
        companyMenuVO.setChildren(departmentMenus);
    }

    /**
     * 数据公司部门菜单
     *
     * @param companyId
     * @param departmentParentId
     * @param departments
     * @return
     */
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

    /**
     * 加载下级部门菜单
     *
     * @param companyId
     * @param departmentParentId
     * @param departments
     * @return
     */
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

    /**
     * 获取公司ID列表
     *
     * @param companies
     * @return
     */
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

    /**
     * 根据部门ID获取用户ID列表
     *
     * @param departmentId
     * @return java.util.List<java.lang.Long>
     * @author zhangzz
     * @date 2019/12/10
     */
    @Override
    public List<Long> getUserIds(Long departmentId) {
        // 部门IDs
        List<Long> departmentIds = new ArrayList<>();
        departmentIds.add(departmentId);
        List<DepartmentMenuVO> list = getChildDepartByDeId(departmentIds);
        // 自身信息添加到List中
        DepartmentMenuVO departmentMenuVO = new DepartmentMenuVO();
        departmentMenuVO.setDepartmentId(departmentId);
        // dd.setDepartmentName("市场与产品中心");
        list.add(departmentMenuVO);
        // 获取到本部门和所有的下属部门ID
        Set<Long> userSet = new HashSet<Long>();
        for (int i = 0; i < list.size(); i++) {
            //ids.add(list.get(i).getDepartmentId());
            // 根据部门ID 查询人员信息
            List<User> list1 = userService.getUserListByDepartmentId(list.get(i).getDepartmentId());
            for (int j = 0; j < list1.size(); j++) {
                // 去重
                userSet.add(list1.get(j).getUserId());
            }
        }
        List<Long> userIds = new ArrayList<Long>(userSet);
        return userIds;
    }


    private List<DepartmentMenuVO> getChildDepartByDeId(List<Long> departmentIds) {
        // 有效的部门的数据集合
        QueryWrapper<Department> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status", 1);
        List<Department> departments = departmentService.list(queryWrapper);
        List<DepartmentMenuVO> result = new ArrayList<>();
        for (Long departmentId : departmentIds) {
            QueryWrapper<Department> query = new QueryWrapper();
            query.eq("department_id", departmentId);
            query.eq("status", 1);
            Department department = departmentService.getOne(query);
            if (department == null) {
                continue;
            }
            List<DepartmentMenuVO> departmentMenus = recursion(departmentId, department.getDepartmentName(), departments, result);
            result.addAll(departmentMenus);
        }
        return result;
    }

    private List<DepartmentMenuVO> recursion(Long departmentParent, String departmentName, List<Department> departments, List<DepartmentMenuVO> result) {
        List<DepartmentMenuVO> children = getChildDepartments(departmentParent, departmentName, departments);
        Iterator<DepartmentMenuVO> inter = children.iterator();
        while (inter.hasNext()) {
            DepartmentMenuVO departmentMenuVO = inter.next();
            List<DepartmentMenuVO> nextChildren = recursion(departmentMenuVO.getDepartmentId(), departmentMenuVO.getDepartmentName(), departments, result);
            if (nextChildren != null && nextChildren.size() > 0) {
                result.addAll(nextChildren);
                //inter.remove();
            }
        }
        return children;
    }

    private List<DepartmentMenuVO> getChildDepartments(Long departmentParent, String departmentName, List<Department> departments) {
        List<DepartmentMenuVO> children = new ArrayList<>();
        for (Department department : departments) {
            if (department.getParentId().longValue() == departmentParent) {
                DepartmentMenuVO departmentMenu = new DepartmentMenuVO();
                BeanUtils.copyProperties(department, departmentMenu);
                departmentMenu.setDepartmentName(departmentName + "," + department.getDepartmentName());
                children.add(departmentMenu);
            }
        }
        return children;
    }
}
