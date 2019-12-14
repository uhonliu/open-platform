package com.bsd.org.server.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.enums.DepartmentLevelEnum;
import com.bsd.org.server.model.entity.Department;
import com.bsd.org.server.model.vo.DepartmentVO;
import com.bsd.org.server.service.CompanyService;
import com.bsd.org.server.service.DepartmentService;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 部门信息 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Api(value = "部门信息", tags = "部门信息")
@RestController
@RequestMapping("department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;

    /**
     * 分页获取部门数据
     *
     * @return
     */
    @ApiOperation(value = "分页获取部门数据", notes = "分页获取部门数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "部门ID", paramType = "form"),
            @ApiImplicitParam(name = "parentId", value = "上级部门ID", paramType = "form"),
            @ApiImplicitParam(name = "departmentCode", value = "部门代码", paramType = "form"),
            @ApiImplicitParam(name = "departmentName", value = "部门名称", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "状态:0-禁用 1-启用", paramType = "form"),
            @ApiImplicitParam(name = "companyId", value = "所属企业ID", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页码", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "form")
    })
    @GetMapping(value = "/page")
    public ResultBody page(
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode", required = false) String departmentCode,
            @RequestParam(value = "departmentName", required = false) String departmentName,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId", required = false) Long companyId,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        departmentVO.setDepartmentCode(departmentCode);
        departmentVO.setDepartmentId(departmentId);
        departmentVO.setDepartmentName(departmentName);
        departmentVO.setParentId(parentId);
        departmentVO.setStatus(status);
        //设置分页
        Page pageConf = new Page<DepartmentVO>(pageIndex, pageSize);
        //查询
        IPage<DepartmentVO> page = departmentService.pageByParam(pageConf, departmentVO);
        return ResultBody.ok().data(page);
    }


    @ApiOperation(value = "获取所有部门select数据", notes = "获取所有部门select数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "需要移除部门ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "companyId", value = "所属企业ID", required = true, paramType = "form")
    })
    @GetMapping(value = "/select/list")
    public ResultBody selectDepartmentList(@RequestParam(value = "departmentId", required = false) Long departmentId,
                                           @RequestParam(value = "companyId", required = true) Long companyId) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        departmentVO.setDepartmentId(departmentId);
        List<DepartmentVO> departments = departmentService.listSelectDepartments(departmentVO);
        return ResultBody.ok().data(departments);
    }


    /**
     * 查找部门信息
     */
    @ApiOperation(value = "查找部门信息", notes = "根据ID查找部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form")
    })
    @GetMapping("/get")
    public ResultBody get(@RequestParam("departmentId") Long departmentId) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return ResultBody.failed().msg("未查找到部门信息");
        }
        return ResultBody.ok().data(department);
    }


    /**
     * 获取所有部门信息
     *
     * @return
     */
    @ApiOperation(value = "获取所有部门信息", notes = "获取所有部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "所属企业ID", required = true, paramType = "form")
    })
    @GetMapping("/list")
    public ResultBody list(@RequestParam(value = "companyId", required = true) Long companyId) {
        DepartmentVO departmentVO = new DepartmentVO();
        departmentVO.setCompanyId(companyId);
        return ResultBody.ok().data(departmentService.listByParam(departmentVO));
    }

    /**
     * 获取所有启用的部门信息
     *
     * @return
     */
    @ApiOperation(value = "获取所有启用的部门信息", notes = "获取所有启用的部门信息")
    @GetMapping("/availableList")
    public ResultBody availableList() {
        return ResultBody.ok().data(departmentService.availableList());
    }


    /**
     * 获取所有下级部门
     *
     * @param departmentId
     * @return
     */
    @ApiOperation(value = "获取所有下级部门", notes = "根据ID获取所有下级部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", paramType = "form")
    })
    @GetMapping("/children")
    public ResultBody children(@RequestParam("departmentId") Long departmentId) {
        return ResultBody.ok().data(departmentService.getChildrenDepartments(departmentId, null));
    }

    /**
     * 获取所有启用的下级部门
     *
     * @param departmentId
     * @return
     */
    @ApiOperation(value = "获取所有启用的下级部门", notes = "根据ID获取所有启用的下级部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form")
    })
    @GetMapping("/availableChildrens")
    public ResultBody availableChildrens(@RequestParam("departmentId") Long departmentId) {
        return ResultBody.ok().data(departmentService.getChildrenDepartments(departmentId, true));
    }


    /**
     * 添加部门
     *
     * @return
     */
    @ApiOperation(value = "添加部门", notes = "添加部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", required = false, value = "上级部门ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "departmentCode", required = true, value = "部门代码", example = "test", paramType = "form"),
            @ApiImplicitParam(name = "departmentName", required = true, value = "部门名称", example = "测试", paramType = "form"),
            @ApiImplicitParam(name = "level", required = true, value = "部门级别", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "companyId", required = true, value = "所属企业ID", example = "1173825172121944065", paramType = "form")
    })
    @PostMapping("/add")
    public ResultBody add(
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode") String departmentCode,
            @RequestParam(value = "departmentName") String departmentName,
            @RequestParam(value = "level") Integer level,
            @RequestParam(value = "seq", required = false) Long seq,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId") Long companyId
    ) {
        //获取用户
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        //创建新增数据
        Department department = new Department();
        department.setParentId(parentId);
        department.setDepartmentCode(departmentCode);
        department.setDepartmentName(departmentName);
        department.setLevel(level);
        department.setSeq(seq);
        department.setStatus(status);
        department.setCompanyId(companyId);
        department.setCreateBy(openUserDetails.getUserId());
        departmentService.saveDepartment(department);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResultBody.ok();
    }

    /**
     * 编辑部门
     *
     * @return
     */
    @ApiOperation(value = "编辑部门", notes = "编辑部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", example = "1162211202827141121", paramType = "form"),
            @ApiImplicitParam(name = "parentId", required = false, value = "上级部门ID", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "departmentCode", required = true, value = "部门代码", example = "test", paramType = "form"),
            @ApiImplicitParam(name = "departmentName", required = true, value = "部门名称", example = "测试", paramType = "form"),
            @ApiImplicitParam(name = "level", required = true, value = "部门级别", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "seq", required = false, value = "显示顺序", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, value = "状态:0-禁用 1-启用", example = "1", paramType = "form"),
            @ApiImplicitParam(name = "companyId", required = true, value = "所属企业ID", example = "1173825172121944065", paramType = "form")
    })
    @PostMapping("/update")
    public ResultBody update(
            @RequestParam(value = "departmentId") Long departmentId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "departmentCode") String departmentCode,
            @RequestParam(value = "departmentName") String departmentName,
            @RequestParam(value = "level") Integer level,
            @RequestParam(value = "seq", required = false) Long seq,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "companyId") Long companyId
    ) {
        //获取当前授权用户
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        //更新数据
        Department department = new Department();
        department.setDepartmentId(departmentId);
        department.setParentId(parentId);
        department.setDepartmentCode(departmentCode);
        department.setDepartmentName(departmentName);
        department.setLevel(level);
        department.setSeq(seq);
        department.setStatus(status);
        department.setCompanyId(companyId);
        department.setCreateBy(openUserDetails.getUserId());
        departmentService.updateDepartment(department);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResultBody.ok();
    }


    /**
     * 禁用/启用部门信息
     *
     * @return
     */
    @ApiOperation(value = "禁用/启用部门信息", notes = "禁用/启用部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "departmentId", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "状态:0-禁用 1-启用", paramType = "form"),
    })
    @PostMapping("/status")
    public ResultBody status(@RequestParam(value = "departmentId") Long departmentId, @RequestParam(value = "status") Boolean status) {
        departmentService.changeStatus(departmentId, status);
        //删除菜单缓存信息
        companyService.removeMeunCache();
        return ResultBody.ok();
    }


    @ApiOperation(value = "部门级别列表", notes = "部门级别列表")
    @PostMapping("/levels")
    public ResultBody levels() {
        JSONArray levelArray = new JSONArray();
        Arrays.asList(DepartmentLevelEnum.values()).forEach(x -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", x.getLevelCode());
            jsonObject.put("name", x.getLevelName());
            levelArray.add(jsonObject);
        });
        JSONObject levels = new JSONObject();
        levels.put("levels", levelArray);
        return ResultBody.ok().data(levels);
    }


    /**
     * 删除部门
     *
     * @return
     */
    /*@ApiOperation(value = "删除部门", notes = "根据部门ID删除部门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = true, value = "部门ID", example = "1", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(@RequestParam(value = "departmentId", required = true) Long departmentId) {
        Department department = departmentService.getById(departmentId);
        if (department == null) {
            return ResultBody.failed().msg("部门信息不存在");
        }
        boolean isSuc = departmentService.removeById(departmentId);
        if (!isSuc) {
            return ResultBody.failed().msg("删除部门信息失败");
        }
        return ResultBody.ok();
    }*/
}
