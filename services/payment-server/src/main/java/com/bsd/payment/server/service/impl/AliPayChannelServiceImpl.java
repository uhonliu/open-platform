package com.bsd.payment.server.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.bsd.payment.server.configuration.channel.alipay.AlipayProperties;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.IPayService;
import com.bsd.payment.server.util.AmountUtil;
import com.bsd.payment.server.util.DateUtil;
import com.bsd.payment.server.util.StrUtil;
import com.opencloud.common.model.ResultBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * @Author: linrongxin
 * @Date: 2019/8/30 16:18
 */
@Slf4j
@Service("alipayChannelServiceImpl")
public class AliPayChannelServiceImpl implements IPayService {
    /**
     * 单笔转账到支付宝账户接口
     * 参考地址:https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
     *
     * @param transOrder
     */
    @Override
    public ResultBody<TransResultDTO> doTransReq(TransOrder transOrder, String configStr) {
        TransResultDTO transResultDTO = new TransResultDTO();
        //获取配置信息
        AlipayProperties alipayProperties = new AlipayProperties();
        alipayProperties.init(configStr);
        //创建支付宝请求客户端
        AlipayClient client = new DefaultAlipayClient(alipayProperties.getUrl(), alipayProperties.getAppid(), alipayProperties.getPrivateKey(), AlipayProperties.FORMAT, AlipayProperties.CHARSET, alipayProperties.getPublicKey(), AlipayProperties.SIGNTYPE);
        //支付宝单笔转账到支付宝账户请求
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        //数据模型
        AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
        model.setOutBizNo(transOrder.getTransOrderId());//商户转账唯一订单号
        model.setPayeeType("ALIPAY_LOGONID");//收款方账户类型。可取值：1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。 2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
        model.setPayeeAccount(transOrder.getChannelUser());//收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户。
        model.setAmount(AmountUtil.convertCent2Dollar(transOrder.getAmount().toString()));//转账金额，单位：元。
        model.setPayerShowName("支付转账");//付款方姓名（最长支持100个英文/50个汉字）。显示在收款方的账单详情页。
        model.setPayeeRealName(transOrder.getUserName()); //收款方真实姓名（最长支持100个英文/50个汉字）
        model.setRemark(transOrder.getRemarkInfo()); //转账备注（支持200个英文/100个汉字）
        request.setBizModel(model);
        try {
            //发起转账请求
            AlipayFundTransToaccountTransferResponse response = client.execute(request);
            if (response.isSuccess()) {
                transResultDTO.setChannelOrderNo(response.getOrderId());
                transResultDTO.setTransSuccTime(DateUtil.getTextDate(response.getPayDate(), "yyyy-MM-dd HH:mm:ss"));
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_SUCCESS);
                return ResultBody.ok().data(transResultDTO);
            } else {
                transResultDTO.setChannelErrCode(response.getSubCode());
                transResultDTO.setChannelErrMsg(response.getSubMsg());
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
            }
        } catch (AlipayApiException | ParseException e) {
            //打印异常堆栈信息
            e.printStackTrace();
            transResultDTO.setChannelErrCode("-1");
            transResultDTO.setChannelErrMsg(StrUtil.substr("支付宝转账请求异常:" + e.getMessage(), PayConstant.TRANS_ERROR_INFO_MAX_SIZE));
            transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
        }
        return ResultBody.failed().data(transResultDTO);
    }


    /**
     * 转账查询
     *
     * @param transOrder
     * @param configStr
     * @return
     */
    @Override
    public ResultBody getTransReq(TransOrder transOrder, String configStr) {
        TransResultDTO transResultDTO = new TransResultDTO();
        //获取配置信息
        AlipayProperties alipayProperties = new AlipayProperties();
        alipayProperties.init(configStr);
        //创建支付宝请求客户端
        AlipayClient client = new DefaultAlipayClient(alipayProperties.getUrl(), alipayProperties.getAppid(), alipayProperties.getPrivateKey(), AlipayProperties.FORMAT, AlipayProperties.CHARSET, alipayProperties.getPublicKey(), AlipayProperties.SIGNTYPE);
        //转账订单查询请求实体
        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        //查询请求模型
        AlipayFundTransOrderQueryModel model = new AlipayFundTransOrderQueryModel();
        //商户转账唯一订单号
        model.setOutBizNo(transOrder.getTransOrderId());
        //支付宝转账单据号：和商户转账唯一订单号不能同时为空。
        model.setOrderId(transOrder.getChannelOrderNo());
        //设置业务参数
        request.setBizModel(model);
        try {
            //请求查询转账订单
            AlipayFundTransOrderQueryResponse response = client.execute(request);
            if (response.isSuccess()) {
                /**
                 *  业务处理结果
                 *  转账单据状态。
                 *  SUCCESS：成功（配合"单笔转账到银行账户接口"产品使用时, 同一笔单据多次查询有可能从成功变成退票状态）；
                 *  FAIL：失败（具体失败原因请参见error_code以及fail_reason返回值）；
                 *  INIT：等待处理；
                 *  DEALING：处理中；
                 *  REFUND：退票（仅配合"单笔转账到银行账户接口"产品使用时会涉及, 具体退票原因请参见fail_reason返回值）；
                 *  UNKNOWN：状态未知。
                 */
                String status = response.getStatus();
                if ("SUCCESS".equals(status)) {
                    transResultDTO.setChannelOrderNo(response.getOrderId());
                    transResultDTO.setTransSuccTime(DateUtil.getTextDate(response.getPayDate(), "yyyy-MM-dd HH:mm:ss"));
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_SUCCESS);
                    return ResultBody.ok().data(transResultDTO);
                } else if ("INIT".equals(status) || "DEALING".equals(status) || "UNKNOWN".equals(status)) {
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_TRANING);
                } else {
                    transResultDTO.setChannelErrCode(status + "|" + response.getErrorCode());
                    transResultDTO.setChannelErrMsg(response.getFailReason());
                    transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
                }
            } else {
                transResultDTO.setChannelErrCode(response.getSubCode());
                transResultDTO.setChannelErrMsg(response.getSubMsg());
                transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
            }
        } catch (AlipayApiException | ParseException e) {
            //打印异常堆栈信息
            e.printStackTrace();
            transResultDTO.setChannelErrCode("-1");
            transResultDTO.setChannelErrMsg(StrUtil.substr("支付宝转账查询请求异常:" + e.getMessage(), PayConstant.TRANS_ERROR_INFO_MAX_SIZE));
            transResultDTO.setTransStatus(PayConstant.TRANS_STATUS_FAIL);
        }
        return ResultBody.failed().data(transResultDTO);
    }
}
