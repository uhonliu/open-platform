package com.bsd.payment.server.controller;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.model.entity.PayOrder;
import com.bsd.payment.server.service.IMchInfoService;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.service.IPayOrderService;
import com.bsd.payment.server.util.*;
import com.bsd.payment.server.util.wx.WxApi;
import com.bsd.payment.server.util.wx.WxApiClient;
import com.google.common.collect.Maps;
import com.opencloud.common.configuration.OpenCommonProperties;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付订单, 包括:统一下单,订单查询,补单等接口
 *
 * @author liujianhong
 */
@Api(tags = "支付订单")
@RestController
@RequestMapping("/order")
public class PayOrderController {
    private final MyLog _log = MyLog.getLog(PayOrderController.class);

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    private IMchInfoService mchInfoService;

    @Autowired
    private OpenCommonProperties openCommonProperties;

    @ApiOperation(value = "统一下单", notes = "统一下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", required = true, paramType = "form"),
            @ApiImplicitParam(name = "amount", value = "金额", required = true, paramType = "form"),
            @ApiImplicitParam(name = "subject", value = "订单标题", required = true, paramType = "form"),
            @ApiImplicitParam(name = "body", value = "商品描述", required = true, paramType = "form"),
            @ApiImplicitParam(name = "notifyUrl", value = "回调地址", paramType = "form"),
            @ApiImplicitParam(name = "returnUrl", value = "同步回调地址", paramType = "form"),
            @ApiImplicitParam(name = "param1", value = "扩展参数1", paramType = "form"),
            @ApiImplicitParam(name = "param2", value = "扩展参数2", paramType = "form"),
            @ApiImplicitParam(name = "openId", value = "微信openId,JSAPI支付必传字段", paramType = "form"),
            @ApiImplicitParam(name = "productId", value = "商品Id,微信NATIVE扫码支付必传字段", paramType = "form")
    })
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public ResultBody<PayOrder> pay(@RequestParam(value = "mchId") String mchId,
                                    @RequestParam(value = "channelCode") String channelCode,
                                    @RequestParam(value = "mchOrderNo") String mchOrderNo,
                                    @RequestParam(value = "amount") Long amount,
                                    @RequestParam(value = "subject") String subject,
                                    @RequestParam(value = "body") String body,
                                    @RequestParam(value = "notifyUrl", required = false) String notifyUrl,
                                    @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                    @RequestParam(value = "param1", required = false) String param1,
                                    @RequestParam(value = "param2", required = false) String param2,
                                    @RequestParam(value = "openId", required = false) String openId,
                                    @RequestParam(value = "productId", required = false) String productId,
                                    HttpServletRequest request) {
        MchInfo mchInfo = mchInfoService.findMchInfo(mchId);
        if (mchInfo == null) {
            throw new OpenAlertException("找不到商户信息");
        }
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            throw new OpenAlertException("找不到支付渠道信息");
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", mchOrderNo);           // 商户订单号
        paramMap.put("channelCode", channelCode);           // 支付渠道编码, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", amount);               // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", WebUtils.getRemoteAddress(request));        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", subject);
        paramMap.put("body", body);
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("returnUrl", returnUrl);         //同步回调地址
        paramMap.put("param1", param1);                         // 扩展参数1
        paramMap.put("param2", param2);                         // 扩展参数2

        if (PayConstant.CHANNEL_NAME_WX.equalsIgnoreCase(payChannel.getString("channelName"))) {
            JSONObject extraObject = new JSONObject();
            JSONObject paramObject = JSON.parseObject(payChannel.get("param").toString());
            if (StringUtils.isNotBlank(paramObject.getString("sceneInfo"))) {
                extraObject.put("sceneInfo", paramObject.getString("sceneInfo"));
            }
            if (StringUtils.isNotBlank(productId)) {
                extraObject.put("productId", productId);
            }
            if (StringUtils.isNotBlank(openId)) {
                extraObject.put("openId", openId);
            }
            paramMap.put("extra", extraObject.toJSONString());  // 附加参数
        }

        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getReqKey());
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String result = create(paramMap);
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if ("SUCCESS".equals(retMap.get("retCode").toString())) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, mchInfo.getResKey(), "sign", "payParams");
            String retSign = retMap.get("sign").toString();
            if (checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            } else {
                System.err.println("=========支付中心下单验签失败=========");
                throw new OpenAlertException("支付中心下单验签失败");
            }

            if (retMap.containsKey("payUrl") && PayConstant.CHANNEL_NAME_ALIPAY.equalsIgnoreCase(payChannel.getString("channelName"))) {
                retMap.put("payUrl", Base64.encode(retMap.get("payUrl").toString(), "UTF-8"));
            }
            return ResultBody.ok().data(retMap);
        }

        return ResultBody.failed().msg(retMap.get("retMsg").toString()).data(retMap);
    }

    /**
     * 统一下单接口:
     * 1)先验证接口参数以及签名信息
     * 2)验证通过创建支付订单
     * 3)根据商户选择渠道,调用支付服务进行下单
     * 4)返回下单数据
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "统一下单接口",
            notes = "     1)先验证接口参数以及签名信息\n" +
                    "     2)验证通过创建支付订单\n" +
                    "     3)根据商户选择渠道,调用支付服务进行下单\n" +
                    "     4)返回下单数据")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@RequestParam String params) {
        JSONObject po = JSONObject.parseObject(params);
        return create(po);
    }

    private String create(JSONObject params) {
        _log.info("###### 开始接收商户统一下单请求 ######");
        String logPrefix = "【商户统一下单】";
        try {
            JSONObject payContext = new JSONObject();
            JSONObject payOrder = null;
            // 验证参数有效性
            Object object = validateParams(params, payContext);
            if (object instanceof String) {
                _log.info("{}参数校验不通过:{}", logPrefix, object);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, object.toString(), null, null));
            }
            if (object instanceof JSONObject) {
                payOrder = (JSONObject) object;
            }
            if (payOrder == null) {
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心下单失败", null, null));
            }
            int result = payOrderService.createPayOrder(payOrder);
            _log.info("{}创建支付订单,结果:{}", logPrefix, result);
            if (result != 1) {
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "创建支付订单失败", null, null));
            }
            String channelCode = payOrder.getString("channelCode");
            switch (channelCode) {
                case PayConstant.PAY_CHANNEL_WX_APP:
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_APP, payOrder, payContext.getString("resKey"));
                case PayConstant.PAY_CHANNEL_WX_JSAPI:
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_JSAPI, payOrder, payContext.getString("resKey"));
                case PayConstant.PAY_CHANNEL_WX_NATIVE:
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_NATIVE, payOrder, payContext.getString("resKey"));
                case PayConstant.PAY_CHANNEL_WX_MWEB:
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_MWEB, payOrder, payContext.getString("resKey"));
                case PayConstant.PAY_CHANNEL_ALIPAY_MOBILE:
                case PayConstant.PAY_CHANNEL_ALIPAY_PC:
                case PayConstant.PAY_CHANNEL_ALIPAY_WAP:
                case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                    return payOrderService.doAliPayReq(channelCode, payOrder, payContext.getString("resKey"));
                default:
                    return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "不支持的支付渠道类型[channelCode=" + channelCode + "]", null, null));
            }
        } catch (Exception e) {
            _log.error(e, "");
            return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心系统异常", null, null));
        }
    }

    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param params
     * @return
     */
    private Object validateParams(JSONObject params, JSONObject payContext) {
        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        String mchId = params.getString("mchId");                // 商户ID
        String mchOrderNo = params.getString("mchOrderNo");    // 商户订单号
        String channelCode = params.getString("channelCode");        // 渠道编码
        String amount = params.getString("amount");            // 支付金额（单位分）
        String currency = params.getString("currency");         // 币种
        String clientIp = params.getString("clientIp");            // 客户端IP
        String device = params.getString("device");            // 设备
        String extra = params.getString("extra");                // 特定渠道发起时额外参数
        String param1 = params.getString("param1");            // 扩展参数1
        String param2 = params.getString("param2");            // 扩展参数2
        String notifyUrl = params.getString("notifyUrl");        // 支付结果回调URL
        String returnUrl = params.getString("returnUrl");
        String sign = params.getString("sign");                // 签名
        String subject = params.getString("subject");            // 商品主题
        String body = params.getString("body");                    // 商品描述信息
        // 验证请求参数有效性（必选项）
        if (StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(mchOrderNo)) {
            errorMessage = "request params[mchOrderNo] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(channelCode)) {
            errorMessage = "request params[channelCode] error.";
            return errorMessage;
        }
        if (!StringUtils.isNumeric(amount)) {
            errorMessage = "request params[amount] error.";
            return errorMessage;
        } else {
            boolean isAmount = AmountUtil.isAmount(amount);
            if (!isAmount) {
                errorMessage = "金额支付不正确.";
                return errorMessage;
            }
        }
        if (StringUtils.isBlank(currency)) {
            errorMessage = "request params[currency] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(notifyUrl)) {
            errorMessage = "request params[notifyUrl] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(subject)) {
            errorMessage = "request params[subject] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(body)) {
            errorMessage = "request params[body] error.";
            return errorMessage;
        }
        // 根据不同渠道,判断extra参数
        if (PayConstant.PAY_CHANNEL_WX_JSAPI.equalsIgnoreCase(channelCode)) {
            if (StringUtils.isEmpty(extra)) {
                errorMessage = "request params[extra] error.";
                return errorMessage;
            }
            JSONObject extraObject = JSON.parseObject(extra);
            String openId = extraObject.getString("openId");
            if (StringUtils.isBlank(openId)) {
                errorMessage = "request params[extra.openId] error.";
                return errorMessage;
            }
        } else if (PayConstant.PAY_CHANNEL_WX_NATIVE.equalsIgnoreCase(channelCode)) {
            if (StringUtils.isEmpty(extra)) {
                errorMessage = "request params[extra] error.";
                return errorMessage;
            }
            JSONObject extraObject = JSON.parseObject(extra);
            String productId = extraObject.getString("productId");
            if (StringUtils.isBlank(productId)) {
                errorMessage = "request params[extra.productId] error.";
                return errorMessage;
            }
        } else if (PayConstant.PAY_CHANNEL_WX_MWEB.equalsIgnoreCase(channelCode)) {
            if (StringUtils.isEmpty(extra)) {
                errorMessage = "request params[extra] error.";
                return errorMessage;
            }
            JSONObject extraObject = JSON.parseObject(extra);
            String sceneInfo = extraObject.getString("sceneInfo");
            if (StringUtils.isBlank(sceneInfo)) {
                errorMessage = "request params[extra.sceneInfo] error.";
                return errorMessage;
            }
            if (StringUtils.isBlank(clientIp)) {
                errorMessage = "request params[clientIp] error.";
                return errorMessage;
            }
        }

        // 签名信息
        if (StringUtils.isEmpty(sign)) {
            errorMessage = "request params[sign] error.";
            return errorMessage;
        }

        // 查询商户信息
        JSONObject mchInfo = mchInfoService.getByMchId(mchId);
        if (mchInfo == null) {
            errorMessage = "Can't found mchInfo[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (mchInfo.getByte("state") != 1) {
            errorMessage = "mchInfo not available [mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        String reqKey = mchInfo.getString("reqKey");
        if (StringUtils.isBlank(reqKey)) {
            errorMessage = "reqKey is null[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        payContext.put("resKey", mchInfo.getString("resKey"));

        // 查询商户对应的支付渠道
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            errorMessage = "Can't found payChannel[channelCode=" + channelCode + ",mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (payChannel.getByte("state") != 1) {
            errorMessage = "channel not available [channelCode=" + channelCode + ",mchId=" + mchId + "]";
            return errorMessage;
        }

        // 验证签名数据
        boolean verifyFlag = XXPayUtil.verifyPaySign(params, reqKey);
        if (!verifyFlag) {
            errorMessage = "Verify XX pay sign failed.";
            return errorMessage;
        }
        // 验证参数通过,返回JSONObject对象
        JSONObject payOrder = new JSONObject();
        payOrder.put("payOrderId", MySeq.getPay());
        payOrder.put("mchId", mchId);
        payOrder.put("mchOrderNo", mchOrderNo);
        payOrder.put("channelCode", channelCode);
        payOrder.put("amount", Long.parseLong(amount));
        payOrder.put("currency", currency);
        payOrder.put("clientIp", clientIp);
        payOrder.put("device", device);
        payOrder.put("subject", subject);
        payOrder.put("body", body);
        payOrder.put("extra", extra);
        payOrder.put("channelMchId", payChannel.getString("channelMchId"));
        payOrder.put("param1", param1);
        payOrder.put("param2", param2);
        payOrder.put("notifyUrl", notifyUrl);
        payOrder.put("returnUrl", returnUrl);
        return payOrder;
    }

    /**
     * 支付订单列表
     *
     * @return
     */
    @ApiOperation(value = "支付订单列表", notes = "点击支付订单进入列表页面")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payOrderId", value = "支付订单号", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "渠道编码", paramType = "form"),
            @ApiImplicitParam(name = "status", value = "支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成", paramType = "form"),
            @ApiImplicitParam(name = "channelMchId", value = "渠道商户ID", paramType = "form"),
            @ApiImplicitParam(name = "channelOrderNo", value = "渠道订单号", paramType = "form"),
            @ApiImplicitParam(name = "paySuccTimeStart", value = "订单支付成功时间开始", paramType = "form"),
            @ApiImplicitParam(name = "paySuccTimeTimeEnd", value = "订单支付成功时间截止", paramType = "form"),
            @ApiImplicitParam(name = "createTimeStart", value = "创建时间开始", paramType = "form"),
            @ApiImplicitParam(name = "createTimeEnd", value = "创建时间截止", paramType = "form"),
            @ApiImplicitParam(name = "pageIndex", value = "页数", paramType = "form"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", paramType = "form")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultBody<IPage<PayOrder>> getApiList(
            @RequestParam(value = "payOrderId", required = false) String payOrderId,
            @RequestParam(value = "mchId", required = false) String mchId,
            @RequestParam(value = "mchOrderNo", required = false) String mchOrderNo,
            @RequestParam(value = "channelCode", required = false) String channelCode,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "channelMchId", required = false) String channelMchId,
            @RequestParam(value = "channelOrderNo", required = false) String channelOrderNo,
            @RequestParam(value = "paySuccTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date paySuccTimeStart,
            @RequestParam(value = "paySuccTimeTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date paySuccTimeTimeEnd,
            @RequestParam(value = "createTimeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeStart,
            @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss") Date createTimeEnd,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        HashMap<String, Object> map = new HashMap<>();
        if (ObjectUtils.isNotEmpty(payOrderId)) {
            map.put("payOrderId", payOrderId);
        }
        if (ObjectUtils.isNotEmpty(mchId)) {
            map.put("mchId", mchId);
        }
        if (ObjectUtils.isNotEmpty(mchOrderNo)) {
            map.put("mchOrderNo", mchOrderNo);
        }
        if (ObjectUtils.isNotEmpty(channelCode)) {
            map.put("channelCode", channelCode);
        }
        if (ObjectUtils.isNotEmpty(status)) {
            map.put("status", status);
        }
        if (ObjectUtils.isNotEmpty(channelMchId)) {
            map.put("channelMchId", channelMchId);
        }
        if (ObjectUtils.isNotEmpty(channelOrderNo)) {
            map.put("channelOrderNo", channelOrderNo);
        }
        if (ObjectUtils.isNotEmpty(paySuccTimeStart)) {
            map.put("paySuccTimeStart", paySuccTimeStart);
        }
        if (ObjectUtils.isNotEmpty(paySuccTimeTimeEnd)) {
            map.put("paySuccTimeTimeEnd", paySuccTimeTimeEnd);
        }
        if (ObjectUtils.isNotEmpty(createTimeStart)) {
            map.put("createTimeStart", createTimeStart);
        }
        if (ObjectUtils.isNotEmpty(createTimeEnd)) {
            map.put("createTimeEnd", createTimeEnd);
        }
        map.put("page", pageIndex);
        map.put("limit", pageSize);

        return ResultBody.ok().data(payOrderService.findListPage(new PageParams(map)));
    }


    @ApiOperation(value = "支付订单详情", notes = "点击查看详情进入订单详情页面")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBody<PayOrder> detail(@RequestParam String payOrderId) {
        PayOrder payOrder = payOrderService.findPayOrder(payOrderId);
        if (payOrder == null) {
            return ResultBody.failed().msg("未查找到ID为" + payOrderId + "的订单信息");
        }
        return ResultBody.ok().data(payOrder);
    }

    @ApiOperation(value = "支付订单查询", notes = "查询支付订单接口:\n" +
            "     * 1)先验证接口参数以及签名信息\n" +
            "     * 2)根据参数查询订单\n" +
            "     * 3)返回订单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payOrderId", value = "支付订单号", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "mchOrderNo", value = "商户订单号", paramType = "form"),
            @ApiImplicitParam(name = "executeNotify", value = "是否执行回调", paramType = "form"),
            @ApiImplicitParam(name = "sign", value = "签名", paramType = "form"),
    })
    @GetMapping(value = "/query")
    public ResultBody query(@RequestParam(value = "payOrderId", required = true) String payOrderId,
                            @RequestParam(value = "mchId", required = true) String mchId,
                            @RequestParam(value = "mchOrderNo", required = true) String mchOrderNo,
                            @RequestParam(value = "executeNotify", required = false) String executeNotify,
                            @RequestParam(value = "sign", required = true) String sign) {
        _log.info("###### 开始接收商户查询支付订单请求 ######");
        HashMap<String, Object> payOrderMap = new HashMap<>();
        payOrderMap.put("payOrderId", payOrderId);
        payOrderMap.put("mchId", mchId);
        payOrderMap.put("mchOrderNo", mchOrderNo);
        payOrderMap.put("executeNotify", executeNotify);
        payOrderMap.put("sign", sign);
        String logPrefix = "【商户支付订单查询】";
        try {
            JSONObject payContext = new JSONObject();
            // 验证参数有效性
            String errorMessage = validateQueryParams(payOrderMap, payContext);
            if (!"success".equalsIgnoreCase(errorMessage)) {
                _log.warn(errorMessage);
                return ResultBody.failed().msg(errorMessage);
            }
            _log.debug("请求参数及签名校验通过");
            JSONObject payOrder = payOrderService.queryPayOrder(mchId, payOrderId, mchOrderNo, executeNotify);
            _log.info("{}查询支付订单,结果:{}", logPrefix, payOrder);
            if (payOrder == null) {
                return ResultBody.failed().msg("支付订单不存在");
            }
            _log.info("###### 商户查询订单处理完成 ######");
            return ResultBody.ok().data(payOrder);
        } catch (Exception e) {
            _log.error(e, "");
            return ResultBody.failed().msg("支付中心系统异常");
        }
    }

    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param map
     * @return
     */
    private String validateQueryParams(Map map, JSONObject payContext) {
        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        String mchId = (String) map.get("mchId");// 商户ID
        String mchOrderNo = (String) map.get("mchOrderNo");    // 商户订单号
        String payOrderId = (String) map.get("payOrderId");    // 支付订单号
        // String sign = (String) map.get("sign");                // 签名

        // 验证请求参数有效性（必选项）
        if (StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(mchOrderNo) && StringUtils.isBlank(payOrderId)) {
            errorMessage = "request params[mchOrderNo or payOrderId] error.";
            return errorMessage;
        }

        // 签名信息
        /*if (StringUtils.isEmpty(sign)) {
            errorMessage = "request params[sign] error.";
            return errorMessage;
        }*/

        // 查询商户信息
        JSONObject mchInfo = mchInfoService.getByMchId(mchId);
        if (mchInfo == null) {
            errorMessage = "Can't found mchInfo[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (mchInfo.getByte("state") != 1) {
            errorMessage = "mchInfo not available [mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        String reqKey = mchInfo.getString("reqKey");
        if (StringUtils.isBlank(reqKey)) {
            errorMessage = "reqKey is null[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        payContext.put("resKey", mchInfo.getString("resKey"));

        // 验证签名数据
        /*boolean verifyFlag = XXPayUtil.verifyPaySign(map, reqKey);
        if (!verifyFlag) {
            errorMessage = "Verify XX pay sign failed.";
            return errorMessage;
        }*/

        return "success";
    }

    @ApiOperation(value = "支付订单过期处理", notes = "查出订单创建时间是否超过24h，若超过则置为过期,前台不需要传参数")
    @RequestMapping(value = "/expire", method = RequestMethod.GET)
    public ResultBody<PayOrder> expire() {
        int count = payOrderService.updateStatus4Expired(1800);

        return ResultBody.ok().data("本次有" + count + "条支付订单未支付已置为过期!");
    }

    @ApiOperation(value = "掉单支付结果同步", notes = "查出订单创建时间是否超过30分钟，若超过则主动查询结果,前台不需要传参数")
    @RequestMapping(value = "/synPayResult", method = RequestMethod.GET)
    public ResultBody<PayOrder> synPayResult() {
        int count = payOrderService.synPayResult(1800);

        return ResultBody.ok().data("本次有" + count + "条支付订单状态已更新!");
    }

    /**
     * 获取微信授权openid
     *
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "redirectUrl", value = "返回页面URL", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "code", value = "微信授权码", paramType = "form")
    })
    @ApiOperation(value = "获取微信授权openid", notes = "获取微信授权openid")
    @RequestMapping(value = "/getOpenId", method = RequestMethod.GET)
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String mchId = request.getParameter("mchId");
        String channelCode = request.getParameter("channelCode");

        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            _log.error("获取支付渠道信息失败");
            throw new OpenAlertException("获取支付渠道信息失败");
        }
        JSONObject param = payChannel.getJSONObject("param");
        if (param.isEmpty() || param.get("appId") == null || param.get("secret") == null) {
            _log.error("支付渠道配置信息不正确");
            throw new OpenAlertException("支付渠道配置信息不正确");
        }

        String openId = "";
        //如果request中包括code，则是微信回调
        if (!StringUtils.isBlank(code)) {
            try {
                openId = WxApiClient.getOAuthOpenId(param.get("appId").toString(), param.get("secret").toString(), code);
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if (redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            } else {
                redirectUrl += "?openId=" + openId;
            }
            response.sendRedirect(redirectUrl);
        } else {
            //oauth获取code
            String redirectUrl4Vx = openCommonProperties.getApiServerAddr() + "/pay/order/getOpenId?redirectUrl=" + redirectUrl;
            String state = OAuth2RequestParamHelper.prepareState(request);
            String url = WxApi.getOAuthCodeUrl(param.get("appId").toString(), redirectUrl4Vx, "snsapi_base", state);
            _log.info("跳转URL={}", url);
            response.sendRedirect(url);
        }
    }

    /**
     * 获取微信授权openid
     *
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "redirectUrl", value = "返回页面URL", required = true, paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", required = true, paramType = "form"),
            @ApiImplicitParam(name = "code", value = "微信授权码", paramType = "form")
    })
    @ApiOperation(value = "获取微信授权openid", notes = "获取微信授权openid")
    @RequestMapping(value = "/getOpenId2", method = RequestMethod.POST)
    public ResultBody getOpenId2(HttpServletRequest request) {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String mchId = request.getParameter("mchId");
        String channelCode = request.getParameter("channelCode");

        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            _log.error("获取支付渠道信息失败");
            throw new OpenAlertException("获取支付渠道信息失败");
        }
        JSONObject param = payChannel.getJSONObject("param");
        if (param.isEmpty() || param.get("appId") == null || param.get("secret") == null) {
            _log.error("支付渠道配置信息不正确");
            throw new OpenAlertException("支付渠道配置信息不正确");
        }

        String openId = "";
        Map map = Maps.newHashMap();
        //如果request中包括code，则是微信回调
        if (!StringUtils.isBlank(code)) {
            try {
                openId = WxApiClient.getOAuthOpenId(param.get("appId").toString(), param.get("secret").toString(), code);
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if (redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            } else {
                redirectUrl += "?openId=" + openId;
            }
            map.put("openId", openId);
            map.put("redirectUrl", redirectUrl);
        } else {
            //oauth获取code
            String state = OAuth2RequestParamHelper.prepareState(request);
            String url = WxApi.getOAuthCodeUrl(param.get("appId").toString(), redirectUrl, "snsapi_base", state);
            _log.info("跳转URL={}", url);
            map.put("redirectUrl", url);
        }
        return ResultBody.ok().data(map);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        //转换日期 注意这里的转化要和传进来的字符串的格式一直 如2015-9-9 就应该为yyyy-MM-dd
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // CustomDateEditor为自定义日期编辑器
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
