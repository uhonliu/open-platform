package com.bsd.payment.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.service.IMchNotifyService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

/**
 * @author liujianhong
 */
@Api(tags = "商户通知")
@RestController
@RequestMapping("/mch_notify")
public class MchNotifyController {
    @Autowired
    private IMchNotifyService mchNotifyService;

    /**
     * 商户通知列表
     *
     * @return ResultBody
     */
    @ApiOperation(value = "商户通知列表", notes = "点击商户通知进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", paramType = "form"),
            @ApiImplicitParam(name = "orderType", value = "订单类型:1-支付,2-转账,3-退款", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "通知状态,1-通知中,2-通知成功,3-通知失败", paramType = "form"),
            @ApiImplicitParam(name = "lastNotifyTimeStart", value = "最后一次通知时间开始", paramType = "form"),
            @ApiImplicitParam(name = "lastNotifyTimeEnd", value = "最后一次通知时间截止", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<MchNotify>> list(@RequestParam(value = "orderId", required = false) String orderId,
                                             @RequestParam(value = "mchId", required = false) String mchId,
                                             @RequestParam(value = "mchOrderNo", required = false) String mchOrderNo,
                                             @RequestParam(value = "orderType", required = false) String orderType,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "lastNotifyTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastNotifyTimeStart,
                                             @RequestParam(value = "lastNotifyTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastNotifyTimeEnd,
                                             @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTimeStart,
                                             @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTimeEnd,
                                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        //设置查询条件
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(orderId)) {
            map.put("orderId", orderId);
        }
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(mchOrderNo)) {
            map.put("mchOrderNo", mchOrderNo);
        }
        if (ObjectUtils.isNotEmpty(orderType)) {
            map.put("orderType", orderType);
        }
        if (ObjectUtils.isNotEmpty(status)) {
            map.put("status", status);
        }
        if (ObjectUtils.isNotEmpty(lastNotifyTimeStart)) {
            map.put("lastNotifyTimeStart", lastNotifyTimeStart);
        }
        if (ObjectUtils.isNotEmpty(lastNotifyTimeEnd)) {
            map.put("lastNotifyTimeEnd", lastNotifyTimeEnd);
        }
        if (ObjectUtils.isNotEmpty(createTimeStart)) {
            map.put("createTimeStart", createTimeStart);
        }
        if (ObjectUtils.isNotEmpty(createTimeEnd)) {
            map.put("createTimeEnd", createTimeEnd);
        }
        map.put("page", pageIndex);
        map.put("limit", pageSize);

        IPage<MchNotify> page = mchNotifyService.findListPage(new PageParams(map));
        return ResultBody.ok().data(page);
    }

    /*@ApiOperation(value = "新增商户通知", notes = "新增商户通知")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", paramType = "form"),
            @ApiImplicitParam(name = "notifyUrl", value = "通知url", paramType = "form"),
            @ApiImplicitParam(name = "result", value = "通知result", paramType = "form"),
            @ApiImplicitParam(name = "orderType", value = "订单类型:1-支付,2-转账,3-退款", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "通知状态,1-通知中,2-通知成功,3-通知失败", paramType = "form"),
            @ApiImplicitParam(name = "notifyCount", value = "通知次数", paramType = "form"),
            @ApiImplicitParam(name = "lastNotifyTime", value = "最后一次通知时间", paramType = "form")
    })
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultBody<MchNotify> update(@RequestParam(value = "orderId", required = false) String orderId,
                                        @RequestParam(value = "mchId", required = false) String mchId,
                                        @RequestParam(value = "mchOrderNo", required = false) String mchOrderNo,
                                        @RequestParam(value = "orderType", required = false) String orderType,
                                        @RequestParam(value = "status", required = false) Byte status,
                                        @RequestParam(value = "notifyUrl", required = false) String notifyUrl,
                                        @RequestParam(value = "result", required = false) String result,
                                        @RequestParam(value = "notifyCount", required = false) Byte notifyCount,
                                        @RequestParam(value = "lastNotifyTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date lastNotifyTime) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setOrderId(orderId);
        mchNotify.setMchId(mchId);
        mchNotify.setMchOrderNo(mchOrderNo);
        mchNotify.setOrderType(orderType);
        mchNotify.setStatus(status);
        mchNotify.setNotifyUrl(notifyUrl);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(lastNotifyTime);
        mchNotify.setResult(result);
        mchNotify.setCreateTime(new Date());
        boolean isCcreate = mchNotifyService.save(mchNotify);
        if (isCcreate) {
            return ResultBody.ok().msg("新增成功");
        } else {
            return ResultBody.failed().msg("新增失败");
        }
    }*/


    @ApiOperation(value = "商户通知详情", notes = "点击查看详情进入详情页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单ID", required = true, paramType = "form"),
    })
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<MchNotify> detail(@RequestParam(value = "orderId") String orderId) {
        MchNotify item = mchNotifyService.findMchNotify(orderId);
        if (item == null) {
            return ResultBody.failed().msg("获取详情失败");
        }
        return ResultBody.ok().data(item);
    }
}