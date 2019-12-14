package com.bsd.user.server.controller;

import com.bsd.user.server.constants.UserConstants;
import com.bsd.user.server.model.ConsigneeAddressPo;
import com.bsd.user.server.service.ConsigneeAddressService;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 收货地址 前端控制器
 *
 * @author lisongmao
 * @date 2019-07-18
 */
@RestController
@RequestMapping("/user/address")
@Api(tags = "用户收货地址管理")
public class ConsigneeAddressController {
    @Autowired
    private ConsigneeAddressService consigneeAddressService;

    @ApiOperation(value = "新增/修改收货地址", notes = "新增/修改收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consigneeName", required = true, value = "收件人", paramType = "form"),
            @ApiImplicitParam(name = "countryCode", required = true, value = "手机地区编号", paramType = "form"),
            @ApiImplicitParam(name = "mobile", required = true, value = "手机", paramType = "form"),
            @ApiImplicitParam(name = "postalCode", required = false, value = "邮编", paramType = "form"),
            @ApiImplicitParam(name = "country", required = true, value = "国家", paramType = "form"),
            @ApiImplicitParam(name = "province", required = true, value = "省", paramType = "form"),
            @ApiImplicitParam(name = "city", required = true, value = "城市", paramType = "form"),
            @ApiImplicitParam(name = "detailAddress", required = true, value = "详细地址", paramType = "form"),
            @ApiImplicitParam(name = "isDefault", required = true, value = "是否未默认地址:0-否 1-是", allowableValues = "0,1", paramType = "form"),
            @ApiImplicitParam(name = "id", required = false, value = "唯一标识,id为空表示新增,id不为空表示修改", paramType = "form"),
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话ID", paramType = "header"),
    })
    @PostMapping("/save")
    public ResultBody saveOrUpdateAddress(@RequestParam(value = "consigneeName", required = true) String consigneeName,
                                          @RequestParam(value = "countryCode", required = true) String countryCode,
                                          @RequestParam(value = "mobile", required = true) String mobile,
                                          @RequestParam(value = "postalCode", required = false) String postalCode,
                                          @RequestParam(value = "country", required = true) String country,
                                          @RequestParam(value = "province", required = true) String province,
                                          @RequestParam(value = "city", required = true) String city,
                                          @RequestParam(value = "detailAddress", required = true) String detailAddress,
                                          @RequestParam(value = "isDefault", required = true) Integer isDefault,
                                          @RequestParam(value = "id", required = false) Long id,
                                          HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        ConsigneeAddressPo po = new ConsigneeAddressPo();
        po.setLoginMobile(LoginMoblie);
        po.setConsigneeName(consigneeName);
        po.setCountryCode(countryCode);
        po.setMobile(mobile);
        po.setPostalCode(postalCode);
        po.setCountry(country);
        po.setProvince(province);
        po.setCity(city);
        po.setDetailAddress(detailAddress);
        po.setIsDefault(isDefault);
        po.setId(id);
        consigneeAddressService.saveOrUpdateConsigneeAddress(po);
        return ResultBody.ok();
    }


    @ApiOperation(value = "当前登录用户收货地址列表", notes = "当前登录用户收货地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话ID", paramType = "header"),
    })
    @PostMapping("/list")
    public ResultBody addressList(HttpServletRequest request) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        ConsigneeAddressPo po = new ConsigneeAddressPo();
        po.setLoginMobile(LoginMoblie);
        return ResultBody.ok().data(consigneeAddressService.queryUserConsigneeAddress(po));
    }


    @ApiOperation(value = "根据用户ID获取用户收货地址列表", notes = "根据用户ID获取用户收货地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", required = true, value = "用户ID", paramType = "form")
    })
    @PostMapping("/list/byUserId")
    public ResultBody addressList(@RequestParam(value = "userId", required = true) Long userId) {
        return ResultBody.ok().data(consigneeAddressService.queryUserConsigneeAddressByUserId(userId));
    }


    @ApiOperation(value = "删除收货地址", notes = "删除收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话ID", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, value = "唯一标识", paramType = "form")
    })
    @PostMapping("/delete")
    public ResultBody addressDelete(HttpServletRequest request,
                                    @RequestParam(value = "id", required = true) Long id) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        ConsigneeAddressPo po = new ConsigneeAddressPo();
        po.setLoginMobile(LoginMoblie);
        po.setId(id);
        consigneeAddressService.deleteUserConsigneeAddress(po);
        return ResultBody.ok();
    }


    @ApiOperation(value = "设置默认收货地址", notes = "设置默认收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", required = true, value = "登录token", paramType = "header"),
            @ApiImplicitParam(name = "sessionId", required = true, value = "登录会话ID", paramType = "header"),
            @ApiImplicitParam(name = "id", required = true, value = "唯一标识", paramType = "form")
    })
    @PostMapping("/setDefault")
    public ResultBody addressSetDefault(HttpServletRequest request,
                                        @RequestParam(value = "id", required = true) Long id) {
        String LoginMoblie = (String) request.getAttribute(UserConstants.LOGIN_MOBILE);
        ConsigneeAddressPo po = new ConsigneeAddressPo();
        po.setLoginMobile(LoginMoblie);
        po.setId(id);
        consigneeAddressService.setDefaultAddress(po);
        return ResultBody.ok();
    }
}
