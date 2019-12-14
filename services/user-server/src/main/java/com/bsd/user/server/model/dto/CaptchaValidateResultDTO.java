package com.bsd.user.server.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: linrongxin
 * @Date: 2019/9/4 17:32
 */
@Builder
@Data
public class CaptchaValidateResultDTO {
    private String status;
    private String version;
}
