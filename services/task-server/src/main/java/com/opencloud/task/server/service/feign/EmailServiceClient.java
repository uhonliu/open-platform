package com.opencloud.task.server.service.feign;

import com.opencloud.msg.client.constatns.MsgConstants;
import com.opencloud.msg.client.service.IEmailClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: liuyadu
 * @date: 2019/4/1 12:57
 * @description:
 */
@Component
@FeignClient(value = MsgConstants.MSG_SERVICE)
public interface EmailServiceClient extends IEmailClient {

}
