package com.bsd.payment.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.RefundOrder;
import com.bsd.payment.server.service.*;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.PayDigestUtil;
import com.bsd.payment.server.util.RpcUtil;
import com.bsd.payment.server.util.XXPayUtil;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujianhong
 */
@Api(tags = "退款订单")
@RestController
@RequestMapping("/refund_order")
public class RefundOrderController {
    private final static MyLog _log = MyLog.getLog(RefundOrderController.class);

    @Autowired
    private IRefundOrderService refundOrderService;

    @Autowired
    private IMchInfoService mchInfoService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    private IPayChannel4AliService payChannel4AliService;

    @Autowired
    private IPayChannel4WxService payChannel4WxService;

    /**
     * 退款订单列表
     *
     * @return ResultBody
     */
    @ApiOperation(value = "退款订单列表", notes = "点击退款订单进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundOrderId", value = "退款订单号", paramType = "form"),
            @ApiImplicitParam(name = "payOrderId", value = "支付订单号", paramType = "form"),
            @ApiImplicitParam(name = "channelPayOrderNo", value = "渠道支付单号", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchRefundNo", value = "商户退款单号", paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成", paramType = "form"),
            @ApiImplicitParam(name = "channelUser", value = "渠道用户标识,如微信openId,支付宝账号", paramType = "form"),
            @ApiImplicitParam(name = "userName", value = "用户姓名", paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", paramType = "form"),
            @ApiImplicitParam(name = "channelOrderNo", value = "渠道订单号", paramType = "form"),
            @ApiImplicitParam(name = "refundSuccTimeStart", value = "订单退款成功时间开始", paramType = "form"),
            @ApiImplicitParam(name = "refundSuccTimeEnd", value = "订单退款成功时间截止", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<RefundOrder>> list(@RequestParam(value = "refundOrderId", required = false) String refundOrderId,
                                               @RequestParam(value = "payOrderId", required = false) String payOrderId,
                                               @RequestParam(value = "channelPayOrderNo", required = false) String channelPayOrderNo,
                                               @RequestParam(value = "mchId", required = false) String mchId,
                                               @RequestParam(value = "mchRefundNo", required = false) String mchRefundNo,
                                               @RequestParam(value = "channelCode", required = false) String channelCode,
                                               @RequestParam(value = "status", required = false) String status,
                                               @RequestParam(value = "channelUser", required = false) String channelUser,
                                               @RequestParam(value = "userName", required = false) String userName,
                                               @RequestParam(value = "channelMchId", required = false) String channelMchId,
                                               @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
                                               @RequestParam(value = "refundSuccTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date refundSuccTimeStart,
                                               @RequestParam(value = "refundSuccTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date refundSuccTimeEnd,
                                               @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeStart,
                                               @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeEnd,
                                               @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(refundOrderId)) {
            map.put("refundOrderId", refundOrderId);
        }
        if (ObjectUtils.isNotEmpty(payOrderId)) {
            map.put("payOrderId", payOrderId);
        }
        if (ObjectUtils.isNotEmpty(channelPayOrderNo)) {
            map.put("channelPayOrderNo", channelPayOrderNo);
        }
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(mchRefundNo)) {
            map.put("mchRefundNo", mchRefundNo);
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
        if (ObjectUtils.isNotEmpty(refundSuccTimeStart)) {
            map.put("refundSuccTimeStart", refundSuccTimeStart);
        }
        if (ObjectUtils.isNotEmpty(refundSuccTimeEnd)) {
            map.put("refundSuccTimeEnd", refundSuccTimeEnd);
        }
        if (ObjectUtils.isNotEmpty(createTimeStart)) {
            map.put("createTimeStart", createTimeStart);
        }
        if (ObjectUtils.isNotEmpty(createTimeEnd)) {
            map.put("createTimeEnd", createTimeEnd);
        }
        map.put("page", pageIndex);
        map.put("limit", pageSize);

        return ResultBody.ok().data(refundOrderService.findListPage(new PageParams(map)));
    }

    @ApiOperation(value = "退款订单详情", notes = "点击查看详情进入详情页面")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<RefundOrder> detail(@RequestParam String refundOrderId) {
        RefundOrder refundOrder = refundOrderService.findRefundOrder(refundOrderId);
        if (refundOrder == null) {
            return ResultBody.failed().msg("未查找到ID为" + refundOrderId + "的退款订单信息");
        }
        return ResultBody.ok().data(refundOrder);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // CustomDateEditor为自定义日期编辑器
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ApiOperation(value = "发起退款", notes = "发起退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "amount", value = "金额", required = true, paramType = "form"),
            @ApiImplicitParam(name = "subject", value = "订单标题", required = true, paramType = "form"),
            @ApiImplicitParam(name = "body", value = "商品描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "notifyUrl", value = "回调地址", paramType = "form"),
            @ApiImplicitParam(name = "payOrderId", value = "支付订单号", paramType = "form"),
            @ApiImplicitParam(name = "channelUser", value = "渠道用户标识,如微信openId,支付宝账号", paramType = "form"),
            @ApiImplicitParam(name = "param1", value = "扩展参数1", paramType = "form"),
            @ApiImplicitParam(name = "param2", value = "扩展参数2", paramType = "form")
    })
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public ResultBody<RefundOrder> pay(@RequestParam(value = "mchId") String mchId,
                                       @RequestParam(value = "channelCode") String channelCode,
                                       @RequestParam(value = "mchOrderNo") String mchOrderNo,
                                       @RequestParam(value = "amount") Long amount,
                                       @RequestParam(value = "subject") String subject,
                                       @RequestParam(value = "body") String body,
                                       @RequestParam(value = "notifyUrl", required = false) String notifyUrl,
                                       @RequestParam(value = "param1", required = false) String param1,
                                       @RequestParam(value = "param2", required = false) String param2,
                                       @RequestParam(value = "payOrderId") String payOrderId,
                                       @RequestParam(value = "channelUser") String channelUser,
                                       HttpServletRequest request) {
        MchInfo mchInfo = mchInfoService.findMchInfo(mchId);
        if (mchInfo == null) {
            throw new OpenAlertException("找不到商户的信息");
        }
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            throw new OpenAlertException("找不到 支付渠道信息");
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", mchOrderNo);           // 商户订单号
        paramMap.put("mchRefundNo", "REFUND" + System.currentTimeMillis()); //退款订单号
        paramMap.put("channelCode", channelCode);           // 支付渠道编码, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", amount);               // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", WebUtils.getRemoteAddress(request));        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", subject);
        paramMap.put("body", body);
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("param1", param1);                         // 扩展参数1
        paramMap.put("param2", param2);                         // 扩展参数2
        paramMap.put("channelUser", channelUser);  // 微信openId,支付宝账号
        paramMap.put("payOrderId", payOrderId);
        paramMap.put("channelName", payChannel.getString("channelName"));

        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getReqKey());
        // 签名
        paramMap.put("sign", reqSign);
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心退款接口,请求数据:" + reqData);
        String result = refundOrderService.createRefundOrder(paramMap);
        System.out.println("请求支付中心退款接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if ("SUCCESS".equals(retMap.get("retCode").toString())) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, mchInfo.getResKey(), "sign", "payParams");
            String retSign = retMap.get("sign").toString();
            if (checkSign.equals(retSign)) {
                System.out.println("=========支付中心--下单验签成功=========");
            } else {
                System.err.println("=========支付中心--下单验签失败=========");
                throw new OpenAlertException("支付中心下单验签失败");
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("refundOrderId", (retMap.get("refundOrderId").toString()));
            jsonObject.put("channelName", (retMap.get("channelName").toString()));
            return ResultBody.ok().msg("退款处理中").data(jsonObject);
        }
        return ResultBody.failed().msg(retMap.get("retMsg").toString());
    }

    @ApiOperation(value = "查询退款", notes = "查询退款是否已经成功")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "refundOrderId", value = "商户退款单号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "payOrderId", value = "支付账单号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelPayOrderNo", value = "渠道支付单号", paramType = "form")
    })
    @RequestMapping(value = "/getRefund", method = RequestMethod.GET)
    public ResultBody<RefundOrder> getRefund(@RequestParam(value = "mchId") String mchId,
                                             @RequestParam(value = "refundOrderId") String refundOrderId,
                                             @RequestParam(value = "channelCode") String channelCode,
                                             @RequestParam(value = "payOrderId") String payOrderId,
                                             @RequestParam(value = "channelPayOrderNo", required = false, defaultValue = "") String channelPayOrderNo) {
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        //创建退款订单
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", mchId);
        paramMap.put("refundOrderId", refundOrderId);
        paramMap.put("channelCode", channelCode);
        paramMap.put("payOrderId", payOrderId);
        paramMap.put("channelPayOrderNo", channelPayOrderNo);
        paramMap.put("channelName", payChannel.getString("channelName"));
        jsonObject.put("refundOrder", paramMap);
        try {
            JSONObject payContext = new JSONObject();
            // 验证参数有效性
            String errorMessage = refundOrderService.validateQueryParams(paramMap, payContext);
            if (!"success".equalsIgnoreCase(errorMessage)) {
                _log.warn(errorMessage);
                return ResultBody.failed().msg(errorMessage);
            }
            String jsonParam = RpcUtil.createBaseParam(jsonObject);
            //封装返回结果
            Map<String, Object> resultMap = new HashMap<>();
            String channelName = paramMap.get("channelName").toString();
            if (PayConstant.CHANNEL_NAME_WX.equalsIgnoreCase(channelName)) {
                resultMap = payChannel4WxService.getWxRefundReq(jsonParam);
            } else if (PayConstant.CHANNEL_NAME_ALIPAY.equalsIgnoreCase(channelName)) {
                resultMap = payChannel4AliService.getAliRefundReq(jsonParam);
            } else {
                _log.warn("不支持的退款渠道,停止退款处理.refundOrderId={},channelName={}", refundOrderId, channelName);
                return ResultBody.failed().msg("不支持的退款渠道,停止退款处理!");
            }
            if (resultMap == null) {
                return ResultBody.failed().msg("支付订单不存在！");
            }
            Map<String, Object> map = XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_SUCCESS, "", PayConstant.RETURN_VALUE_SUCCESS, null);
            map.put("result", resultMap);
            return ResultBody.ok().data(map);
        } catch (Exception e) {
            _log.error(e, "");
            return ResultBody.failed().msg("支付中心系统异常");
        }
    }
}