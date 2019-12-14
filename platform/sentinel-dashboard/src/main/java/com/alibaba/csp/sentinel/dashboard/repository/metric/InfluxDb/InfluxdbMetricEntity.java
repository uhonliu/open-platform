/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.repository.metric.InfluxDb;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

/**
 * @author zhipeng.zhang
 * https://blog.52itstyle.vip
 */
@Measurement(name = "sentinelInfo")
public class InfluxdbMetricEntity {
    @Column(name = "time")
    private Instant time;
    @Column(name = "gmtCreate")
    private Long gmtCreate;
    @Column(name = "gmtModified")
    private Long gmtModified;
    /**
     * 监控信息的时间戳
     */
    @Column(name = "app", tag = true)
    private String app;
    @Column(name = "resource", tag = true)
    private String resource;
    @Column(name = "timestamp")
    private Long timestamp;
    @Column(name = "passQps")
    private Long passQps;//通过qps
    @Column(name = "successQps")
    private Long successQps;//成功qps
    @Column(name = "blockQps")
    private Long blockQps;//限流qps
    @Column(name = "exceptionQps")
    private Long exceptionQps;//异常qps

    /**
     * 所有successQps的rt的和
     */
    @Column(name = "rt")
    private double rt;

    /**
     * 本次聚合的总条数
     */
    @Column(name = "count")
    private int count;
    @Column(name = "resourceCode")
    private int resourceCode;

    public static InfluxdbMetricEntity copyOf(InfluxdbMetricEntity oldEntity) {
        InfluxdbMetricEntity entity = new InfluxdbMetricEntity();
        entity.setApp(oldEntity.getApp());
        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(oldEntity.getGmtModified());
        entity.setTimestamp(oldEntity.getTimestamp());
        entity.setResource(oldEntity.getResource());
        entity.setPassQps(oldEntity.getPassQps());
        entity.setBlockQps(oldEntity.getBlockQps());
        entity.setSuccessQps(oldEntity.getSuccessQps());
        entity.setExceptionQps(oldEntity.getExceptionQps());
        entity.setRt(oldEntity.getRt());
        entity.setCount(oldEntity.getCount());
        entity.setResource(oldEntity.getResource());
        return entity;
    }

    public synchronized void addPassQps(Long passQps) {
        this.passQps += passQps;
    }

    public synchronized void addBlockQps(Long blockQps) {
        this.blockQps += blockQps;
    }

    public synchronized void addExceptionQps(Long exceptionQps) {
        this.exceptionQps += exceptionQps;
    }

    public synchronized void addCount(int count) {
        this.count += count;
    }

    public synchronized void addRtAndSuccessQps(double avgRt, Long successQps) {
        this.rt += avgRt * successQps;
        this.successQps += successQps;
    }

    /**
     * {@link #rt} = {@code avgRt * successQps}
     *
     * @param avgRt      average rt of {@code successQps}
     * @param successQps
     */
    public synchronized void setRtAndSuccessQps(double avgRt, Long successQps) {
        this.rt = avgRt * successQps;
        this.successQps = successQps;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
        this.resourceCode = resource.hashCode();
    }

    public Long getPassQps() {
        return passQps;
    }

    public void setPassQps(Long passQps) {
        this.passQps = passQps;
    }

    public Long getBlockQps() {
        return blockQps;
    }

    public void setBlockQps(Long blockQps) {
        this.blockQps = blockQps;
    }

    public Long getExceptionQps() {
        return exceptionQps;
    }

    public void setExceptionQps(Long exceptionQps) {
        this.exceptionQps = exceptionQps;
    }

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getResourceCode() {
        return resourceCode;
    }

    public Long getSuccessQps() {
        return successQps;
    }

    public void setSuccessQps(Long successQps) {
        this.successQps = successQps;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public String toString() {
        return "InfluxdbMetricEntity{" +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", timestamp=" + timestamp +
                ", resource='" + resource + '\'' +
                ", passQps=" + passQps +
                ", blockQps=" + blockQps +
                ", successQps=" + successQps +
                ", exceptionQps=" + exceptionQps +
                ", rt=" + rt +
                ", count=" + count +
                ", resourceCode=" + resourceCode +
                '}';
    }
}
