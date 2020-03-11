package com.bsd.user.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.bsd.user.server.configuration.GeetestProperties;
import com.bsd.user.server.enums.GeetestClientTypeEnum;
import com.bsd.user.server.model.dto.CaptchaInitDTO;
import com.bsd.user.server.model.dto.CaptchaInitResultDTO;
import com.bsd.user.server.model.dto.CaptchaValidateDTO;
import com.bsd.user.server.model.dto.CaptchaValidateResultDTO;
import com.bsd.user.server.service.CaptchaService;
import com.bsd.user.server.utils.geetest.GeetestLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 16:39
 */
@Service
public class GeetestCaptchaServiceImpl implements CaptchaService {
    @Autowired
    private GeetestProperties geetestProperties;

    /**
     * 初始化
     *
     * @param captchaInitDTO
     * @return
     */
    @Override
    public CaptchaInitResultDTO init(CaptchaInitDTO captchaInitDTO) {
        //客户端类型不在范围内,设置为未知
        if (!GeetestClientTypeEnum.isContainCode(captchaInitDTO.getClientType())) {
            captchaInitDTO.setClientType(GeetestClientTypeEnum.UNKNOWN.getClientTypeCode());
        }
        //获取SDK工具类
        GeetestLib gtSdk = new GeetestLib(geetestProperties.getCaptchaId(), geetestProperties.getPrivateKey(), true);
        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(gtSdk.createCustomerParam(captchaInitDTO.getUserId(), captchaInitDTO.getClientType(), captchaInitDTO.getIp()));
        //返回初始化结果
        return CaptchaInitResultDTO.builder().gtServerStatus(gtServerStatus).result(JSON.parse(gtSdk.getResponseStr())).build();
    }

    /**
     * 二次验证
     *
     * @param captchaValidateDTO
     * @return
     */
    @Override
    public CaptchaValidateResultDTO validate(CaptchaValidateDTO captchaValidateDTO) {
        //获取SDK工具类
        GeetestLib gtSdk = new GeetestLib(geetestProperties.getCaptchaId(), geetestProperties.getPrivateKey(), true);
        int result = 0;
        if (captchaValidateDTO.getGtServerStatus() == 1) {
            //gt-server正常，向gt-server进行二次验证
            try {
                result = gtSdk.enhancedValidateRequest(captchaValidateDTO);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //gt-server非正常情况下，进行failback模式验证
            result = gtSdk.failbackValidateRequest(captchaValidateDTO.getChllenge(), captchaValidateDTO.getValidate(), captchaValidateDTO.getSeccode());
        }
        return CaptchaValidateResultDTO.builder().status(result == 0 ? "fail" : "success").version(gtSdk.getVersionInfo()).build();
    }
}
