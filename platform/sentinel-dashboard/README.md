## 说明

阿里巴巴提供的控制台只是用于演示 Sentinel 的基本能力和工作流程，并没有依赖生产环境中所必需的组件，比如持久化的后端数据库、可靠的配置中心等。目前 Sentinel 采用内存态的方式存储监控和规则数据，监控最长存储时间为 5 分钟，控制台重启后数据丢失。

## 改造

- sentinel-dashboard（控制台，收集数据）
- Nacos（存储流控规则）
- Influxdb（时序数据库，存储数据）
- Chronograf （展示控制台，显示数据并实现预警）

## 架构

![输入图片说明](https://images.gitee.com/uploads/images/2019/0929/210553_1686537f_87650.png "流控数据监控预警.png")


## Sentinel 控制台

![输入图片说明](https://images.gitee.com/uploads/images/2019/0924/202051_71b85819_87650.png "屏幕截图.png")

## 公益中心

Nacos：http://47.104.197.9:8848/nacos

账号：nacos 密码：nacos

sentinel-dashboard：http://118.190.247.102:8084

账号：admin 密码：6347097

## 可参考

[SpringBoot 2.0 + 阿里巴巴 Sentinel 动态限流实战](https://blog.52itstyle.vip/archives/4395/)

[SpringBoot 2.0 + Nacos + Sentinel 流控规则集中存储](https://blog.52itstyle.vip/archives/4433/)

[SpringBoot 2.0 + InfluxDB+ Sentinel 实时监控数据存储](https://blog.52itstyle.vip/archives/4460/)

[阿里巴巴 Sentinel + InfluxDB + Chronograf 实现监控大屏](https://blog.52itstyle.vip/archives/4496/)
