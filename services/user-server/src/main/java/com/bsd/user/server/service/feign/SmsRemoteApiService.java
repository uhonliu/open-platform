package com.bsd.user.server.service.feign;

import com.opencloud.msg.client.constatns.MsgConstants;
import com.opencloud.msg.client.service.ISmsClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 短信服务feign客户端
 *
 * @author lisongmao
 * 2019年6月27日
 */
@FeignClient(value = MsgConstants.MSG_SERVICE)
public interface SmsRemoteApiService extends ISmsClient {

}
