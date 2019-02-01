# acp-admin-cloud
###### v1.1.0 [版本更新日志](doc/version_history.md)
基于SpringCloud构建的一套后端系统。该项目是前后端分离架构中的“后端部分”。前端工程[请移步](https://github.com/zhangbin1010/acp-admin)

## 相关组件版本及官方文档
- [Spring Boot 2.1.2.RELEASE](https://projects.spring.io/spring-boot)
- [Spring Cloud Greenwich.RELEASE](http://projects.spring.io/spring-cloud)

## 技术栈
- apache httpclient
- mina
- xstream
- hibernate
- jackson
- poi
- freemarker
- flying-saucer-pdf-itext5
- swagger2
- junit5
- spring-cloud
    - spring-boot
        - spring-aop
        - spring-data-jpa
        - spring-security
        - spring-security-oauth2
        - spring-data-redis
        - spring-boot-actuator
    - spring-data-redis-reactive
    - spring-boot-admin-server
    - spring-cloud-netflix-eureka-server
    - spring-cloud-netflix-eureka-client
    - spring-cloud-netflix-hystrix
    - spring-cloud-netflix-dashboard
    - spring-cloud-netflix-turbine
    - spring-cloud-gateway
    - spring-cloud-stream-binder-kafka
    - spring-cloud-openfeign
    - spring-cloud-sleuth-zipkin

## 总体架构
![Architecture diagram](doc/images/总体架构.jpg)

## 一、环境要求
- jdk 11
- gradle 5.1.1

## 二、gralde 配置及使用
### （一）配置文件
##### 1.gradle/dependencies.gradle
定义外部依赖版本号
    
##### 2.settings.gradle
定义项目/模块结构

##### 3.gradle.properties
gradle全局参数：
- gradleVersion：gradle版本号
- group：对应打包时的groupid
- version：工程版本号
- encoding：编译字符集
- mavenCentralUrl：maven中央仓库地址
- org.gradle.jvmargs：gradle执行时的jvm参数
- javaVersion：jdk版本号
    
##### 4.build.gradle
公共构建脚本
    
##### 5.模块根路径/build.gradle
单个模块特有的构建脚本

### （二）自定义任务
- clearPj 清理所有输出文件
- release 编译、打包并输出

### （三）升级命令
``
    gradlew wrapper --gradle-version=5.1.1 --distribution-type=all
``

## 三、工程说明
- doc目录下的files文件夹，当需要用到时放到打包后的jar同目录下即可
- 工程全局默认使用 UTF-8 字符集
- libs 下面为 [acp](https://github.com/zhangbin1010/acp/tree/release-5.1.2) 核心模块包
- cloud 目录下为基于 Spring Cloud 的一整套组件模块
- gradle 目录下为相关配置文件
- test 目录下为测试工程
- swagger url : /swagger-ui.html

## 四、启停 springboot 应用
- [jvm 参考参数](doc/jvm-params.txt)
- [启停脚本(Linux) server.sh](doc/script/server.sh)，根据实际情况修改第2行 APP_NAME 和第3行 JVM_PARAM 的值即可，和 SpringBoot 应用的 .jar 放在同一路径下
- [启停脚本(windows) server.bat](doc/script/server.bat)，根据实际情况修改第1行末尾需要执行的 jar 名称，和SpringBoot应用的 .jar 放在同一路径下
- Linux 命令：

|          命令         |           描述          |
| --------------------- | ----------------------- | 
| ./server.sh           | 查看可用参数            |
| ./server.sh status    | 查看系统运行状态        |
| ./server.sh start     | 启动应用                |
| ./server.sh stop      | 停止应用                |
| ./server.sh restart   | 重启应用                |

## 五、基础中间件环境搭建
基础中间件包括：redis、zookeeper、kafka、kafka-manager、elasticsearch、kibana、logstash、zipkin、zipkin-dependencies、zoonavigator-api、zoonavigator-web、prometheus、grafana、setup_grafana_datasource
> - 启动服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml up -d
> ```
> - 停止服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml stop
> ```
> - 停止并删除容器实例
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yml down
> ```
> - docker-compose 文件：cloud/dockerfile/docker-compose-base.yml
> - elasticsearch 的插件安装：
>    - docker exec -it [容器Id] /bin/sh
>    - elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.5.4/elasticsearch-analysis-ik-6.5.4.zip
>    - elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.5.4/elasticsearch-analysis-pinyin-6.5.4.zip
##### 1. zipkin
http://127.0.0.1:9411
![Architecture diagram](doc/images/zipkin.png)
##### 2. kafka-manager
http://127.0.0.1:9000
![Architecture diagram](doc/images/kafka-manager.png)
##### 3. zoonavigator
http://127.0.0.1:8004
![Architecture diagram](doc/images/zoonavigator.png)
##### 4. prometheus
http://127.0.0.1:9090
![Architecture diagram](doc/images/prometheus.png)
##### 5. kibana
http://127.0.0.1:5601
![Architecture diagram](doc/images/kibana.png)

## 六、系统初始化
### （一）数据库
> - 执行 oauth-server 模块下的 pers.acp.admin.oauth.test.InitData.doInitAll() 单元测试

## 六、服务列表
### （一）. admin-server 
###### 1 可视化监控，监控服务状态、信息聚合
|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 后台监控管理首页        |
| /hystrix              | 断路信息监控            |
###### 2 zipkin 链路追踪（需依赖 kafka）
- 服务端
> 从SpringCloud2.0 以后，官方已经不支持自定义服务，官方只提供编译好的jar包供用户使用。可以自行使用多种方式部署zipkin服务，并采用elasticsearch作为zipkin的数据存储器。
- 客户端
> - 依赖 cloud:acp-spring-cloud-starter-common
> - 增加 zipkin 相关配置
> ```yaml
> spring:
>   zipkin:
>     sender:
>       type: kafka
>   sleuth:
>     sampler:
>       probability: 1 #样本采集量，默认为0.1，为了测试这里修改为1，正式环境一般使用默认值。
> ```
### （二）eureka-server
服务注册发现

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 服务状态监控界面        |
> 服务注册发现（支持高可用eureka集群）
>（1）无需改动代码
>（2）修改 yml 配置即可
### （二）gateway-server
> 网关服务、修改 yml 进行路由配置
### （三）oauth-server
> - 统一认证服务
> - 提供全套权限体系接口