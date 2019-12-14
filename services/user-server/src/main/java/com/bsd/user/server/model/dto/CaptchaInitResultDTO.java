package com.bsd.user.server.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 16:56
 */
@Builder
@Data
public class CaptchaInitResultDTO {
    private Integer gtServerStatus;
    private Object result;
}
