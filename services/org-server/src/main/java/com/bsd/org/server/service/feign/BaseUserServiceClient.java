package com.bsd.org.server.service.feign;

import com.opencloud.base.client.constants.BaseConstants;
import com.opencloud.base.client.service.IBaseUserServiceClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @Author: linrongxin
 * @Date: 2019/9/21 16:22
 */
@Component
@FeignClient(value = BaseConstants.BASE_SERVER)
public interface BaseUserServiceClient extends IBaseUserServiceClient {

}
