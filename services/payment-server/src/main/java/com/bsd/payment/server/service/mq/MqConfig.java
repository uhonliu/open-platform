package com.bsd.payment.server.service.mq;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description:
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class MqConfig {
    public static final String PAY_NOTIFY_QUEUE_NAME = "pay.notify.queue";

    public static final String PAY_NOTIFY_EXCHANGE_NAME = "pay.notify.exchange";

    /**
     * 退款业务
     */
    public static final String REFUND_NOTIFY_QUEUE_NAME = "refund.notify.queue";

    public static final String REFUND_NOTIFY_EXCHANGE_NAME = "refund.notify.exchange";

    /**
     * 退款通知商户
     */
    public static final String MCH_REFUND_NOTIFY_QUEUE_NAME = "refund.notify.mch.queue";

    public static final String MCH_REFUND_NOTIFY_EXCHANGE_NAME = "refund.notify.mch.exchange";

    /**
     * 转账交易异步处理
     */
    public static final String TRANS_TRADE_QUEUE_NAME = "trans.trade.queue";
    public static final String TRANS_TRADE_EXCHANGE_NAME = "trans.trade.exchange";

    /**
     * 转账结果异步通知商户
     */
    public static final String MCH_TRANS_NOTIFY_QUEUE_NAME = "mch.trans.notify.queue";
    public static final String MCH_TRANS_NOTIFY_EXCHANGE_NAME = "mch.trans.notify.exchange";

    /**
     * 转账查询异步处理
     */
    public static final String TRANS_QUERY_QUEUE_NAME = "trans.query.queue";
    public static final String TRANS_QUERY_EXCHANGE_NAME = "trans.query.exchange";

    public static class Impl {
        // public static final String ACTIVE_MQ = "activeMQ";
        public static final String RABBIT_MQ = "rabbitMQ";
    }
}
