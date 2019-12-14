package com.bsd.org.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.model.entity.Company;
import com.bsd.org.server.model.vo.CompanyMenuVO;
import com.bsd.org.server.service.CompanyService;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import com.opencloud.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 企业信息 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Slf4j
@Api(value = "企业信息", tags = "企业信息")
@RestController
@RequestMapping("company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    /**
     * 获取企业信息分页数据
     *
     * @return
     */
    @ApiOperation(value = "分页获取企业信息", notes = "分页获取企业信息")
    @GetMapping(value = "/page")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "companyId", value = "企业ID", paramType = "form"),
            @ApiImplicitParam(name = "companyName", value = "企业全称", paramType = "form"),
            @ApiImplicitParam(name = "companyNameEn", value = "企业英文名", paramType = "form"),
            @ApiImplicitParam(name = "natureId", value = "企业性质ID", paramType = "form"),
            @ApiImplicitParam(name = "industryId", value = "所属行业ID", paramType = "form"),
            @ApiImplicitParam(name = "areaId", value = "所在区域ID", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页码", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", paramType = "form")
    })
    public ResultBody page(Long companyId,
                           @RequestParam(value = "companyName", required = false) String companyName,
                           @RequestParam(value = "companyNameEn", required = false) String companyNameEn,
                           @RequestParam(value = "natureId", required = false) Integer natureId,
                           @RequestParam(value = "industryId", required = false) Integer industryId,
                           @RequestParam(value = "areaId", required = false) Integer areaId,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        //企业信息查询条件
        Company queryCompany = new Company();
        queryCompany.setCompanyId(companyId);
        queryCompany.setCompanyName(companyName);
        queryCompany.setCompanyNameEn(companyNameEn);
        queryCompany.setNatureId(natureId);
        queryCompany.setIndustryId(industryId);
        queryCompany.setAreaId(areaId);
        //查询分页数据
        IPage<Company> pageData = companyService.page(new Page<Company>(pageIndex, pageSize), Wrappers.<Company>query(queryCompany));
        return ResultBody.ok().data(pageData);
    }


    /**
     * 获取所有企业信息
     *
     * @param
     * @return com.opencloud.common.model.ResultBody
     * @author zhangzz
     * @date 2019/12/6
     */
    @ApiOperation(value = "获取所有企业信息", notes = "获取所有企业信息数据")
    @GetMapping(value = "/findAll")
    @ApiImplicitParams(value = {})
    public ResultBody findAll() {
        List<Company> companys = companyService.list();
        return ResultBody.ok().data(companys);
    }


    /**
     * 查找企业信息
     */
    @ApiOperation(value = "查找企业信息", notes = "根据ID查找企业信息")
    @GetMapping("/get")
    public ResultBody get(@RequestParam(value = "companyId") Long companyId) {
        Company company = companyService.getById(companyId);
        if (company == null) {
            return ResultBody.failed().msg("未查找到ID为" + companyId + "的企业信息");
        }
        return ResultBody.ok().data(company);
    }


    /**
     * 添加企业信息
     *
     * @return
     */
    @ApiOperation(value = "添加企业信息", notes = "添加企业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyName", required = true, value = "企业全称", example = "必胜道", paramType = "form"),
            @ApiImplicitParam(name = "companyNameEn", required = false, value = "企业英文名", example = "BSD", paramType = "form"),
            @ApiImplicitParam(name = "natureId", required = false, value = "企业性质ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "industryId", required = false, value = "所属行业ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "areaId", required = false, value = "所在区域ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "establishedTime", required = false, value = "成立时间", example = "2019-09-17 00:00:00", paramType = "form"),
            @ApiImplicitParam(name = "registeredCapital", required = false, value = "注册资金", example = "1000000", paramType = "form"),
            @ApiImplicitParam(name = "staffNum", required = false, value = "员工人数", example = "200", paramType = "form"),
            @ApiImplicitParam(name = "website", required = false, value = "公司网址", example = "http://www.bsd.com", paramType = "form"),
            @ApiImplicitParam(name = "profile", required = false, value = "公司介绍", example = "公司介绍", paramType = "form"),
            @ApiImplicitParam(name = "contact", required = true, value = "联系人", example = "联系人", paramType = "form"),
            @ApiImplicitParam(name = "phone", required = true, value = "电话", example = "13189947695", paramType = "form"),
            @ApiImplicitParam(name = "fax", required = false, value = "传真", example = "fax", paramType = "form"),
            @ApiImplicitParam(name = "email", required = false, value = "电子邮件", example = "13189947695@163.com", paramType = "form"),
            @ApiImplicitParam(name = "address", required = false, value = "通信地址", example = "广东省深圳市", paramType = "form"),
            @ApiImplicitParam(name = "postCode", required = false, value = "邮政编码", example = "518000", paramType = "form"),
            @ApiImplicitParam(name = "logo", required = false, value = "企业Logo", example = "http://www.bsd.com/logo", paramType = "form"),
    })
    @PostMapping("/add")
    public ResultBody add(
            @RequestParam(value = "companyName") String companyName,
            @RequestParam(value = "companyNameEn", required = false) String companyNameEn,
            @RequestParam(value = "natureId", required = false) Integer natureId,
            @RequestParam(value = "industryId", required = false) Integer industryId,
            @RequestParam(value = "areaId", required = false) Integer areaId,
            @RequestParam(value = "establishedTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date establishedTime,
            @RequestParam(value = "registeredCapital", required = false) BigDecimal registeredCapital,
            @RequestParam(value = "staffNum", required = false) Integer staffNum,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "profile", required = false) String profile,
            @RequestParam(value = "contact") String contact,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "fax", required = false) String fax,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "postCode", required = false) String postCode,
            @RequestParam(value = "logo", required = false) String logo
    ) {
        Company company = new Company();
        company.setCompanyName(companyName);
        company.setCompanyNameEn(companyNameEn);
        company.setNatureId(natureId);
        company.setIndustryId(industryId);
        company.setAreaId(areaId);
        company.setEstablishedTime(establishedTime);
        company.setRegisteredCapital(registeredCapital);
        company.setStaffNum(staffNum);
        company.setWebsite(website);
        company.setProfile(profile);
        company.setContact(contact);
        if (!StringUtils.matchMobile(phone)) {
            return ResultBody.failed().msg("号码格式错误");
        }
        company.setPhone(phone);
        company.setFax(fax);
        company.setEmail(email);
        company.setAddress(address);
        company.setPostCode(postCode);
        company.setLogo(logo);
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        company.setCreateBy(openUserDetails.getUserId());
        boolean isSuc = companyService.save(company);
        if (!isSuc) {
            return ResultBody.failed().msg("添加企业信息失败");
        }
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResultBody.ok();
    }


    /**
     * 编辑企业信息
     *
     * @return
     */
    @ApiOperation(value = "编辑企业信息", notes = "编辑企业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", required = true, value = "企业ID", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "companyName", required = true, value = "企业全称", example = "必胜道", paramType = "form"),
            @ApiImplicitParam(name = "companyNameEn", required = false, value = "企业英文名", example = "BSD", paramType = "form"),
            @ApiImplicitParam(name = "natureId", required = false, value = "企业性质ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "industryId", required = false, value = "所属行业ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "areaId", required = false, value = "所在区域ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "establishedTime", required = false, value = "成立时间", example = "2019-09-17 00:00:00", paramType = "form"),
            @ApiImplicitParam(name = "registeredCapital", required = false, value = "注册资金", example = "1000000", paramType = "form"),
            @ApiImplicitParam(name = "staffNum", required = false, value = "员工人数", example = "200", paramType = "form"),
            @ApiImplicitParam(name = "website", required = false, value = "公司网址", example = "http://www.bsd.com", paramType = "form"),
            @ApiImplicitParam(name = "profile", required = false, value = "公司介绍", example = "公司介绍", paramType = "form"),
            @ApiImplicitParam(name = "contact", required = true, value = "联系人", example = "联系人", paramType = "form"),
            @ApiImplicitParam(name = "phone", required = true, value = "电话", example = "13189947695", paramType = "form"),
            @ApiImplicitParam(name = "fax", required = false, value = "传真", example = "fax", paramType = "form"),
            @ApiImplicitParam(name = "email", required = false, value = "电子邮件", example = "13189947695@163.com", paramType = "form"),
            @ApiImplicitParam(name = "address", required = false, value = "通信地址", example = "广东省深圳市", paramType = "form"),
            @ApiImplicitParam(name = "postCode", required = false, value = "邮政编码", example = "518000", paramType = "form"),
            @ApiImplicitParam(name = "logo", required = false, value = "企业Logo", example = "http://www.bsd.com/logo", paramType = "form")
    })
    @PostMapping("/update")
    public ResultBody update(
            @RequestParam(value = "companyId") Long companyId,
            @RequestParam(value = "companyName") String companyName,
            @RequestParam(value = "companyNameEn", required = false) String companyNameEn,
            @RequestParam(value = "natureId", required = false) Integer natureId,
            @RequestParam(value = "industryId", required = false) Integer industryId,
            @RequestParam(value = "areaId", required = false) Integer areaId,
            @RequestParam(value = "establishedTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date establishedTime,
            @RequestParam(value = "registeredCapital", required = false) BigDecimal registeredCapital,
            @RequestParam(value = "staffNum", required = false) Integer staffNum,
            @RequestParam(value = "website", required = false) String website,
            @RequestParam(value = "profile", required = false) String profile,
            @RequestParam(value = "contact") String contact,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "fax", required = false) String fax,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "postCode", required = false) String postCode,
            @RequestParam(value = "logo", required = false) String logo
    ) {
        Company dbCompany = companyService.getById(companyId);
        if (dbCompany == null) {
            return ResultBody.failed().msg("企业信息不存在");
        }
        Company company = new Company();
        company.setCompanyId(companyId);
        company.setCompanyName(companyName);
        company.setCompanyNameEn(companyNameEn);
        company.setNatureId(natureId);
        company.setIndustryId(industryId);
        company.setAreaId(areaId);
        company.setEstablishedTime(establishedTime);
        company.setRegisteredCapital(registeredCapital);
        company.setStaffNum(staffNum);
        company.setWebsite(website);
        company.setProfile(profile);
        company.setContact(contact);
        if (!StringUtils.matchMobile(phone)) {
            return ResultBody.failed().msg("号码格式错误");
        }
        company.setPhone(phone);
        company.setFax(fax);
        company.setEmail(email);
        company.setAddress(address);
        company.setPostCode(postCode);
        company.setLogo(logo);
        //设置更新人
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        company.setUpdateBy(openUserDetails.getUserId());
        boolean isSuc = companyService.updateById(company);
        if (!isSuc) {
            return ResultBody.failed().msg("更新数据失败");
        }
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResultBody.ok();
    }


    @ApiOperation(value = "获取公司部门菜单信息", notes = "获取公司部门菜单信息")
    @GetMapping("/get/menu")
    public ResultBody<List<CompanyMenuVO>> getMenu() {
        List<CompanyMenuVO> menus = companyService.getAllCompanyMenu();
        return ResultBody.ok().data(menus);
    }


    /**
     * 删除企业信息
     *
     * @return
     */
    /*@ApiOperation(value = "删除企业信息", notes = "根据企业ID删除企业信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", required = true, value = "企业ID", example = "1", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(@RequestParam(value = "companyId", required = true) Long companyId) {
        companyService.deleteCompany(companyId);
        return ResultBody.ok();
    }*/
}
