package com.bsd.payment.server.service.mq.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.TransOrderPo;
import com.bsd.payment.server.model.dto.SimpleTransDTO;
import com.bsd.payment.server.model.dto.TransResultDTO;
import com.bsd.payment.server.model.entity.TransOrder;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.service.IPayService;
import com.bsd.payment.server.service.ITransOrderService;
import com.bsd.payment.server.service.mq.AbstractRabbitMqService;
import com.bsd.payment.server.service.mq.MqConfig;
import com.bsd.payment.server.service.mq.MqService;
import com.opencloud.common.model.ResultBody;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 转账订单交易MQ异步处理
 *
 * @Author: linrongxin
 * @Date: 2019/8/30 14:59
 */
@Slf4j
@Service("rabbitMq4TransServiceImpl")
public class RabbitMq4TransServiceImpl extends AbstractRabbitMqService implements MqService {

    @Autowired
    private Map<String, IPayService> payServiceMap;

    @Resource
    private IPayChannelService payChannelService;

    @Autowired
    private ITransOrderService transOrderService;


    @Override
    protected String getQueueName() {
        return MqConfig.TRANS_TRADE_QUEUE_NAME;
    }

    @Override
    protected String getExchangeName() {
        return MqConfig.TRANS_TRADE_EXCHANGE_NAME;
    }

    @Override
    @RabbitListener(queues = MqConfig.TRANS_TRADE_QUEUE_NAME)
    public void receive(String msg) {
        //记录接收消息日志
        log.info("{} receive :{}", MqConfig.TRANS_TRADE_QUEUE_NAME, msg);
        try {
            //反序列化消息
            SimpleTransDTO simpleTransDTO = JSON.parseObject(msg, SimpleTransDTO.class);
            //预处理与获取订单信息
            TransOrder transOrder = pretreatmentAndGetTransOrder(simpleTransDTO.getTransOrderId());
            if (transOrder == null) {
                return;
            }
            //根据通道名称获取服务
            IPayService service = payServiceMap.get(simpleTransDTO.getChannelName().toLowerCase() + PayConstant.PAY_CHANNEL_SERVICE_SUFFIX);
            if (service == null) {
                log.error("找不到对应的转账通道服务,转账订单:{} channelName:{}", simpleTransDTO, simpleTransDTO.getChannelName());
                return;
            }
            //获取通道配置信息
            String configStr = payChannelService.getChannelParamConfig(transOrder.getMchId(), transOrder.getChannelCode());
            if (StringUtils.isEmpty(configStr)) {
                log.error("找不到商户通道配置信息", transOrder.getTransOrderId());
                return;
            }
            //调用转账服务
            ResultBody<TransResultDTO> resultBody = service.doTransReq(transOrder, configStr);
            //处理转账结果
            handleTransResult(service, transOrder, configStr, resultBody);
        } catch (Exception ex) {
            //打印堆栈信息
            ex.printStackTrace();
            //记录错误日志
            log.error("{} 异常 :{}", MqConfig.TRANS_TRADE_QUEUE_NAME, ex.getMessage());
        }
    }

    /**
     * 处理转账结果
     *
     * @param service
     * @param transOrder
     * @param configStr
     * @param resultBody
     */
    private void handleTransResult(IPayService service, TransOrder transOrder, String configStr, ResultBody<TransResultDTO> resultBody) {
        //判断是否业务结果未明,是否需要主动查询结果
        TransResultDTO transResultDTO = resultBody.getData();
        if (transResultDTO.isQuery()) {
            //主动查询转账结果,以确定未明订单状态
            resultBody = service.getTransReq(transOrder, configStr);
        }
        //处理转账结果
        TransOrderPo transOrderPo = new TransOrderPo();
        BeanUtils.copyProperties(transOrder, transOrderPo);
        transOrderService.handleTransResult(resultBody, transOrderPo);
    }

    /**
     * 与处理与获取订单
     *
     * @param transOrderId
     * @return
     */
    private TransOrder pretreatmentAndGetTransOrder(String transOrderId) {
        //查询订单
        TransOrder transOrder = transOrderService.getByTransOrderId(transOrderId);
        if (transOrder == null) {
            //转账订单不存在,记录错误日志
            log.error("转账订单不存在 :{}", transOrderId);
            return null;
        }
        if (transOrder.getStatus() != PayConstant.TRANS_STATUS_INIT) {
            //转账状态不是:0-订单生成
            log.error("转账订单状态错误,订单:{},状态:{}", transOrderId, transOrder.getStatus());
            return null;
        }
        //转账订单状态修改成转账中
        int count = transOrderService.updateStatus4Traning(transOrderId);
        if (count <= 0) {
            log.error("修改转账订单状态为转账中失败,转账订单ID:{}", transOrderId);
            return null;
        }
        return transOrder;
    }
}
