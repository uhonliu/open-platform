package com.bsd.payment.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.entity.GoodsOrder;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.service.GoodsOrderService;
import com.bsd.payment.server.service.IMchInfoService;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.util.*;
import com.bsd.payment.server.util.wx.WxApi;
import com.bsd.payment.server.util.wx.WxApiClient;
import com.google.common.collect.Lists;
import com.opencloud.common.utils.StringHelper;
import com.opencloud.common.utils.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujianhong
 */
@RestController
@RequestMapping("/goods")
@Api(tags = "下单测试")
public class GoodsOrderController {
    private final static MyLog _log = MyLog.getLog(GoodsOrderController.class);

    @Autowired
    private GoodsOrderService goodsOrderService;

    @Autowired
    private IMchInfoService mchInfoService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    RestTemplate restTemplate;

    static final String baseUrl = "http://bsd.vaiwan.com";
    static final String notifyUrl = "http://bsd.vaiwan.com/pay/goods/payNotify";
    private final static String QR_PAY_URL = "http://bsd.vaiwan.com/pay/goods/qrPay";
    private final static String GetOpenIdURL = "http://bsd.vaiwan.com/pay/goods/getOpenId";
    private final static String GetOpenIdURL2 = "http://bsd.vaiwan.com/pay/goods/getOpenId2";

    @ApiOperation(value = "模拟下单", notes = "创建商品订单")
    @RequestMapping(value = "/buy/{goodsId}", method = RequestMethod.GET)
    public String buy(@PathVariable("goodsId") String goodsId) {
        if (!"G_0001".equals(goodsId)) {
            return "fail";
        }
        String goodsOrderId = StringHelper.getSeq("G");
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("XXPAY捐助商品" + goodsId);
        goodsOrder.setAmount(1L);
        goodsOrder.setUserId("xxpay_000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        goodsOrder.setCreateTime(new Date());
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        if (result == 1) {
            return goodsOrderId;
        }
        return "fail";
    }

    @ApiOperation(value = "统一下单", notes = "1分钱支付测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsOrderId", value = "商品订单ID", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", paramType = "form")
    })
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(@RequestParam(value = "goodsOrderId") String goodsOrderId,
                      @RequestParam(value = "mchId", required = false, defaultValue = "10000002") String mchId,
                      @RequestParam(value = "channelCode", required = false, defaultValue = "ALIPAY_WAP") String channelCode,
                      HttpServletRequest request) {
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(goodsOrderId);
        if (goodsOrder == null) {
            return "fail";
        }
        int status = goodsOrder.getStatus();
        if (status != Constant.GOODS_ORDER_STATUS_INIT) {
            return "fail_001";
        }
        MchInfo mchInfo = mchInfoService.findMchInfo(mchId);
        if (mchInfo == null) {
            return "fail";
        }
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            return "fail";
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrderId);           // 商户订单号
        paramMap.put("channelCode", channelCode);             // 支付渠道编码, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", WebUtils.getRemoteAddress(request));        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2
        paramMap.put("extra", payChannel.get("param").toString());  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getReqKey());
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = baseUrl + "/pay/order/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(paramMap.toJSONString(), headers);
        String result = restTemplate.postForEntity(url, entity, String.class).getBody();
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if ("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, mchInfo.getResKey(), "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if (checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            } else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        String payOrderId = retMap.get("payOrderId").toString();

        goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setPayOrderId(payOrderId);
        goodsOrder.setChannelCode(channelCode);
        int ret = goodsOrderService.update(goodsOrder);
        _log.info("修改商品订单,返回:{}", ret);
        if (ret == 1) {
            return retMap.get("payUrl").toString();
        }
        return "fail";
    }

    private Map createPayOrder(GoodsOrder goodsOrder, Map<String, Object> params) {
        if (!params.containsKey("mchId")) {
            return null;
        }

        String mchId = params.get("mchId").toString();
        MchInfo mchInfo = mchInfoService.findMchInfo(mchId);
        if (mchInfo == null) {
            return null;
        }

        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrder.getGoodsOrderId());           // 商户订单号
        paramMap.put("channelCode", params.get("channelCode"));             // 支付渠道编码, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", "114.112.124.236");        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2

        JSONObject extra = new JSONObject();
        extra.put("openId", params.get("openId"));
        paramMap.put("extra", extra.toJSONString());  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getReqKey());
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        System.out.println("请求支付中心下单接口,请求数据:" + reqData);
        String url = baseUrl + "/pay/order/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(paramMap.toJSONString(), headers);
        String result = restTemplate.postForEntity(url, entity, String.class).getBody();
        System.out.println("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if ("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, mchInfo.getResKey(), "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if (checkSign.equals(retSign)) {
                System.out.println("=========支付中心下单验签成功=========");
            } else {
                System.err.println("=========支付中心下单验签失败=========");
                return null;
            }
        }
        return retMap;
    }

    @ApiOperation(value = "扫码支付", notes = "1分钱支付测试")
    @RequestMapping(value = "/qrPay", method = RequestMethod.POST)
    public String qrPay(ModelMap model, HttpServletRequest request, Long amount) {
        String logPrefix = "【二维码扫码支付】";
        String view = "qrPay";
        _log.info("====== 开始接收二维码扫码支付请求 ======");
        String ua = request.getHeader("User-Agent");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},ua={}", logPrefix, goodsId, amount, ua);
        String client = "alipay";
        String channelCode = "ALIPAY_WAP";
        if (StringUtils.isBlank(ua)) {
            String errorMessage = "User-Agent为空！";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        } else {
            if (ua.contains("Alipay")) {
                client = "alipay";
                channelCode = "ALIPAY_WAP";
            } else if (ua.contains("MicroMessenger")) {
                client = "wx";
                channelCode = "WX_JSAPI";
            }
        }
        if (client == null) {
            String errorMessage = "请用微信或支付宝扫码";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            return view;
        }
        // 先插入订单数据
        GoodsOrder goodsOrder = null;
        Map<String, String> orderMap = null;
        if ("alipay".equals(client)) {
            _log.info("{}{}扫码下单", logPrefix, "支付宝");
            Map params = new HashMap<>();
            params.put("mchId", "10000002");
            params.put("channelCode", channelCode);
            // 下单
            goodsOrder = createGoodsOrder(goodsId, amount);
            orderMap = createPayOrder(goodsOrder, params);
        } else if ("wx".equals(client)) {
            _log.info("{}{}扫码", logPrefix, "微信");
            // 判断是否拿到openid，如果没有则去获取
            String openId = request.getParameter("openId");
            if (StringUtils.isNotBlank(openId)) {
                _log.info("{}openId：{}", logPrefix, openId);
                Map params = new HashMap<>();
                params.put("channelCode", channelCode);
                params.put("openId", openId);
                goodsOrder = createGoodsOrder(goodsId, amount);
                // 下单
                orderMap = createPayOrder(goodsOrder, params);
            } else {
                String redirectUrl = QR_PAY_URL + "?amount=" + amount;
                String url = GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
                _log.info("跳转URL={}", url);
                return "redirect:" + url;
            }
        }
        model.put("goodsOrder", goodsOrder);
        model.put("amount", AmountUtil.convertCent2Dollar(goodsOrder.getAmount() + ""));
        if (orderMap != null) {
            model.put("orderMap", orderMap);
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelCode(channelCode);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        model.put("client", client);
        return view;
    }

    GoodsOrder createGoodsOrder(String goodsId, Long amount) {
        // 先插入订单数据
        String goodsOrderId = StringHelper.getSeq("G");
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("XXPAY捐助商品" + goodsId);
        goodsOrder.setAmount(amount);
        goodsOrder.setUserId("xxpay_000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        goodsOrder.setCreateTime(new Date());
        goodsOrder.setPayOrderId("111111");
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return goodsOrder;
    }

    /**
     * 获取code
     *
     * @return
     */
    @RequestMapping(value = "/getOpenId", method = RequestMethod.POST)
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String mchId = request.getParameter("mchId");
        String channelCode = request.getParameter("channelCode");

        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            _log.error("获取支付渠道信息失败");
        }
        JSONObject param = payChannel.getJSONObject("param");

        String openId = "";
        if (!StringUtils.isBlank(code)) {//如果request中包括code，则是微信回调
            try {
                openId = WxApiClient.getOAuthOpenId(param.get("appid").toString(), param.get("private_key").toString(), code);
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
        } else {//oauth获取code
            String redirectUrl4Vx = GetOpenIdURL + "?redirectUrl=" + redirectUrl;
            String state = OAuth2RequestParamHelper.prepareState(request);
            String url = WxApi.getOAuthCodeUrl(param.get("appid").toString(), redirectUrl4Vx, "snsapi_base", state);
            _log.info("跳转URL={}", url);
            response.sendRedirect(url);
        }
    }

    /**
     * 获取code
     *
     * @return
     */
    @RequestMapping(value = "/getOpenId2", method = RequestMethod.POST)
    public void getOpenId2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("进入获取用户openID页面");
        String redirectUrl = request.getParameter("redirectUrl");
        String code = request.getParameter("code");
        String mchId = request.getParameter("mchId");
        String channelCode = request.getParameter("channelCode");

        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(mchId, channelCode);
        if (payChannel == null) {
            _log.error("获取支付渠道信息失败");
        }
        JSONObject param = payChannel.getJSONObject("param");

        String openId = "";
        if (!StringUtils.isBlank(code)) {//如果request中包括code，则是微信回调
            try {
                openId = WxApiClient.getOAuthOpenId(param.get("appid").toString(), param.get("private_key").toString(), code);
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
        } else {//oauth获取code
            //http://www.abc.com/xxx/get-weixin-code.html?appid=XXXX&scope=snsapi_base&state=hello-world&redirect_uri=http%3A%2F%2Fwww.xyz.com%2Fhello-world.html
            String redirectUrl4Vx = GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
            String url = String.format("http://www.xiaoshuding.com/get-weixin-code.html?appid=%s&scope=snsapi_base&state=hello-world&redirect_uri=%s", param.get("appid").toString(), WxApi.urlEnodeUTF8(redirectUrl4Vx));
            _log.info("跳转URL={}", url);
            response.sendRedirect(url);
        }
    }

    /**
     * 接收支付中心通知
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/payNotify", method = RequestMethod.POST)
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理支付中心通知 ======");
        Map<String, Object> paramMap = request2payResponseMap(request, new String[]{
                "payOrderId", "mchId", "mchOrderNo", "channelCode", "amount", "currency", "status", "clientIp",
                "device", "subject", "channelOrderNo", "param1",
                "param2", "paySuccTime", "backType", "sign"
        });
        _log.info("支付中心通知请求参数,paramMap={}", paramMap);
        if (!verifyPayResponse(paramMap)) {
            String errorMessage = "verify request param failed.";
            _log.warn(errorMessage);
            outResult(response, "fail");
            return;
        }
        String payOrderId = (String) paramMap.get("payOrderId");
        String mchOrderNo = (String) paramMap.get("mchOrderNo");
        String resStr;
        try {
            GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
            if (goodsOrder != null && Constant.GOODS_ORDER_STATUS_COMPLETE.equals(goodsOrder.getStatus())) {
                outResult(response, "success");
                return;
            }
            // 执行业务逻辑
            int ret = goodsOrderService.updateStatus4Success(mchOrderNo);
            // ret返回结果
            // 等于1表示处理成功,返回支付中心success
            // 其他值,返回支付中心fail,让稍后再通知
            if (ret == 1) {
                ret = goodsOrderService.updateStatus4Complete(mchOrderNo);
                if (ret == 1) {
                    resStr = "success";
                } else {
                    resStr = "fail";
                }
            } else {
                resStr = "fail";
            }
        } catch (Exception e) {
            resStr = "fail";
            _log.error(e, "执行业务异常,payOrderId=%s.mchOrderNo=%s", payOrderId, mchOrderNo);
        }
        _log.info("响应支付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        outResult(response, resStr);
        _log.info("====== 支付中心通知处理完成 ======");
    }

    @RequestMapping(value = "/toAliPay", method = RequestMethod.POST)
    @ApiOperation(value = "支付宝测试", notes = "支付宝测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额", paramType = "form"),
            @ApiImplicitParam(name = "mchId", value = "商户ID", paramType = "form"),
            @ApiImplicitParam(name = "channelCode", value = "支付渠道编码", paramType = "form")
    })
    public String toAliPay(HttpServletRequest request,
                           @RequestParam(value = "amount") Long amount,
                           @RequestParam(value = "mchId", required = false, defaultValue = "10000002") String mchId,
                           @RequestParam(value = "channelCode", required = false, defaultValue = "ALIPAY_WAP") String channelCode) {
        String logPrefix = "【支付宝支付】";
        _log.info("====== 开始接收支付宝支付请求 ======");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},channelCode={}", logPrefix, goodsId, amount, channelCode);
        // 先插入订单数据
        Map params = new HashMap<>();
        params.put("mchId", mchId);
        params.put("channelCode", channelCode);
        // 下单
        GoodsOrder goodsOrder = createGoodsOrder(goodsId, amount);
        Map<String, String> orderMap = createPayOrder(goodsOrder, params);
        if (orderMap != null && "success".equalsIgnoreCase(orderMap.get("resCode"))) {
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelCode(channelCode);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        if (PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equalsIgnoreCase(channelCode)) {
            return orderMap.get("payParams");
        }
        return orderMap.get("payUrl");
    }

    void outResult(HttpServletResponse response, String content) {
        response.setContentType("text/html");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(content);
            pw.flush();
            pw.close();
            _log.info("response xxpay complete.");
        } catch (IOException e) {
            _log.error(e, "response xxpay write exception.");
        }
    }

    public Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
        Map<String, Object> responseMap = new HashMap<>();
        for (int i = 0; i < paramArray.length; i++) {
            String key = paramArray[i];
            String v = request.getParameter(key);
            if (v != null) {
                responseMap.put(key, v);
            }
        }
        return responseMap;
    }

    public boolean verifyPayResponse(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        String payOrderId = (String) map.get("payOrderId");
        String mchOrderNo = (String) map.get("mchOrderNo");
        String amount = (String) map.get("amount");
        String sign = (String) map.get("sign");

        if (StringUtils.isEmpty(mchId)) {
            _log.warn("Params error. mchId={}", mchId);
            return false;
        }
        if (StringUtils.isEmpty(payOrderId)) {
            _log.warn("Params error. payOrderId={}", payOrderId);
            return false;
        }
        if (StringUtils.isEmpty(amount) || !NumberUtils.isDigits(amount)) {
            _log.warn("Params error. amount={}", amount);
            return false;
        }
        if (StringUtils.isEmpty(sign)) {
            _log.warn("Params error. sign={}", sign);
            return false;
        }

        // 验证签名
        if (!verifySign(map)) {
            _log.warn("verify params sign failed. payOrderId={}", payOrderId);
            return false;
        }

        // 根据payOrderId查询业务订单,验证订单是否存在
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
        if (goodsOrder == null) {
            _log.warn("业务订单不存在,payOrderId={},mchOrderNo={}", payOrderId, mchOrderNo);
            return false;
        }
        // 核对金额
        if (goodsOrder.getAmount() != Long.parseLong(amount)) {
            _log.warn("支付金额不一致,dbPayPrice={},payPrice={}", goodsOrder.getAmount(), amount);
            return false;
        }
        return true;
    }

    public boolean verifySign(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        MchInfo mchInfo = mchInfoService.findMchInfo(mchId);
        if (mchInfo == null) {
            return false;
        }
        String localSign = PayDigestUtil.getSign(map, mchInfo.getResKey(), "sign");
        String sign = (String) map.get("sign");
        return localSign.equalsIgnoreCase(sign);
    }
}