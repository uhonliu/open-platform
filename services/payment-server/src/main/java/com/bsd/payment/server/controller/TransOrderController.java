package com.bsd.payment.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.model.dto.QueryTransResultDTO;
import com.bsd.payment.server.model.dto.SimpleTransDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.service.ITransOrderService;
import com.bsd.payment.server.service.mq.MqService;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;

/**
 * @author liujianhong
 */
@Api(tags = "转账订单")
@RestController
@RequestMapping("/trans_order")
public class TransOrderController {
    @Autowired
    private ITransOrderService transOrderService;

    @Resource(name = "rabbitMq4TransServiceImpl")
    private MqService rabbitMq4TransServiceImpl;

    @Autowired
    private IPayChannelService payChannelService;

    /**
     * 转账订单列表
     *
     * @return ResultBody
     */
    @ApiOperation(value = "转账订单列表", notes = "点击转账订单进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transOrderId", value = "转账订单号", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchTransNo", value = "商户转账单号", paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "转账状态：0-订单生成,1-转账中,2-转账成功,3-转账失败,4-业务处理完成", paramType = "form"),
            @ApiImplicitParam(name = "channelUser", value = "渠道用户标识,如微信openId,支付宝账号", paramType = "form"),
            @ApiImplicitParam(name = "userName", value = "用户姓名", paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", paramType = "form"),
            @ApiImplicitParam(name = "channelOrderNo", value = "渠道订单号", paramType = "form"),
            @ApiImplicitParam(name = "transSuccTimeStart", value = "订单转账成功时间开始", paramType = "form"),
            @ApiImplicitParam(name = "transSuccTimeEnd", value = "订单转账成功时间截止", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<TransOrder>> list(@RequestParam(value = "transOrderId", required = false) String transOrderId,
                                              @RequestParam(value = "mchId", required = false) String mchId,
                                              @RequestParam(value = "mchTransNo", required = false) String mchTransNo,
                                              @RequestParam(value = "channelCode", required = false) String channelCode,
                                              @RequestParam(value = "status", required = false) String status,
                                              @RequestParam(value = "channelUser", required = false) String channelUser,
                                              @RequestParam(value = "userName", required = false) String userName,
                                              @RequestParam(value = "channelMchId", required = false) String channelMchId,
                                              @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
                                              @RequestParam(value = "transSuccTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date transSuccTimeStart,
                                              @RequestParam(value = "transSuccTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date transSuccTimeEnd,
                                              @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTimeStart,
                                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTimeEnd,
                                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(transOrderId)) {
            map.put("transOrderId", transOrderId);
        }
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(mchTransNo)) {
            map.put("mchTransNo", mchTransNo);
        }
        if (ObjectUtils.isNotEmpty(channelCode)) {
            map.put("channelCode", channelCode);
        }
        if (ObjectUtils.isNotEmpty(status)) {
            map.put("status", status);
        }
        if (ObjectUtils.isNotEmpty(channelUser)) {
            map.put("channelUser", channelUser);
        }
        if (ObjectUtils.isNotEmpty(userName)) {
            map.put("userName", userName);
        }
        if (ObjectUtils.isNotEmpty(channelMchId)) {
            map.put("channelMchId", channelMchId);
        }
        if (ObjectUtils.isNotEmpty(channelOrderNo)) {
            map.put("channelOrderNo", channelOrderNo);
        }
        if (ObjectUtils.isNotEmpty(transSuccTimeStart)) {
            map.put("transSuccTimeStart", transSuccTimeStart);
        }
        if (ObjectUtils.isNotEmpty(transSuccTimeEnd)) {
            map.put("transSuccTimeEnd", transSuccTimeEnd);
        }
        if (ObjectUtils.isNotEmpty(createTimeStart)) {
            map.put("createTimeStart", createTimeStart);
        }
        if (ObjectUtils.isNotEmpty(createTimeEnd)) {
            map.put("createTimeEnd", createTimeEnd);
        }
        map.put("page", pageIndex);
        map.put("limit", pageSize);


        return ResultBody.ok().data(transOrderService.findListPage(new PageParams(map)));
    }

    @ApiOperation(value = "转账订单详情", notes = "点击查看详情进入详情页面")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<TransOrder> detail(@RequestParam String transOrderId) {
        TransOrder transOrder = transOrderService.findTransOrder(transOrderId);
        if (transOrder == null) {
            return ResultBody.failed().msg("未查找到ID为" + transOrderId + "的转账订单信息");
        }
        return ResultBody.ok().data(transOrder);
    }

    @ApiOperation(value = "发起转账", notes = "发起转账")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchTransNo", value = "商户转账单号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "amount", value = "转账金额(单位分)", required = true, paramType = "form"),
            @ApiImplicitParam(name = "currency", value = "三位货币代码,人民币:cny(默认为cny)", paramType = "form"),
            @ApiImplicitParam(name = "clientIp", value = "客户端IP", paramType = "form"),
            @ApiImplicitParam(name = "device", value = "设备", paramType = "form"),
            @ApiImplicitParam(name = "extra", value = "特定渠道发起时额外参数", paramType = "form"),
            @ApiImplicitParam(name = "param1", value = "扩展参数1", paramType = "form"),
            @ApiImplicitParam(name = "param2", value = "扩展参数2", paramType = "form"),
            @ApiImplicitParam(name = "notifyUrl", value = "转账结果回调URL(不传则结果不回调给商户)", paramType = "form"),
            @ApiImplicitParam(name = "channelUser", value = "渠道用户标识,如微信openId,支付宝账号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "userName", value = "用户姓名", required = true, paramType = "form"),
            @ApiImplicitParam(name = "remarkInfo", value = "备注", required = true, paramType = "form"),
            @ApiImplicitParam(name = "sign", value = "签名", required = true, paramType = "form")
    })
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBody create(@RequestParam(value = "mchId") String mchId,
                             @RequestParam(value = "mchTransNo") String mchTransNo,
                             @RequestParam(value = "channelCode") String channelCode,
                             @RequestParam(value = "amount") Long amount,
                             @RequestParam(value = "currency", required = false, defaultValue = "") String currency,
                             @RequestParam(value = "clientIp", required = false, defaultValue = "") String clientIp,
                             @RequestParam(value = "device", required = false, defaultValue = "") String device,
                             @RequestParam(value = "extra", required = false, defaultValue = "") String extra,
                             @RequestParam(value = "param1", required = false, defaultValue = "") String param1,
                             @RequestParam(value = "param2", required = false, defaultValue = "") String param2,
                             @RequestParam(value = "notifyUrl", required = false, defaultValue = "") String notifyUrl,
                             @RequestParam(value = "channelUser") String channelUser,
                             @RequestParam(value = "userName") String userName,
                             @RequestParam(value = "remarkInfo") String remarkInfo,
                             @RequestParam(value = "sign") String sign,
                             HttpServletRequest request) {
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            return ResultBody.failed().msg("找不到支付渠道");
        }

        //创建转账订单
        TransOrder transOrder = new TransOrder();
        transOrder.setMchTransNo(mchTransNo);
        transOrder.setMchId(mchId);
        transOrder.setChannelCode(channelCode);
        transOrder.setChannelUser(channelUser);
        transOrder.setUserName(userName);
        transOrder.setAmount(amount);
        transOrder.setCurrency(currency);
        transOrder.setRemarkInfo(remarkInfo);
        transOrder.setExtra(extra);
        transOrder.setParam1(param1);
        transOrder.setParam2(param2);
        transOrder.setDevice(device);
        transOrder.setClientIp(clientIp);
        transOrder.setNotifyUrl(notifyUrl);
        //获取客户端IP
        String ip = WebUtils.getRemoteAddress(request);
        boolean isCreate = transOrderService.saveTransOrder(transOrder, sign, ip);
        if (isCreate) {
            //设置传输消息
            SimpleTransDTO simpleTransDTO = new SimpleTransDTO();
            simpleTransDTO.setChannelName(payChannel.getString("channelName"));
            simpleTransDTO.setTransOrderId(transOrder.getTransOrderId());
            //发送异步转账消息
            rabbitMq4TransServiceImpl.send(JSON.toJSONString(simpleTransDTO));
            return ResultBody.ok().data(transOrder);
        } else {
            return ResultBody.failed().msg("发起转账失败");
        }
    }

    @ApiOperation(value = "转账订单结果查询", notes = "转账订单结果查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transOrderId", value = "转账订单号", required = true, paramType = "form"),
    })
    @GetMapping(value = "/query")
    public ResultBody query(@RequestParam(value = "transOrderId") String transOrderId) {
        //查询转账订单结果
        QueryTransResultDTO queryTransResultDTO = transOrderService.queryTransOrderResult(transOrderId);
        //返回转账订单结果
        return ResultBody.ok().data(queryTransResultDTO);
    }
}