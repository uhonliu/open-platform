package com.bsd.user.server.controller;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.enums.UserSourceEnum;
import com.bsd.user.server.model.UserPo;
import com.bsd.user.server.model.entity.ConsigneeAddress;
import com.bsd.user.server.model.entity.User;
import com.bsd.user.server.model.entity.UserLoginLogs;
import com.bsd.user.server.model.excel.ExcelPropertyUserModel;
import com.bsd.user.server.model.vo.ConsigneeAddressVO;
import com.bsd.user.server.model.vo.UserDetailVO;
import com.bsd.user.server.model.vo.UserVO;
import com.bsd.user.server.service.ConsigneeAddressService;
import com.bsd.user.server.service.UserLoginLogsService;
import com.bsd.user.server.service.UserService;
import com.google.common.collect.Maps;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.BeanConvertUtils;
import com.opencloud.common.utils.RandomValueUtils;
import com.opencloud.common.utils.StringUtils;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户-基础信息 前端控制器
 *
 * @author lisongmao
 * @date 2019-06-26
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户登录注册")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ConsigneeAddressService consigneeAddressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserLoginLogsService userLoginLogsService;

    /**
     * 获取短信验证码
     *
     * @param mobile
     * @param type
     * @return
     */
    @ApiOperation(value = "获取短信验证码", notes = "获取短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
            @ApiImplicitParam(name = "type", required = true, value = "类型,1-注册验证码 2-登录验证码 3-忘记密码 4-微信绑定 5-修改手机账号验证 6-新手机账号验证", allowableValues = "1,2,3,4,5,6", paramType = "form"),
            @ApiImplicitParam(name = "signSource", required = false, value = "短信签名 0-跨境知道 1-卖家成长 默认为0", allowableValues = "1,2,3,4,5,6", paramType = "form"),
    })
    @PostMapping("/get/smscode")
    public ResultBody getcode(@RequestParam(value = "mobile", required = true) String mobile,
                              @RequestParam(value = "type", required = true) Integer type,
                              @RequestParam(value = "signSource", required = false, defaultValue = "0") Integer signSource) {
        userService.sendSmsCode(mobile, type, signSource);
        return ResultBody.ok();
    }


    /**
     * 手机验证码注册
     *
     * @param mobile
     * @param password
     * @param code
     * @param source
     * @param request
     * @return
     */
    @ApiOperation(value = "手机验证码注册", notes = "手机验证码注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "短信验证码", paramType = "form"),
            @ApiImplicitParam(name = "source", required = true, value = "0-跨境知道 1-卖家成长 3-人工录入", allowableValues = "0,1,2,3", paramType = "form"),
            @ApiImplicitParam(name = "invitedCode", required = false, value = "邀请码", paramType = "form")
    })
    @PostMapping("/register")
    public ResultBody register(@RequestParam(value = "mobile", required = true) String mobile,
                               @RequestParam(value = "password", required = true) String password,
                               @RequestParam(value = "code", required = true) String code,
                               @RequestParam(value = "source", required = true) Integer source,
                               @RequestParam(value = "invitedCode", required = false) String invitedCode,
                               HttpServletRequest request) {
        UserPo user = new UserPo();
        user.setMobile(mobile);
        user.setPassword(password);
        user.setSource(source);
        user.setInputCode(code);
        user.setRegisterIp(WebUtils.getRemoteAddress(request));
        user.setInvitedCode(invitedCode);
        Integer result = userService.registerByPhone(user);
        if (result == null || result <= 0) {
            return ResultBody.failed();
        }
        return ResultBody.ok();
    }


    /**
     * 手机验证码登录
     *
     * @param mobile
     * @param code
     * @return
     */

    @ApiOperation(value = "手机验证码登录", notes = "手机验证码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "短信验证码", paramType = "form"),
    })
    @PostMapping("/login/smscode")
    public ResultBody loginByMobileCode(@RequestParam(value = "mobile", required = true) String mobile,
                                        @RequestParam(value = "code", required = true) String code,
                                        HttpServletRequest request) {
        UserPo user = new UserPo();
        user.setMobile(mobile);
        user.setInputCode(code);
        user.setLoginIp(WebUtils.getRemoteAddress(request));
        Map<String, Object> map = userService.loginByMobileCode(user);
        return ResultBody.ok().data(map);
    }

    /**
     * 手机+密码登录
     *
     * @param mobile
     * @param password
     * @return
     */
    @ApiOperation(value = "手机密码登录", notes = "手机密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "form"),
    })
    @PostMapping("/login/password")
    public ResultBody loginByMobilePassword(@RequestParam(value = "mobile", required = true) String mobile,
                                            @RequestParam(value = "password", required = true) String password,
                                            HttpServletRequest request) {
        UserPo user = new UserPo();
        user.setMobile(mobile);
        user.setPassword(password);
        user.setLoginIp(WebUtils.getRemoteAddress(request));
        Map<String, Object> map = userService.loginByMobilePassword(user);
        return ResultBody.ok().data(map);
    }

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @ApiOperation(value = "获取登录用户信息", notes = "获取登录用户信息,可以用于登录token验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录后返回的会话id", paramType = "header"),
            @ApiImplicitParam(name = "token", required = true, value = "登录后返回的token", paramType = "header"),
    })
    @PostMapping("/get/info")
    public ResultBody getInfo(HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        UserPo user = new UserPo();
        user.setLoginMobile(LoginMoblie);
        Map<String, Object> map = userService.authenticatingToken(user);
        return ResultBody.ok().data(map);
    }

    /**
     * 登录token验证
     *
     * @return
     */
    @ApiOperation(value = "登录token验证", notes = "登录token验证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录后返回的会话id", paramType = "header"),
            @ApiImplicitParam(name = "token", required = true, value = "登录后返回的token", paramType = "header"),
    })
    @PostMapping("/verify/token")
    public ResultBody authenticatingToken(HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        UserPo user = new UserPo();
        user.setLoginMobile(LoginMoblie);
        Map<String, Object> map = userService.authenticatingToken(user);
        return ResultBody.ok();
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", required = true, value = "原密码", paramType = "form"),
            @ApiImplicitParam(name = "newPassword", required = true, value = "新密码", paramType = "form"),
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话id", paramType = "header"),
    })
    @PostMapping("/update/password")
    public ResultBody updatePassword(@RequestParam(value = "oldPassword", required = true) String oldPassword,
                                     @RequestParam(value = "newPassword", required = true) String newPassword,
                                     HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        UserPo user = new UserPo();
        user.setOldPassword(oldPassword);
        user.setPassword(newPassword);
        user.setLoginMobile(LoginMoblie);
        userService.updatePassword(user);
        return ResultBody.ok();
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", paramType = "form"),
            @ApiImplicitParam(name = "newPassword", required = true, value = "新密码", paramType = "form"),
    })
    @PostMapping("/reset/password")
    public ResultBody resetPassword(@RequestParam(value = "mobile", required = true) String mobile,
                                    @RequestParam(value = "code", required = true) String code,
                                    @RequestParam(value = "newPassword", required = true) String newPassword) {
        UserPo user = new UserPo();
        user.setMobile(mobile);
        user.setPassword(newPassword);
        user.setInputCode(code);
        userService.resetPassword(user);
        return ResultBody.ok();
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话id", paramType = "header"),
            @ApiImplicitParam(name = "nickname", required = false, value = "昵称", paramType = "form"),
            @ApiImplicitParam(name = "avatar", required = false, value = "头像", paramType = "form"),
            @ApiImplicitParam(name = "email", required = false, value = "邮箱", paramType = "form"),
            @ApiImplicitParam(name = "userDesc", required = false, value = "描述", paramType = "form"),
    })
    @PostMapping("/update/info")
    public ResultBody updateUserInfo(@RequestParam(value = "nickname", required = false) String nickname,
                                     @RequestParam(value = "avatar", required = false) String avatar,
                                     @RequestParam(value = "email", required = false) String email,
                                     @RequestParam(value = "userDesc", required = false) String userDesc,
                                     HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        UserPo user = new UserPo();
        user.setLoginMobile(LoginMoblie);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setEmail(email);
        user.setUserDesc(userDesc);
        userService.updateUserInfo(user);
        return ResultBody.ok();
    }

    /**
     * 验证旧账号手机(账号)
     *
     * @param password
     * @param code
     * @return
     */
    @ApiOperation(value = "更换手机-验证旧手机", notes = "更换手机-验证旧手机")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话id", paramType = "header"),
            @ApiImplicitParam(name = "password", required = true, value = "密码", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", paramType = "form"),
    })
    @PostMapping("/verify/OldMobile")
    public ResultBody verifyOldMobile(
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "code", required = true) String code,
            HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        UserPo po = new UserPo();
        po.setInputCode(code);
        po.setLoginMobile(LoginMoblie);
        po.setOldPassword(password);
        userService.verifyOldMobile(po);
        return ResultBody.ok();
    }


    /**
     * 修改手机号(账号)
     *
     * @param newMobile
     * @param code
     * @return
     */
    @ApiOperation(value = "更换手机", notes = "更换手机")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话id", paramType = "header"),
            @ApiImplicitParam(name = "newMobile", required = true, value = "新手机", paramType = "form"),
            @ApiImplicitParam(name = "code", required = true, value = "验证码", paramType = "form"),
    })
    @PostMapping("/update/mobile")
    public ResultBody updateMobile(@RequestParam(value = "newMobile", required = true) String newMobile,
                                   @RequestParam(value = "code", required = true) String code,
                                   HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        Map<String, Object> loginInfoMap = (Map<String, Object>) request.getAttribute(UserConstants.LOGIN_INFO);
        UserPo po = new UserPo();
        po.setInputCode(code);
        po.setLoginMobile(LoginMoblie);
        po.setMobile(newMobile);
        po.setSessionId(loginInfoMap.get(UserConstants.SESSIONID).toString());
        userService.updateMobile(po);
        return ResultBody.ok();
    }


    /**
     * 分页获取用户列表
     *
     * @param source
     * @param status
     * @param searchContent
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页获取用户列表", notes = "分页获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", required = false, value = "用户来源(0-跨境知道 1-卖家成长 3-人工录入)", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, value = "账号状态(0-禁用 1-启用 2-锁定)", paramType = "form"),
            @ApiImplicitParam(name = "searchContent", required = false, value = "用户昵称/手机号/用户ID", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", required = false, value = "页码", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", required = false, value = "每页条数", paramType = "form")
    })
    @PostMapping("/list")
    public ResultBody list(@RequestParam(value = "source", required = false) Integer source,
                           @RequestParam(value = "status", required = false) Integer status,
                           @RequestParam(value = "searchContent", required = false) String searchContent,
                           @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        //查询条件
        UserPo userPo = new UserPo();
        userPo.setSource(source);
        userPo.setStatus(status);
        userPo.setSearchContent(searchContent);
        //获取分页数据
        IPage resultPage = userService.userPageList(pageIndex, pageSize, userPo);
        //BO转VO
        List<UserVO> users = BeanConvertUtils.copyList(resultPage.getRecords(), UserVO.class);
        resultPage.setRecords(users);

        return ResultBody.ok().data(resultPage);
    }


    /**
     * 手工录入用户数据
     *
     * @param mobile
     * @param username
     * @param sex
     * @return
     */
    @ApiOperation(value = "手工录入用户数据", notes = "手工录入用户数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", required = true, value = "手机号码", paramType = "form"),
            @ApiImplicitParam(name = "username", required = false, value = "用户名称", paramType = "form"),
            @ApiImplicitParam(name = "sex", required = true, value = "性别 (0 保密  1男 2女)", paramType = "form"),
    })
    @PostMapping("/manual/save")
    public ResultBody list(@RequestParam(value = "mobile", required = true) String mobile,
                           @RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "sex", required = true) Integer sex) {
        //查询条件
        UserPo userPo = new UserPo();
        //手工录入
        userPo.setSource(UserConstants.USER_SOURCE3);
        //号码
        userPo.setMobile(mobile);
        //姓名
        userPo.setUsername(username);
        //手工录入默认昵称跟姓名一样
        userPo.setNickname(username);
        //性别设置
        userPo.setSex(sex);
        //userCode随机生成
        userPo.setUserCode(RandomValueUtils.uuid());
        userPo.setUpdateFlag(UserConstants.UPDATEFLAG1);
        //默认密码设置
        userPo.setPassword(passwordEncoder.encode(UserConstants.MANUAL_INPUT_USER_DEFAULT_PASSWORD));
        boolean isSuc = userService.manualSave(userPo);
        if (!isSuc) {
            throw new OpenAlertException("手工录入用户数据保存失败");
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("user_id", userPo.getUserId());
        return ResultBody.ok().data(result);
    }

    /**
     * 导出用户列表
     *
     * @param source
     * @param status
     * @param searchContent
     * @return
     */
    @ApiOperation(value = "导出用户列表", notes = "导出用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", required = false, value = "用户来源(0-跨境知道 1-卖家成长 3-人工录入)", paramType = "form"),
            @ApiImplicitParam(name = "status", required = false, value = "账号状态(0-禁用 1-启用 2-锁定)", paramType = "form"),
            @ApiImplicitParam(name = "searchContent", required = false, value = "用户昵称/手机号/用户ID", paramType = "form"),
    })
    @PostMapping(value = "/list/export")
    public void list(@RequestParam(value = "source", required = false) Integer source,
                     @RequestParam(value = "status", required = false) Integer status,
                     @RequestParam(value = "searchContent", required = false) String searchContent,
                     HttpServletResponse response) {
        //查询条件
        UserPo userPo = new UserPo();
        userPo.setSource(source);
        userPo.setStatus(status);
        userPo.setSearchContent(searchContent);
        //查询用户数据,限制一下导出最大行数(产品要求,查询结果条数超过1W条时候,截取前1W条数据)
        List<User> users = userService.exportMaxUserList(userPo);
        if (users == null || users.size() <= 0) {
            throw new OpenAlertException("未查到用户列表信息");
        }
        //导出用户数据
        try {
            //将PO对象转为导出数据的模型
            List<ExcelPropertyUserModel> userModels = BeanConvertUtils.copyList(users, ExcelPropertyUserModel.class);
            //获取输出流
            ServletOutputStream out = response.getOutputStream();
            //设置文件名
            String fileName = new String(("用户信息-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())).getBytes(), "UTF-8");
            //文件名字做URL编码处理
            fileName = URLEncoder.encode(fileName, "utf-8");
            //设置头部信息
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet = new Sheet(1, 0, ExcelPropertyUserModel.class);
            sheet.setSheetName("用户数据");
            //导出数据
            writer.write(userModels, sheet);
            //输出数据,关闭流
            writer.finish();
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new OpenAlertException("导出用户数据失败");
        }
    }


    @ApiOperation(value = "获取用户来源列表", notes = "获取用户来源列表")
    @PostMapping("/source/list")
    public ResultBody list() {
        JSONArray sourceArray = new JSONArray();
        Arrays.asList(UserSourceEnum.values()).forEach(x -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", x.getSourceName());
            jsonObject.put("name", x.getSourceCode());
            sourceArray.add(jsonObject);
        });
        JSONObject userSources = new JSONObject();
        userSources.put("user_source", sourceArray);
        return ResultBody.ok().data(userSources);
    }


    /**
     * 根据ID获取用户详情
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户详情", notes = "根据ID获取用户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form"),
    })
    @PostMapping("/detail")
    public ResultBody detail(@RequestParam(value = "userId", required = true) Long userId) {
        //查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return ResultBody.failed().msg("用户详情不存在");
        }
        //查询用户地址信息
        List<ConsigneeAddress> addressList = consigneeAddressService.queryUserConsigneeAddressByUserId(userId);
        //查询用户最后登录时间
        UserLoginLogs lastLoginLog = userLoginLogsService.findUserLastLoginLog(userId);
        //封装结果
        Map<String, Object> map = Maps.newHashMap();
        try {
            UserDetailVO userVO = BeanConvertUtils.convertBean(user, UserDetailVO.class);
            String sourceName = UserSourceEnum.userSourceMap().get(userVO.getSource());
            if (sourceName == null) {
                throw new OpenAlertException("找不到对应的用户来源");
            }
            userVO.setSourceStr(sourceName);
            //最后一次登录日志存在时候
            if (lastLoginLog != null && lastLoginLog.getLoginTime() != null) {
                userVO.setLastLoginTime(lastLoginLog.getLoginTime());
            }
            //用户头像处理
            if (StringUtils.isNotEmpty(userVO.getAvatar()) && !userVO.getAvatar().startsWith("https") && !userVO.getAvatar().startsWith("http")) {
                //非https,http开头的,不是全地址,需要补上
                userVO.setAvatar("https" + userVO.getAvatar());
            }
            map.put("user", userVO);
            map.put("address", BeanConvertUtils.copyList(addressList, ConsigneeAddressVO.class));
        } catch (Exception ex) {
            log.error("PO转VO异常:{}", ex.getMessage());
            throw new OpenAlertException("获取详情失败");
        }
        return ResultBody.ok().data(map);
    }


    /**
     * 批量推送用户到客户系统
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量推送用户到客户系统", notes = "批量推送用户到客户系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "用户ID,多个用,号隔开", paramType = "form"),
    })
    @PostMapping("/batch/push")
    public ResultBody push(@RequestParam(value = "ids", required = true) String ids, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        boolean isSuc = userService.batchPush(Arrays.asList(ids.split(",")), authorization);
        if (!isSuc) {
            return ResultBody.failed().msg("批量推送用户信息到客户系统失败");
        }
        return ResultBody.ok();
    }

    /**
     * 批量修改用户状态
     *
     * @return
     */
    @ApiOperation(value = "批量修改用户状态", notes = "批量修改用户状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "多个用,号隔开", paramType = "form"),
            @ApiImplicitParam(name = "status", required = true, value = "0-禁用 1-启用", paramType = "form")
    })
    @PostMapping("/batch/change")
    public ResultBody changeStatus(@RequestParam(value = "ids", required = true) String ids,
                                   @RequestParam(value = "status", required = true) Integer status) {
        boolean isSuc = userService.batchChangeStatus(Arrays.asList(ids.split(",")), status);
        if (!isSuc) {
            return ResultBody.failed().msg("批量修改用户状态失败");
        }
        return ResultBody.ok();
    }
}
