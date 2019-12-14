package com.opencloud.gateway.spring.server.actuator;

import com.opencloud.common.event.RemoteRefreshRouteEvent;
import com.opencloud.common.model.ResultBody;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.cloud.bus.endpoint.AbstractBusEndpoint;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 自定义网关监控端点
 *
 * @author liuyadu
 */
@RestControllerEndpoint(
        id = "open"
)
public class ApiEndpoint extends AbstractBusEndpoint {
    public ApiEndpoint(ApplicationEventPublisher context, String id) {
        super(context, id);
    }

    /**
     * 支持灰度发布
     * /actuator/open/refresh?destination = customers：**
     *
     * @param destination
     */
    @PostMapping("/refresh")
    public ResultBody busRefreshWithDestination(@RequestParam(required = false) String destination) {
        this.publish(new RemoteRefreshRouteEvent(this, this.getInstanceId(), destination));
        return ResultBody.ok();
    }
}
