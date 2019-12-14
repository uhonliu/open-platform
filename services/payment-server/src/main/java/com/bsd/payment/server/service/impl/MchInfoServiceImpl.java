package com.bsd.payment.server.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.bsd.payment.server.domain.BaseParam;
import com.bsd.payment.server.enumm.RetEnum;
import com.bsd.payment.server.mapper.MchInfoMapper;
import com.bsd.payment.server.model.entity.MchInfo;
import com.bsd.payment.server.service.BaseService;
import com.bsd.payment.server.service.IMchInfoService;
import com.bsd.payment.server.util.JsonUtil;
import com.bsd.payment.server.util.MyLog;
import com.bsd.payment.server.util.ObjectValidUtil;
import com.bsd.payment.server.util.RpcUtil;
import com.opencloud.common.exception.OpenAlertException;
import com.opencloud.common.model.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
@Service
public class MchInfoServiceImpl extends BaseService implements IMchInfoService {
    private static final MyLog _log = MyLog.getLog(MchInfoServiceImpl.class);

    @Resource
    private MchInfoMapper mchInfoMapper;

    @Override
    public Map selectMchInfo(String jsonParam) {
        BaseParam baseParam = JsonUtil.getObjectFromJson(jsonParam, BaseParam.class);
        Map<String, Object> bizParamMap = baseParam.getBizParamMap();
        if (ObjectValidUtil.isInvalid(bizParamMap)) {
            _log.warn("查询商户信息失败, {}. jsonParam={}", RetEnum.RET_PARAM_NOT_FOUND.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_NOT_FOUND);
        }
        String mchId = baseParam.isNullValue("mchId") ? null : bizParamMap.get("mchId").toString();
        if (ObjectValidUtil.isInvalid(mchId)) {
            _log.warn("查询商户信息失败, {}. jsonParam={}", RetEnum.RET_PARAM_INVALID.getMessage(), jsonParam);
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_PARAM_INVALID);
        }
        MchInfo mchInfo = super.baseSelectMchInfo(mchId);
        if (mchInfo == null) {
            return RpcUtil.createFailResult(baseParam, RetEnum.RET_BIZ_DATA_NOT_EXISTS);
        }
        String jsonResult = JsonUtil.object2Json(mchInfo);
        return RpcUtil.createBizResult(baseParam, jsonResult);
    }

    @Override
    public JSONObject getByMchId(String mchId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", mchId);
        String jsonParam = RpcUtil.createBaseParam(paramMap);
        Map<String, Object> result = selectMchInfo(jsonParam);
        String s = RpcUtil.mkRet(result);
        if (s == null) {
            return null;
        }
        return JSONObject.parseObject(s);
    }

    @Override
    public int addMchInfo(MchInfo mchInfo) {
        checkMchInfo(mchInfo);
        //拿到商户id
        List<MchInfo> mchInfos = mchInfoMapper.selectMchId();
        String mchId = "10000000";
        if (!CollectionUtils.isEmpty(mchInfos)) {
            mchId = String.valueOf(Integer.parseInt(mchInfos.get(0).getMchId()) + 1);
        }
        mchInfo.setMchId(mchId);
        mchInfo.setCreateTime(new Date());
        return mchInfoMapper.insertSelective(mchInfo);
    }

    @Override
    public int updateMchInfo(MchInfo mchInfo) {
        checkMchInfo(mchInfo);
        mchInfo.setUpdateTime(new Date());
        return mchInfoMapper.updateByPrimaryKeySelective(mchInfo);
    }

    /**
     * 检查参数
     *
     * @param mchInfo
     */
    private void checkMchInfo(MchInfo mchInfo) {
        if (StringUtils.isEmpty(mchInfo.getName())) {
            throw new OpenAlertException("商户名称不能为空");
        }
        if (StringUtils.isEmpty(mchInfo.getType())) {
            throw new OpenAlertException("商户类型不能为空");
        }
//        if (StringUtils.isEmpty(mchInfo.getResKey())) {
//            throw new OpenAlertException("商户请求私钥不能为空");
//        }
//        if (StringUtils.isEmpty(mchInfo.getResKey())) {
//            throw new OpenAlertException("商户响应私钥不能为空");
//        }
        if (mchInfo.getState() == null) {
            throw new OpenAlertException("商户状态不能为空");
        }
        if (mchInfo.getState().intValue() != 0 && mchInfo.getState().intValue() != 1) {
            throw new OpenAlertException("商户状态值错误");
        }
    }

    @Override
    public IPage<MchInfo> findListPage(PageParams pageParams) {
        MchInfo query = pageParams.mapToObject(MchInfo.class);
        QueryWrapper<MchInfo> queryWrapper = new QueryWrapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Object createTimeStart = pageParams.getRequestMap().get("createTimeStart");
        Object createTimeEnd = pageParams.getRequestMap().get("createTimeEnd");
        boolean isCreateTime = ObjectValidUtil.isNotEmptyBatch(createTimeStart, createTimeEnd);

        if (isCreateTime) {
            String startTime = dateFormat.format(createTimeStart);
            String endTime = dateFormat.format(createTimeEnd);
            if (startTime.compareTo(endTime) == 1) {
                throw new OpenAlertException("创建开始时间不能大于创建结束时间!");
            }
        }
        queryWrapper.lambda()
                .likeRight(ObjectUtils.isNotEmpty(query.getMchId()), MchInfo::getMchId, query.getMchId())
                .eq(ObjectUtils.isNotEmpty(query.getName()), MchInfo::getName, query.getName())
                .eq(ObjectUtils.isNotEmpty(query.getType()), MchInfo::getType, query.getType())
                .eq(ObjectUtils.isNotNull(query.getState()), MchInfo::getState, query.getState())
                .ge(ObjectUtils.isNotEmpty(createTimeStart), MchInfo::getCreateTime, createTimeStart)
                .le(ObjectUtils.isNotEmpty(createTimeEnd), MchInfo::getCreateTime, createTimeEnd)
                .orderByDesc(MchInfo::getCreateTime);
        return mchInfoMapper.selectPage(pageParams, queryWrapper);
    }

    @Override
    public MchInfo findMchInfo(String mchId) {
        QueryWrapper<MchInfo> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .eq(MchInfo::getMchId, mchId);
        return mchInfoMapper.selectOne(queryWrapper);
    }
}
