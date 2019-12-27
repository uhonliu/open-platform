## 微服务开放平台 3.0.0

#### 简介
搭建基于OAuth2的开放平台、为APP端、应用服务提供统一接口管控平台、为第三方合作伙伴的业务对接提供授信可控的技术对接平台
+ 分布式架构，Nacos(服务注册+配置中心)统一管理、Feign(RPC服务调用)
+ 统一API网关(参数验签、身份认证、接口鉴权、接口调试、接口限流、接口状态、接口外网访问)
+ 统一Oauth2认证协议

#### 管理后台
+ 后台默认账号:admin 123456
+ 后台测试账号:test 123456
+ SpringBootAdmin账号:sba 123456

#### 源码
+ <a target="_blank" href="https://github.com/uhonliu/open-platform">服务端源码</a>
+ <a target="_blank" href="https://github.com/uhonliu/open-admin-ui">后台UI源码</a>

#### 使用手册
<a target="_blank" href="https://gitee.com/uhon/open-platform/wikis/pages">使用手册</a>

#### 功能介绍
![功能介绍](/docs/功能介绍.png)

#### 代码结构
``` lua
open-platform
├── docs                               -- 文档及脚本
    ├── bin                            -- 执行脚本
    ├── config                         -- 公共配置,用于导入到nacos配置中心
    ├── sql                            -- sql文件
      ├── data                         -- 增量数据
├── docker                             -- Docker部署脚本
    ├── elk                            -- ELK部署脚本
    ├── influxdb                       -- InfluxDB部署脚本
    ├── monitor                        -- Grafana部署脚本
├── components                         -- 公共组件
    ├── common-core         -- 提供微服务相关依赖包、工具类、全局异常解析等
    ├── common-starter      -- SpringBoot自动配置扫描
    ├── java-sdk            -- 开放平台api集成SDK(完善中...)
    ├── tenant-starter      -- 多租户模块,多数据源自动切换(完善中...)
├── migration                          -- 多环境数据迁移-菜单、操作、路由、异构API
├── platform                           -- 平台服务
    ├── api-spring-server   -- API开放网关-基于SpringCloudGateway[port = 8888](推荐）
    ├── api-zuul-server     -- API开放网关-基于Zuul[port = 8888](功能完善）
    ├── base-client         -- 平台基础服务接口
    ├── base-server         -- 平台基础服务器[port=8233]
    ├── generator-server    -- 在线代码生成服务器[port = 5555]
    ├── sba-server          -- SpringBootAdmin监控服务[port = 8849]
    ├── sentinel-dashboard  -- Sentinel控制台服务[port = 8080]
    ├── uaa-admin-server    -- 平台用户认证服务器[port = 8211]
    ├── uaa-portal-server   -- 门户开发者认证服务器[port = 7211]
├── services                           -- 通用微服务
    ├── bpm-client          -- 工作流接口
    ├── bpm-server          -- 工作流服务器[port = 8255]
    ├── comment-server      -- 评价服务[port = 8311]
    ├── dingtalk-server     -- 钉钉服务[port = 8222]
    ├── file-server         -- 文件存储服务[port = 8225]
    ├── msg-client          -- 消息服务接口
    ├── msg-server          -- 消息服务器[port = 8266]
    ├── org-server          -- 组织架构服务[port = 8280]
    ├── payment-server      -- 支付服务[port = 8288]
    ├── sso-ui-demo         -- SSO单点登录演示demo[port = 2222]
    ├── task-client         -- 任务调度接口
    ├── task-server         -- 调度服务器[port = 8501]
    ├── tenant-demo         -- 多租户演示demo[port = 6878]
    ├── user-server         -- 用户服务[port = 8277]
```

#### 快速开始
本项目基于SpringCloud打造的分布式快速开发框架. 需要了解SpringCloud,SpringBoot,SpringSecurity,分布式原理。

1. 准备环境
    + Java1.8  (v1.8.0_131+)
    + Nacos服务注册和配置中心 (v1.0.0+) <a href="https://nacos.io/zh-cn/">阿里巴巴Nacos</a>
    + Sentinel流量控制、熔断降级、系统负载保护 (v1.6.0+) <a href="https://github.com/alibaba/Sentinel">阿里巴巴Sentinel</a>
    + Redis (v3.2.00+)
    + RabbitMq (v3.7+)（需安装rabbitmq_delayed_message_exchange插件 <a href="https://www.rabbitmq.com/community-plugins.html" target="_blank">下载地址</a>）
    + Mysql (v5.5.28+)
    + InfluxDB (v1.7.9+)
    + ELK (v7.4.1+)
    + Maven (v3+)
    + Nodejs (v10.14.2+)

2. 执行创建数据库open_platform并执行sql脚本
    + docs/sql/oauth2.sql
    + docs/sql/base.sql
    + docs/sql/gateway.sql
    + docs/sql/msg.sql
    + docs/sql/quartz.sql && scheduler.sql
    + docs/sql/org.sql

   执行创建数据库open_platform_user并执行sql脚本
    + docs/sql/user.sql

   执行创建数据库open_platform_payment并执行sql脚本
    + docs/sql/payment.sql

   执行创建数据库open_platform_comment并执行sql脚本
    + docs/sql/comment.sql

3. 启动nacos服务发现&配置中心,新建公共配置文件
    + 访问 http://localhost:8848/nacos/index.html 
    + 导入配置 /docs/config/DEFAULT_GROUP.zip（nacos1.0.3以上版本支持一键导入）
    + 新建配置文件  （nacos1.0.3以下版本）
        + 项目目录/docs/config/db.properties >  db.properties
        + 项目目录/docs/config/rabbitmq.properties > rabbitmq.properties
        + 项目目录/docs/config/redis.properties > redis.properties
        + 项目目录/docs/config/common.properties  > common.properties

    如图:
    ![nacos](https://gitee.com/uploads/images/2019/0425/231436_fce24434_791541.png "nacos.png")

4. 修改主pom.xml
    初始化maven项目
    ``` bash
    maven clean install
    ```
    本地启动,默认不用修改
    ``` xml
    <!--Nacos配置中心地址-->
    <config.server-addr>127.0.0.1:8848</config.server-addr>
    <!--Nacos配置中心命名空间,用于支持多环境.这里必须使用ID，不能使用名称,默认为空-->
    <config.namespace></config.namespace>
    <!--Nacos服务发现地址-->
    <discovery.server-addr>127.0.0.1:8848</discovery.server-addr>
    <!--Nacos服务发现命名空间,用于支持多环境.这里必须使用ID，不能使用名称,默认为空-->
    <discovery.namespace></discovery.namespace>
    ```

5. 本地启动(按顺序启动)
    1. [必需]BaseApplication(平台基础服务)
    2. [必需]UaaAdminApplication(平台用户认证服务器)
    3. [必需]GatewaySpringApplication(推荐)或GatewayZuulApplication
    ```
    访问 http://localhost:8888
    ```
    4.[非必需]SpringBootAdmin(监控服务器)(非必需)
    ```
    访问 http://localhost:8849
    ```

6. 前端启动
    ``` bash
    npm install
    npm run dev
    ```
    访问 http://localhost:8080

7. 项目打包部署
    + maven多环境打包,替换变量
    ``` bash
    mvn clean install package -P {dev|test|uat|online}
    ```
    + 项目启动
    ``` bash
    ./docs/bin/startup.sh {start|stop|restart|status} base-server.jar
    ./docs/bin/startup.sh {start|stop|restart|status} uaa-admin-server.jar
    ./docs/bin/startup.sh {start|stop|restart|status} api-spring-server.jar
    ```

8. docker部署
    + 配置DOCKER私服仓库
    + maven多环境打包,替换变量.并构建docker镜像
    ``` bash
    clean install package -P {dev|test|uat|online} dockerfile:build
    ```
    + 启动docker镜像
    ```bash
    docker run -d -e JAVA_OPTS="-Xms128m -Xmx768m" -p 8233:8233 --name base-server platform/base-server:3.0.0
    docker run -d -e JAVA_OPTS="-Xms128m -Xmx768m" -p 8211:8211 --name uaa-admin-server platform/uaa-admin-server:3.0.0
    docker run -d -e JAVA_OPTS="-Xms128m -Xmx768m" -p 8888:8888 --name api-spring-server platform/api-spring-server:3.0.0
    ```

#### 参考项目及文档
+ https://gitee.com/liuyadu/open-cloud
+ https://github.com/jmdhappy/xxpay-master
+ https://gitee.com/52itstyle/sentinel-dashboard
+ https://github.com/deviantony/docker-elk
+ https://github.com/alibaba/sentinel/wiki
+ https://github.com/alibaba/spring-cloud-alibaba
+ https://nacos.io/zh-cn
+ https://mybatis.plus