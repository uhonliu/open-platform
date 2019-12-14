package com.bsd.user.server.service;

import com.bsd.user.server.model.dto.CaptchaInitDTO;
import com.bsd.user.server.model.dto.CaptchaInitResultDTO;
import com.bsd.user.server.model.dto.CaptchaValidateDTO;
import com.bsd.user.server.model.dto.CaptchaValidateResultDTO;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 16:32
 */
public interface CaptchaService {
    /**
     * 初始化
     *
     * @param captchaInitDTO
     * @return
     */
    CaptchaInitResultDTO init(CaptchaInitDTO captchaInitDTO);

    /**
     * 二次验证
     *
     * @param captchaValidateDTO
     * @return
     */
    CaptchaValidateResultDTO validate(CaptchaValidateDTO captchaValidateDTO);
}
