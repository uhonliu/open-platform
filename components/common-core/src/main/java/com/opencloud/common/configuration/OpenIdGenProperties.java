package com.opencloud.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自定义ID生成器配置
 *
 * @author LYD
 * @date 2018/7/29
 */
@ConfigurationProperties(prefix = "opencloud.id")
public class OpenIdGenProperties {
    /**
     * 工作ID (0~31)
     */
    private long workId = 0;
    /**
     * 数据中心ID (0~31)
     */
    private long centerId = 0;

    public long getWorkId() {
        return workId;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    public long getCenterId() {
        return centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OpenIdGenProperties{");
        sb.append("workId=").append(workId);
        sb.append(", centerId=").append(centerId);
        sb.append('}');
        return sb.toString();
    }
}
