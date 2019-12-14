package com.bsd.payment.server.service.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.model.TransOrderPo;
import com.bsd.payment.server.model.dto.TransResultDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 转账订单查询MQ异步处理
 *
 * @Author: linrongxin
 * @Date: 2019/9/2 18:28
 */
@Slf4j
@Service("rabbitMq4TransQueryServiceImpl")
public class RabbitMq4TransQueryServiceImpl extends AbstractRabbitMqService implements MqService {
    @Autowired
    private ITransOrderService transOrderService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    private Map<String, IPayService> payServiceMap;

    @Override
    protected String getQueueName() {
        return MqConfig.TRANS_QUERY_QUEUE_NAME;
    }

    @Override
    protected String getExchangeName() {
        return MqConfig.TRANS_QUERY_EXCHANGE_NAME;
    }

    /**
     * 结果未明转账订单进行异步查询
     *
     * @param msg
     */
    @Override
    @RabbitListener(queues = MqConfig.TRANS_QUERY_QUEUE_NAME)
    public void receive(String msg) {
        TransOrderPo transOrder = JSON.parseObject(msg, TransOrderPo.class);
        int queryCount = transOrder.getQueryCount();
        //查询次数超过6次,不在查询,不做处理
        if (queryCount >= 6) {
            return;
        }
        JSONObject payChannel = payChannelService.getByMchIdAndChannelCode(transOrder.getMchId(), transOrder.getChannelCode());
        if (payChannel == null) {
            log.error("找不到对应的转账通道,转账订单:{} channelCode:{}", transOrder.getTransOrderId(), transOrder.getChannelCode());
        }
        String channelName = payChannel.getString("channelName");
        IPayService service = payServiceMap.get(channelName.toLowerCase() + PayConstant.PAY_CHANNEL_SERVICE_SUFFIX);
        if (service == null) {
            log.error("找不到对应的转账通道服务,转账订单:{} channelName:{}", transOrder.getTransOrderId(), channelName);
            return;
        }
        //通道配置信息
        String configStr = payChannel.getString("param");
        if (StringUtils.isEmpty(configStr)) {
            log.error("找不到商户通道配置信息", transOrder.getTransOrderId());
            return;
        }
        //查询转账结果
        ResultBody<TransResultDTO> resultBody = service.getTransReq(transOrder, configStr);
        //查询次数加1
        transOrder.setQueryCount(queryCount + 1);
        //处理转账结果
        transOrderService.handleTransResult(resultBody, transOrder);
    }
}
