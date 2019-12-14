package com.bsd.payment.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bsd.payment.server.constant.PayConstant;
import com.bsd.payment.server.mapper.MchNotifyMapper;
import com.bsd.payment.server.model.entity.MchNotify;
import com.bsd.payment.server.service.IMchNotifyService;
import com.bsd.payment.server.util.ObjectValidUtil;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import com.opencloud.common.mybatis.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangyankai
 * @date 2019/8/20
 */
@Service
public class MchNotifyServiceImpl extends BaseServiceImpl<MchNotifyMapper, MchNotify> implements IMchNotifyService {
    @Resource
    private MchNotifyMapper mchNotifyMapper;

    /**
     * 分页查询
     *
     * @param pageParams
     * @return
     */
    @Override
    public IPage<MchNotify> findListPage(PageParams pageParams) {
        MchNotify query = pageParams.mapToObject(MchNotify.class);
        QueryWrapper<MchNotify> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object lastNotifyTimeStart = pageParams.getRequestMap().get("lastNotifyTimeStart");
        Object lastNotifyTimeEnd = pageParams.getRequestMap().get("lastNotifyTimeEnd");
        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeEnd, createTimeStart);
        boolean islastNotifyTime = ObjectValidUtil.isNotEmptyBatch(lastNotifyTimeStart, lastNotifyTimeEnd);

        if (isCreateTime) {
            //创建时间范围
            String endTime = dateFormat.format(createTimeEnd);
            String startTime = dateFormat.format(createTimeStart);
            if (startTime.compareTo(endTime) == 1) {
                throw new OpenAlertException("创建开始时间不能大于创建结束时间");
            }
        }
        if (islastNotifyTime) {
            String startTime = dateFormat.format(lastNotifyTimeStart);
            String endTime = dateFormat.format(lastNotifyTimeEnd);
            if (startTime.compareTo(endTime) == 1) {
                throw new OpenAlertException("最后一次通知开始时间不能大于最后一次通知结束时间");
            }
        }

        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getOrderId()), MchNotify::getOrderId, query.getOrderId())
                .eq(ObjectUtils.isNotEmpty(query.getMchId()), MchNotify::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotNull(query.getStatus()), MchNotify::getStatus, query.getStatus())
                .likeRight(ObjectUtils.isNotEmpty(query.getMchOrderNo()), MchNotify::getMchOrderNo, query.getMchOrderNo())
                .eq(ObjectUtils.isNotEmpty(query.getOrderType()), MchNotify::getOrderType, query.getOrderType())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), MchNotify::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeEnd), MchNotify::getCreateTime, createTimeEnd)
                .ge(ObjectUtils.isNotEmpty(lastNotifyTimeStart), MchNotify::getLastNotifyTime, lastNotifyTimeStart)
                .le(ObjectUtils.isNotEmpty(lastNotifyTimeEnd), MchNotify::getLastNotifyTime, lastNotifyTimeEnd)
                .orderByDesc(MchNotify::getCreateTime);
        return mchNotifyMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public MchNotify findMchNotify(String orderId) {
        QueryWrapper<MchNotify> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(MchNotify::getOrderId, orderId);
        return mchNotifyMapper.selectOne(queryWrapper);
    }

    @Override
    public int updateMchNotifyStatus(String orderId, byte status, String result, byte notifyCount) {
        MchNotify mchNotify = new MchNotify();
        mchNotify.setStatus(status);
        mchNotify.setResult(result);
        mchNotify.setNotifyCount(notifyCount);
        mchNotify.setLastNotifyTime(new Date());
        int count = mchNotifyMapper.update(mchNotify, Wrappers.<MchNotify>lambdaUpdate()
                .eq(MchNotify::getOrderId, orderId)
                .and(x -> x.eq(MchNotify::getStatus, PayConstant.MCH_NOTIFY_STATUS_NOTIFYING).or()
                        .eq(MchNotify::getStatus, PayConstant.MCH_NOTIFY_STATUS_FAIL)));
        return count;
    }
}
