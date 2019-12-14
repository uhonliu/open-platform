package com.opencloud.msg.client.model;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * 异步通知消息
 *
 * @author liuyadu
 */
@ApiModel("异步通知消息")
public class WebHookMessage extends BaseMessage {
    private static final long serialVersionUID = 1566807113989212480L;
    /**
     * 通知回调路径
     */
    @ApiModelProperty("通知回调路径")
    private String url;
    /**
     * 请求内容
     */
    @ApiModelProperty("请求内容")
    private Map<String, String> data = Maps.newLinkedHashMap();
    /**
     * 通知业务类型
     */
    @ApiModelProperty("通知业务类型")
    private String type;

    public WebHookMessage() {
        super();
    }

    /**
     * 构建消息
     *
     * @param url  通知地址
     * @param type 通知业务类型
     * @param data 请求数据
     */
    public WebHookMessage(String url, String type, Map<String, String> data) {
        this.url = url;
        this.data = data;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpNotifyMsg{");
        sb.append("url='").append(url).append('\'');
        sb.append(", data=").append(data);
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
