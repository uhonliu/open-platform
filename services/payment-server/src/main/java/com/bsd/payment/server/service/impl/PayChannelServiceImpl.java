package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.domain.BaseParam;
import com.bsd.payment.server.enumm.RetEnum;
import com.bsd.payment.server.mapper.PayChannelMapper;
import com.bsd.payment.server.model.entity.PayChannel;
import com.bsd.payment.server.service.BaseService;
import com.bsd.payment.server.service.IPayChannelService;
import com.bsd.payment.server.util.JsonUtil;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.ObjectValidUtil;
import com.bsd.payment.server.util.RpcUtil;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
@Slf4j
@Service
public class PayChannelServiceImpl extends BaseService implements IPayChannelService {
    private static final MyLog _log = MyLog.getLog(PayChannelServiceImpl.class);

    @Resource
    private PayChannelMapper payChannelMapper;

    @Override
    public Map selectPayChannel(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("查询支付渠道信息失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mch_id") ? null : bizParamMap.get("mch_id").toString();
        String channelCode = baseParam.isNullValue("channel_code") ? null : bizParamMap.get("channel_code").toString();
        if (ObjectValidUtil.isInvalid(mchId, channelCode)) {
            _log.warn("查询支付渠道信息失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        PayChannel payChannel = super.baseSelectPayChannel(mchId, channelCode);
        if (payChannel == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(payChannel);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public JSONObject getByMchIdAndChannelCode(String mchId, String channelCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mch_id", mchId);
        paramMap.put("channel_code", channelCode);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result = selectPayChannel(jsonParam);
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return null;
        }
        return JSONObject.parseObject(s);
    }

    @Override
    public int addPayChannel(PayChannel payChannel) {
        return payChannelMapper.insertSelective(payChannel);

    }

    @Override
    public int updatePayChannel(PayChannel payChannel) {
        return payChannelMapper.updateByPrimaryKeySelective(payChannel);
    }

    @Override
    public PayChannel selectByPayChannelId(int channelId) {
        return payChannelMapper.selectByPrimaryKey(channelId);
    }

    @Override
    public IPage<PayChannel> findListPage(PageParams pageParams) {
        PayChannel query = pageParams.mapToObject(PayChannel.class);
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeStart, createTimeEnd);
        if (isCreateTime) {
            //创建时间范围
            String endTime = dateFormat.format(createTimeEnd);
            String startTime = dateFormat.format(createTimeStart);
            if (startTime.compareTo(endTime) == 1) {
                throw new OpenAlertException("创建开始时间不能大于创建结束时间");
            }
        }

        queryWrapper.lambda()
                .select(PayChannel::getChannelId, PayChannel::getChannelCode, PayChannel::getChannelName, PayChannel::getChannelMchId,
                        PayChannel::getMchId, PayChannel::getState, PayChannel::getRemark, PayChannel::getCreateTime, PayChannel::getUpdateTime)
                .likeRight(ObjectUtils.isNotEmpty(query.getChannelCode()), PayChannel::getChannelCode, query.getChannelCode())
                .likeRight(ObjectUtils.isNotEmpty(query.getMchId()), PayChannel::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotNull(query.getState()), PayChannel::getState, query.getState())
                .eq(ObjectUtils.isNotEmpty(query.getChannelName()), PayChannel::getChannelName, query.getChannelName())
                .eq(ObjectUtils.isNotEmpty(query.getChannelMchId()), PayChannel::getChannelMchId, query.getChannelMchId())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), PayChannel::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeEnd), PayChannel::getCreateTime, createTimeEnd)
                .orderByDesc(PayChannel::getCreateTime);
        return payChannelMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public PayChannel findPayChannel(String channelId) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(PayChannel::getChannelId, channelId);
        return payChannelMapper.selectOne(queryWrapper);
    }

    @Override
    public String getChannelParamConfig(String mchId, String channelCode) {
        //根据转账订单获取通道信息,配置信息
        List<PayChannel> payChannels = payChannelMapper.selectByMchIdAndChannelCode(mchId, channelCode);
        if (payChannels == null) {
            log.error("未找到商户对应的支付通道信息:mchId{},channelCode:{}", mchId, channelCode);
            return null;
        }
        if (payChannels.size() != 1) {
            log.error("支付通道编码存在多个支付通道信息:mchId{},channelCode:{}", mchId, channelCode);
            return null;
        }
        PayChannel payChannel = payChannels.get(0);
        if (payChannel.getState() != 1) {
            log.error("支付通道状态不可用:mchId{},channelCode:{}", mchId, channelCode);
            return null;
        }
        return payChannel.getParam();
    }
}
