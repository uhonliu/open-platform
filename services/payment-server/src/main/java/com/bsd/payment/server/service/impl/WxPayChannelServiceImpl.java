package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.payment.server.configuration.channel.wechat.WxPayProperties;
import com.bsd.payment.server.configuration.channel.wechat.WxPayUtil;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.IPayService;
import com.bsd.payment.server.util.DateUtil;
import com.bsd.payment.server.util.StrUtil;
import com.github.binarywang.wxpay.bean.entpay.EntPayQueryResult;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.EntPayService;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @Author: linrongxin
 * @Date: 2019/8/30 16:15
 */
@Service("wechatChannelServiceImpl")
public class WxPayChannelServiceImpl implements IPayService {
    @Resource
    private WxPayProperties wxPayProperties;

    /**
     * 微信企业付款到零钱
     * 参考地址:https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=14_2
     *
     * @param transOrder
     */
    @Override
    public ResultBody<TransResultDTO> doTransReq(TransOrder transOrder, String configStr) {
        TransResultDTO transResultDTO = new TransResultDTO();
        //获取微信配置信息
        WxPayConfig wxPayConfig = WxPayUtil.getWxPayConfig(configStr, "", wxPayProperties.getCertRootPath(), wxPayProperties.getNotifyUrl());
        //获取微信支付服务
        WxPayService wxPayService = new WxPayServiceImpl();
        //设置配置信息
        wxPayService.setConfig(wxPayConfig);
        //获取微信转账服务
        EntPayService entPayService = wxPayService.getEntPayService();
        //创建转账请求实体
        EntPayRequest entPayRequest = buildEntPayRequest(transOrder, wxPayConfig);
        try {
            //调用转账服务
            EntPayResult result = entPayService.entPay(entPayRequest);
            //状态码
            String returnCode = result.getReturnCode();
            //业务码
            String resultCode = result.getResultCode();
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                //转账成功
                transResultDTO.setChannelOrderNo(result.getPaymentNo());//微信付款单号
                transResultDTO.setTransSuccTime(DateUtil.getTextDate(result.getPaymentTime(), "yyyy-MM-dd HH:mm:ss"));
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_SUCCESS);
                return ResultBody.ok().data(transResultDTO);
            } else if ("FAIL".equals(resultCode)) {
                transResultDTO.setChannelErrCode(result.getErrCode());
                transResultDTO.setChannelErrMsg(result.getErrCodeDes());
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
                if ("SYSTEMERROR".equals(result.getErrCode())) {
                    //状态码成功,业务码失败,，ErrCode存在业务结果未明确的情况,记录日志,需要再次查询确认结果
                    transResultDTO.setQuery(true);
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_TRANING);
                }
            } else {
                transResultDTO.setChannelErrCode(result.getReturnCode());
                transResultDTO.setChannelErrMsg(result.getReturnMsg());
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
            }
        } catch (WxPayException | ParseException e) {
            //打印异常堆栈信息
            e.printStackTrace();
            transResultDTO.setChannelErrCode("-1");
            transResultDTO.setChannelErrMsg(StrUtil.substr("微信转账请求异常:" + e.getMessage(), PayConstant.TRANS_ERROR_INFO_MAX_SIZE));
            transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
        }
        return ResultBody.failed().data(transResultDTO);
    }

    /**
     * 构建微信转账请求数据
     *
     * @param transOrder
     * @param wxPayConfig
     * @return
     */
    private EntPayRequest buildEntPayRequest(TransOrder transOrder, WxPayConfig wxPayConfig) {
        //转账请求实体
        EntPayRequest entPayRequest = new EntPayRequest();
        entPayRequest.setAmount(transOrder.getAmount().intValue());//设置转账金额
        //校验用户姓名选项[NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名]
        String checkName = "NO_CHECK";
        if (StringUtils.isNotEmpty(transOrder.getExtra())) {
            checkName = JSON.parseObject(transOrder.getExtra()).getString("checkName");
        }
        entPayRequest.setCheckName(checkName);
        //企业付款备注
        entPayRequest.setDescription(transOrder.getRemarkInfo());
        //收款用户姓名[如果check_name设置为FORCE_CHECK，则必填用户真实姓名]
        entPayRequest.setReUserName(transOrder.getUserName());
        //商户订单号，需保持唯一性(只能是字母或者数字，不能包含有其他字符)
        entPayRequest.setPartnerTradeNo(transOrder.getTransOrderId());
        //微信支付分配的终端设备号
        entPayRequest.setDeviceInfo(transOrder.getDevice());
        //该IP同在商户平台设置的IP白名单中的IP没有关联，该IP可传用户端或者服务端的IP。(必填)
        String ip = "47.97.181.171";//默认服务端IP
        if (StringUtils.isNotEmpty(transOrder.getClientIp())) {
            //前端传递非空时候,使用前端IP
            ip = transOrder.getClientIp();
        }
        entPayRequest.setSpbillCreateIp(ip);
        //商户appid下，某用户的openid
        entPayRequest.setOpenid(transOrder.getChannelUser());
        return entPayRequest;
    }

    /**
     * 获取转账结果
     *
     * @param transOrder
     * @param configStr
     * @return
     */
    @Override
    public ResultBody getTransReq(TransOrder transOrder, String configStr) {
        TransResultDTO transResultDTO = new TransResultDTO();
        //获取微信配置信息
        WxPayConfig wxPayConfig = WxPayUtil.getWxPayConfig(configStr, "", wxPayProperties.getCertRootPath(), wxPayProperties.getNotifyUrl());
        //获取微信支付服务
        WxPayService wxPayService = new WxPayServiceImpl();
        //设置配置信息
        wxPayService.setConfig(wxPayConfig);
        //获取微信转账服务
        EntPayService entPayService = wxPayService.getEntPayService();
        try {
            EntPayQueryResult entPayQueryResult = entPayService.queryEntPay(transOrder.getTransOrderId());
            //状态码
            String returnCode = entPayQueryResult.getReturnCode();
            //业务码
            String resultCode = entPayQueryResult.getResultCode();
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
                //付款是否成功需要查看status字段来判断
                String status = entPayQueryResult.getStatus();
                if ("SUCCESS".equals(status)) {
                    //转账成功
                    transResultDTO.setChannelOrderNo(entPayQueryResult.getDetailId());//微信付款单号
                    transResultDTO.setTransSuccTime(DateUtil.getTextDate(entPayQueryResult.getPaymentTime(), "yyyy-MM-dd HH:mm:ss"));
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_SUCCESS);
                    return ResultBody.ok().data(transResultDTO);
                } else if ("PROCESSING".equals(status)) {
                    //处理中
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_TRANING);
                } else if ("FAILED".equals(status)) {
                    //转账失败
                    transResultDTO.setChannelErrCode(status);
                    transResultDTO.setChannelErrMsg(entPayQueryResult.getReason());
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
                } else {
                    //未知原因
                    transResultDTO.setChannelErrCode(status);
                    transResultDTO.setChannelErrMsg("未知错误码");
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
                }
            } else {
                transResultDTO.setChannelErrCode(entPayQueryResult.getReturnCode());
                transResultDTO.setChannelErrMsg(entPayQueryResult.getReturnMsg());
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
            }
        } catch (WxPayException | ParseException e) {
            //打印异常堆栈信息
            e.printStackTrace();
            transResultDTO.setChannelErrCode("-1");
            transResultDTO.setChannelErrMsg(StrUtil.substr("微信转账查询请求异常:" + e.getMessage(), PayConstant.TRANS_ERROR_INFO_MAX_SIZE));
            transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
        }
        return ResultBody.failed().data(transResultDTO);
    }
}
