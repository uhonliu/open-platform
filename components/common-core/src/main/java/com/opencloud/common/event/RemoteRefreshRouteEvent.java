package com.opencloud.common.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * 自定义网关刷新远程事件
 *
 * @author liuyadu
 */
public class RemoteRefreshRouteEvent extends RemoteApplicationEvent {
    private RemoteRefreshRouteEvent() {
    }

    public RemoteRefreshRouteEvent(Object source, String originService, String destinationService) {
        super(source, originService, destinationService);
    }
}
