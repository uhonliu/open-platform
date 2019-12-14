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

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Caches metrics data in a period of time in Influxdb.
 * 参考：https://github.com/influxdata/influxdb-java
 * 参考：https://blog.52itstyle.vip/archives/4460
 *
 * @author zhipeng.zhang
 * https://blog.52itstyle.vip
 */
@Component("inInfluxdbMetricsRepository")
public class InInfluxdbMetricsRepository implements MetricsRepository<MetricEntity> {
    @Autowired
    public InfluxDB influxDB;

    @Override
    public synchronized void save(MetricEntity metric) {
        try {
            Point point = Point
                    .measurement("sentinelInfo")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("app", metric.getApp())//tag 数据走索引
                    .tag("resource", metric.getResource())//tag 数据走索引
                    .addField("gmtCreate", metric.getGmtCreate().getTime())
                    .addField("gmtModified", metric.getGmtModified().getTime())
                    .addField("timestamp", metric.getTimestamp().getTime())
                    .addField("passQps", metric.getPassQps())
                    .addField("successQps", metric.getSuccessQps())
                    .addField("blockQps", metric.getBlockQps())
                    .addField("exceptionQps", metric.getExceptionQps())
                    .addField("rt", metric.getRt())
                    .addField("count", metric.getCount())
                    .addField("resourceCode", metric.getResourceCode())
                    .build();
            influxDB.write(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        BatchPoints batchPoints = BatchPoints.builder()
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        metrics.forEach(metric -> {
            Point point = Point
                    .measurement("sentinelInfo")
                    /**
                     * 这里使用毫秒
                     * 但是从客户端过来的请求可能是多个(部分静态资源没有被过滤掉)
                     * 导致数据唯一标识重复，后面资源的把前面的覆盖点
                     * 如果还有覆盖数据就使用微妙，保证 time 和 tag 唯一就可以
                     * 但是有个问题，使用微妙会导致查询时间有问题，建议这里过滤掉静态请求
                     * 或者客户端屏蔽
                     */
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("app", metric.getApp())//tag 数据走索引
                    .tag("resource", metric.getResource())//tag 数据走索引
                    .addField("gmtCreate", metric.getGmtCreate().getTime())
                    .addField("gmtModified", metric.getGmtModified().getTime())
                    .addField("timestamp", metric.getTimestamp().getTime())
                    .addField("passQps", metric.getPassQps())
                    .addField("successQps", metric.getSuccessQps())
                    .addField("blockQps", metric.getBlockQps())
                    .addField("exceptionQps", metric.getExceptionQps())
                    .addField("rt", metric.getRt())
                    .addField("count", metric.getCount())
                    .addField("resourceCode", metric.getResourceCode())
                    .build();
            batchPoints.point(point);
        });
        influxDB.write(batchPoints);
    }

    @Override
    public synchronized List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        String command = "SELECT * FROM sentinelInfo WHERE app='" + app + "' AND resource = '" + resource + "' AND gmtCreate>" + startTime + " AND gmtCreate<" + endTime;
        Query query = new Query(command);
        QueryResult queryResult = influxDB.query(query);
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<InfluxdbMetricEntity> influxResults = resultMapper.toPOJO(queryResult, InfluxdbMetricEntity.class);
        try {
            influxResults.forEach(entity -> {
                MetricEntity metric = new MetricEntity();
                BeanUtils.copyProperties(entity, metric);
                metric.setTimestamp(new Date(entity.getTimestamp()));
                metric.setGmtCreate(new Date(entity.getGmtCreate()));
                metric.setGmtModified(new Date(entity.getGmtModified()));
                results.add(metric);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public synchronized List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        //最近一分钟的指标(实时数据)
        final long minTimeMs = System.currentTimeMillis() - 1000 * 60;
        String command = "SELECT * FROM sentinelInfo WHERE app='" + app + "' AND gmtCreate>" + minTimeMs;
        Query query = new Query(command);
        QueryResult queryResult = influxDB.query(query);
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
        List<InfluxdbMetricEntity> influxResults = resultMapper.toPOJO(queryResult, InfluxdbMetricEntity.class);
        try {
            if (CollectionUtils.isEmpty(influxResults)) {
                return results;
            }
            Map<String, InfluxdbMetricEntity> resourceCount = new HashMap<>(32);
            for (InfluxdbMetricEntity metricEntity : influxResults) {
                String resource = metricEntity.getResource();
                if (resourceCount.containsKey(resource)) {
                    InfluxdbMetricEntity oldEntity = resourceCount.get(resource);
                    oldEntity.addPassQps(metricEntity.getPassQps());
                    oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                    oldEntity.addBlockQps(metricEntity.getBlockQps());
                    oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                    oldEntity.addCount(1);
                } else {
                    resourceCount.put(resource, InfluxdbMetricEntity.copyOf(metricEntity));
                }
            }
            //排序
            results = resourceCount.entrySet()
                    .stream()
                    .sorted((o1, o2) -> {
                        InfluxdbMetricEntity e1 = o1.getValue();
                        InfluxdbMetricEntity e2 = o2.getValue();
                        int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                        if (t != 0) {
                            return t;
                        }
                        return e2.getPassQps().compareTo(e1.getPassQps());
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}