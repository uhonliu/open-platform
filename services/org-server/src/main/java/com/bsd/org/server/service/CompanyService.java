package com.bsd.org.server.service;

import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.vo.CompanyMenuVO;
import com.opencloud.common.mybatis.base.service.IBaseService;

import java.util.List;

/**
 * 企业信息表 服务类
 *
 * @author lrx
 * @date 2019-08-14
 */
public interface CompanyService extends IBaseService<Company> {
    /**
     * 删除公司
     *
     * @param companyId
     * @return
     */
    Boolean deleteCompany(Long companyId);

    /**
     * 获取公司组织架构菜单
     *
     * @return
     */
    List<CompanyMenuVO> getAllCompanyMenu();

    /**
     * 删除菜单缓存
     */
    void removeMeunCache();

    /**
     * 根据部门ID 获取所有下属人员的ID
     *
     * @param departmentId
     * @return java.util.List<java.lang.Long>
     * @author zhangzz
     * @date 2019/12/10
     */
    List<Long> getUserIds(Long departmentId);
}
