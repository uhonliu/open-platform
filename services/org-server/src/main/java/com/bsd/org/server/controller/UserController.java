package com.bsd.org.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bsd.org.server.constants.BaseUserConst;
import com.bsd.org.server.model.entity.User;
import com.bsd.org.server.model.vo.UserDetailVO;
import com.bsd.org.server.service.CompanyService;
import com.bsd.org.server.service.UserService;
import com.bsd.org.server.service.feign.BaseUserServiceClient;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.security.OpenHelper;
import com.opencloud.common.security.OpenUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 人员信息（钉钉） 前端控制器
 *
 * @author lrx
 * @date 2019-08-14
 */
@Slf4j
@Api(value = "人员信息（钉钉）", tags = "人员信息（钉钉）")
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private BaseUserServiceClient baseUserServiceClient;

    /**
     * 分页获取人员信息(钉钉)
     *
     * @return
     */
    @ApiOperation(value = "分页获取人员信息(钉钉)", notes = "分页获取人员信息(钉钉)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "人员ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "parentId", value = "上级ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "companyId", value = "公司ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "positionId", value = "岗位ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = false, paramType = "form"),
            @ApiImplicitParam(name = "positionCode", value = "职位编码", required = false, paramType = "form"),
            @ApiImplicitParam(name = "positionName", value = "职位名称", required = false, paramType = "form"),
            @ApiImplicitParam(name = "name", value = "员工名字", required = false, paramType = "form"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = false, paramType = "form"),
            @ApiImplicitParam(name = "active", value = "是否已经激活:1已激活，0未激活", required = false, paramType = "form"),
            @ApiImplicitParam(name = "jobnumber", value = "员工工号", required = false, paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页码", required = false, paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", required = false, paramType = "form")
    })
    @GetMapping(value = "/page")
    public ResultBody page(@RequestParam(value = "userId", required = false) Long userId,
                           @RequestParam(value = "parentId", required = false) Long parentId,
                           @RequestParam(value = "companyId", required = false) Long companyId,
                           @RequestParam(value = "positionId", required = false) Long positionId,
                           @RequestParam(value = "departmentId", required = false) Long departmentId,
                           @RequestParam(value = "positionCode", required = false) String positionCode,
                           @RequestParam(value = "positionName", required = false) String positionName,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "mobile", required = false) String mobile,
                           @RequestParam(value = "active", required = false) Boolean active,
                           @RequestParam(value = "jobnumber", required = false) String jobnumber,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setUserId(userId);
        userDetailVO.setParentId(parentId);
        userDetailVO.setPositionCode(positionCode);
        userDetailVO.setPositionName(positionName);
        userDetailVO.setName(name);
        userDetailVO.setMobile(mobile);
        userDetailVO.setActive(active);
        userDetailVO.setJobnumber(jobnumber);
        //公司ID
        userDetailVO.setCompanyId(companyId);
        //部门ID
        userDetailVO.setDepartmentId(departmentId);
        //岗位ID
        userDetailVO.setPositionId(positionId);
        IPage<UserDetailVO> page = new Page<UserDetailVO>(pageIndex, pageSize);
        IPage<UserDetailVO> users = userService.userDetailListPage(page, userDetailVO);
        return ResultBody.ok().data(users);
    }

    /**
     * 根据部门ID获取用户ID列表
     *
     * @param departmentId
     * @return com.opencloud.common.model.ResultBody
     * @author zhangzz
     * @date 2019/12/9
     */
    @ApiOperation(value = "根据部门ID获取用户ID列表", notes = "根据部门ID获取用户ID列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", required = false, value = "部门ID", paramType = "form"),
            @ApiImplicitParam(name = "companyId", required = true, value = "公司ID", paramType = "form")
    })
    @GetMapping(value = "/userIds")
    public ResultBody getUserIdsByDepartmentId(@RequestParam("companyId") Long companyId, @RequestParam(value = "departmentId", required = false) Long departmentId) {
        if (companyId == null) {
            throw new OpenAlertException("参数不可为空!");
        }
        //List<Long> list = companyService.getUserIds(departmentId);
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setDepartmentId(departmentId);
        userDetailVO.setCompanyId(companyId);
        List<String> list = userService.userIdList(userDetailVO);
        return ResultBody.ok().data(list);
    }

    /**
     * 获取所有人员信息(钉钉)
     *
     * @return
     */
    @ApiOperation(value = "获取所有人员信息(钉钉)", notes = "获取所有人员信息(钉钉)")
    @GetMapping("/list")
    public ResultBody list() {
        List<UserDetailVO> users = userService.userDetailList(null);
        return ResultBody.ok().data(users);
    }


    /**
     * 查找人员信息(钉钉)
     */
    @ApiOperation(value = "查找人员信息(钉钉)", notes = "根据用户ID查找人员信息(钉钉)数据")
    @GetMapping("/get")
    public ResultBody<User> get(@RequestParam("userId") Long userId) {
        //设置条件
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setUserId(userId);
        //查询用户
        List<UserDetailVO> users = userService.userDetailList(userDetailVO);
        if (users == null || users.size() <= 0) {
            throw new OpenAlertException("用户信息不存在");
        }
        if (users.size() != 1) {
            throw new OpenAlertException("存在重复的用户信息");
        }
        return ResultBody.ok().data(users.get(0));
    }


    /**
     * 获取所有下级人员信息
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取所有下级人员信息", notes = "根据用户ID获取所有下级人员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form")
    })
    @GetMapping("/children")
    public ResultBody children(@RequestParam("userId") Long userId) {
        //设置条件
        UserDetailVO userDetailVO = new UserDetailVO();
        userDetailVO.setParentId(userId);
        //查询下级用户
        List<UserDetailVO> users = userService.userDetailList(userDetailVO);
        return ResultBody.ok().data(users);
    }


    @ApiOperation(value = "递归获取所有下级用户信息", notes = "根据用户ID递归获取所有下级用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form")
    })
    @GetMapping("/cascadeChildren")
    public ResultBody<List<UserDetailVO>> cascadeChildren(@RequestParam("userId") Long userId) {
        //递归获取所有下级用户信息
        List<UserDetailVO> users = userService.getCascadeChildren(userId);
        return ResultBody.ok().data(users);
    }

    /**
     * 添加系统用户
     *
     * @return
     */
    @ApiOperation(value = "添加系统用户", notes = "添加系统用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ddUserid", required = true, value = "员工在当前企业内的钉钉唯一标识", paramType = "form"),
            @ApiImplicitParam(name = "userName", required = true, value = "用户名", paramType = "form"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "form"),
            @ApiImplicitParam(name = "nickName", required = true, value = "昵称", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "状态:0-禁用 1-正常 2-锁定", paramType = "form"),
            @ApiImplicitParam(name = "userType", required = true, value = "用户类型:super-超级管理员 normal-普通管理员", paramType = "form"),
            @ApiImplicitParam(name = "email", required = false, value = "邮箱", paramType = "form"),
            @ApiImplicitParam(name = "mobile", required = false, value = "号码", paramType = "form"),
            @ApiImplicitParam(name = "userDesc", required = false, value = "用户描述", paramType = "form"),
            @ApiImplicitParam(name = "avatar", required = false, value = "头像", paramType = "form"),
    })
    @PostMapping("/add")
    public ResultBody add(
            @RequestParam(value = "userName") @NotEmpty(message = "用户名不能为空") String userName,
            @RequestParam(value = "password") @NotEmpty(message = "密码不能为空") String password,
            @RequestParam(value = "nickName") @NotEmpty(message = "昵称不能为空") String nickName,
            @RequestParam(value = "status") @NotEmpty(message = "状态不能为空") Integer status,
            @RequestParam(value = "userType") @NotEmpty(message = "用户类型不能为空") String userType,
            @RequestParam(value = "ddUserid") @NotEmpty(message = "用户钉钉ID不能为空") String ddUserid,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "userDesc", required = false) String userDesc,
            @RequestParam(value = "avatar", required = false) String avatar
    ) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getDdUserid, ddUserid));
        if (user == null) {
            return ResultBody.failed().msg("查询不到该钉钉唯一标识ID的用户信息");
        }
        if (user.getUserId() != null && user.getUserId().longValue() != 0) {
            return ResultBody.failed().msg("该用户已经添加过系统用户信息");
        }
        //校验参数
        if (status != BaseUserConst.USER_STATUS_FORBIDDEN && status != BaseUserConst.USER_STATUS_NORMAL && status != BaseUserConst.USER_STATUS_LOCK) {
            return ResultBody.failed().msg("用户状态参数有误,请填写指定范围");
        }
        if (!BaseUserConst.USER_TYPE_NORMAL.equals(userType) && !BaseUserConst.USER_TYPE_SUPER.equals(userType)) {
            return ResultBody.failed().msg("用户类型参数有误,请填写指定范围");
        }
        ResultBody resultBody = baseUserServiceClient.addUser(userName, password, nickName, status, userType, email, mobile, userDesc, avatar);
        if (!resultBody.isOk()) {
            return ResultBody.failed().msg(resultBody.getMessage());
        }
        Long userId = Long.valueOf(String.valueOf(resultBody.getData()));
        if (userId == null) {
            return ResultBody.failed().msg("添加系统用户失败");
        }
        user.setUserId(userId);
        boolean isSuc = userService.update(user, Wrappers.<User>lambdaQuery().eq(User::getDdUserid, ddUserid));
        if (!isSuc) {
            return ResultBody.failed().msg("绑定系统用户ID失败");
        }
        return ResultBody.ok();
    }

    /**
     * 编辑用户(钉钉)
     *
     * @return
     */
    @ApiOperation(value = "编辑用户(钉钉)", notes = "编辑用户(钉钉)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ddUserid", required = true, value = "员工在当前企业内的钉钉唯一标识", paramType = "form"),
            @ApiImplicitParam(name = "userId", required = true, value = "人员ID", example = "557063237640650752", paramType = "form"),
            @ApiImplicitParam(name = "parentId", required = false, value = "上级ID", example = "521677655146233856", paramType = "form"),
            @ApiImplicitParam(name = "positionId", required = true, value = "职位ID", example = "1162303811142623234", paramType = "form"),
            @ApiImplicitParam(name = "unionid", required = false, value = "员工在当前开发者企业账号范围内的唯一标识", example = "0235036601785503", paramType = "form"),
            @ApiImplicitParam(name = "name", required = true, value = "员工名字", example = "测试", paramType = "form"),
            @ApiImplicitParam(name = "tel", required = false, value = "分机号（仅限企业内部开发调用）", example = "", paramType = "form"),
            @ApiImplicitParam(name = "workPlace", required = false, value = "办公地点", example = "办公地点", paramType = "form"),
            @ApiImplicitParam(name = "remark", required = false, value = "备注", example = "备注", paramType = "form"),
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号码", example = "13189947695", paramType = "form"),
            @ApiImplicitParam(name = "email", required = false, value = "员工的电子邮箱", example = "13189947695@163.com", paramType = "form"),
            @ApiImplicitParam(name = "orgEmail", required = false, value = "员工的企业邮箱", example = "13189947695@163.com", paramType = "form"),
            @ApiImplicitParam(name = "active", required = false, value = "是否已经激活:1已激活，0未激活", example = "0", paramType = "form"),
            @ApiImplicitParam(name = "department", required = true, value = "成员所属部门id列表", example = "1173840209515470849", paramType = "form"),
            @ApiImplicitParam(name = "position", required = false, value = "职位信息", example = "测试", paramType = "form"),
            @ApiImplicitParam(name = "avatar", required = false, value = "头像url", example = "http://www.bsd.com/avatar", paramType = "form"),
            @ApiImplicitParam(name = "hiredDate", required = false, value = "入职时间", example = "2019-07-11 00:00:00", paramType = "form"),
            @ApiImplicitParam(name = "jobnumber", required = true, value = "员工工号", example = "20190917", paramType = "form")
    })
    @PostMapping("/update")
    public ResultBody update(
            @RequestParam(value = "ddUserid") String ddUserid,
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "positionId", required = false) Long positionId,
            @RequestParam(value = "unionid", required = false) String unionid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "tel", required = false) String tel,
            @RequestParam(value = "workPlace", required = false) String workPlace,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "mobile") String mobile,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "orgEmail", required = false) String orgEmail,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "department") String department,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "hiredDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date hiredDate,
            @RequestParam(value = "jobnumber") String jobnumber
    ) {
        OpenUserDetails openUserDetails = OpenHelper.getUser();
        User user = new User();
        user.setUserId(userId);
        user.setDdUserid(ddUserid);
        user.setParentId(parentId);
        user.setPositionId(positionId);
        user.setUnionid(unionid);
        user.setName(name);
        user.setTel(tel);
        user.setWorkPlace(workPlace);
        user.setRemark(remark);
        user.setMobile(mobile);
        user.setEmail(email);
        user.setOrgEmail(orgEmail);
        user.setActive(active);
        user.setDepartment(department);
        user.setPosition(position);
        user.setAvatar(avatar);
        user.setHiredDate(hiredDate);
        user.setJobnumber(jobnumber);
        user.setUpdateBy(openUserDetails.getUserId());
        userService.updateUser(user);
        return ResultBody.ok();
    }

    /**
     * 修改激活状态(钉钉)
     *
     * @return
     */
    @ApiOperation(value = "修改激活状态(钉钉)", notes = "修改激活状态(钉钉)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "状态:0-未激活 1-已激活", paramType = "form")
    })
    @PostMapping("/active")
    public ResultBody active(@RequestParam(value = "userId") Long userId, @RequestParam(value = "status") Boolean status) {
        userService.changeActiveStatus(userId, status);
        return ResultBody.ok();
    }

    /**
     * 删除人员(钉钉)
     *
     * @return
     */
    /*@ApiOperation(value = "删除人员(钉钉)", notes = "删除人员(钉钉)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "根据用户ID删除钉钉人员新", paramType = "form")
    })
    @PostMapping("/remove")
    public ResultBody remove(@RequestParam(value = "userId") Long userId) {
        //判断用户是否存在
        User user = userService.getByUserId(userId);
        if (user == null) {
            return ResultBody.failed().msg("人员信息(钉钉)不存在");
        }

        //删除用户
        boolean isSuc = userService.delByUserId(userId);
        if (!isSuc) {
            return ResultBody.failed().msg("删除人员信息(钉钉)失败");
        }

        return ResultBody.ok();
    }*/
}
