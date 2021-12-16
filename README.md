# acp-admin-cloud
###### v5.1.2 [版本更新日志](doc/version_history.md)
- 使用Application Construction Platform 应用构建平台作为脚手架
- 基于 Spring Cloud 的微服务版本，基于 Spring Boot 的单机版请查看[这里](https://github.com/zhangbinhub/acp-admin-standalone)
- 该项目是前后端分离架构中的“后端部分”。前端工程[v5.1.2](https://github.com/zhangbinhub/acp-admin)

## 相关组件版本
- [Spring Boot 2.6.1](https://projects.spring.io/spring-boot)
- [Acp 2021.0.0](https://github.com/zhangbinhub/acp)

## 技术栈
- flowable
- joda-time
- okhttp
- netty
- xstream
- hibernate
- jackson
- poi
- freemarker
- flying-saucer-pdf-itext5
- swagger2
- junit5
- Nacos
- Sentinel
- spring-cloud-alibaba
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
    - spring-cloud-gateway
    - spring-cloud-stream-binder-kafka
    - spring-cloud-openfeign
    - spring-cloud-bus-kafka

## 总体架构
![Architecture diagram](doc/images/总体架构.jpg)

#### 说明
> - 各服务在 **Nacos** 上进行注册，**gateway** 和其他各个服务通过 **Nacos** 发现和查找目标服务进行访问
> - 各服务将互相调用的断路信息通过 **admin server** 进行监控
> - **【依赖中间件 redis】 gateway server** 根据制定的策略路由到指定服务；路由定义从 **Redis** 获取，缓存至本地；基于动态路由配置可根据实际情况扩展实现灰度发布
> - **【依赖中间件 redis、kafka】 route server** 修改路由信息后更新至 **Redis** ，通过 **Kafka** 通知 **gateway server** 动态更新路由
> - **【依赖中间件 kafka】 oauth server** 修改应用配置和参数配置后通过 **Kafka** 推送 **Bus** 总线事件，广播通知所有 **oauth server** 更新缓存
> - **【依赖中间件 kafka】** 各深度定制开发的服务通过 **kafka** 发送日志消息，**log server** 从 **Kafka** 中消费消息并进行日志的统一记录
> - **【依赖中间件 kafka、logstash、elasticsearch】 log server** 不仅将日志信息记录在本地，还发送给 **elasticsearch** 进行汇总
> - **【依赖中间件 redis】** 包路径中包含 **Redis** 时，**oauth server** 将 **token** 信息持久化到 **Redis** 进行统一认证管理，否则仅持久化到内存
> - **【依赖中间件 zookeeper】** 分布式锁，实现 **io.github.zhangbinhub.acp.cloud.lock.DistributedLock** 接口，并注册为**Spring Bean**，包路径中包含 **curator-recipes** 时，默认配置一个基于 **zookeeper** 的分布式锁实现
> - 需要进行防重请求的 controller 方法上增加注解 **io.github.zhangbinhub.acp.cloud.annotation.AcpCloudDuplicateSubmission**，默认30秒过期
> - 前后端交互 **HttpStatus Code** 说明
> 
>     | HttpStatus | 描述 |
>     | --- | --- | 
>     | 200 | 请求成功 |
>     | 201 | 资源创建成功 |
>     | 400 | 业务错误 |
>     | 401 | token（登录）失效 |
>     | 403 | 权限不足 |
>     | 404 | 找不到资源 |
>     | 500 | 系统异常 |

## 一、环境要求
- jdk 11
- gradle 6.5+
- kotlin 1.5+

## 二、gradle 脚本配置及使用
### （一）配置文件
##### 1.[gradle/dependencies.gradle](gradle/dependencies.gradle)
定义外部依赖版本号

##### 2.[gradle/environment.gradle](gradle/environment.gradle)
编译时定义的环境变量

##### 3.[settings.gradle](settings.gradle)
定义项目/模块结构

##### 4.[project.properties](project.properties)
gradle全局参数：
- gradleVersion：gradle版本号
- group：对应打包时的最外层groupid，最终的groupid还会加上模块路径，例如`groupid.acp.core`
- version：版本号
- encoding：编译字符集
- mavenCentralUrl：maven中央仓库地址
- javaVersion：jdk版本号

##### 5.[build.gradle](build.gradle)
公共构建脚本

### （二）自定义任务
- clearPj 清理所有输出文件
- release 编译、打包并输出
  - 如需编译打包对应环境，命令中使用参数 active，例如
  ```
  gradlew project:release -Pactive=test
  ```
### （三）发布至maven仓库
##### 1、发布至本地仓库
- 执行 publishToMavenLocal 任务
##### 2、发布至私服
- 项目根路径下创建 gradle.properties 并添加如下参数（参数值根据实际情况修改）
```properties
mavenUploadUrlRelease=https://maven.com/repositories/releases/
mavenUploadUrlSnapshot=https://maven.com/repositories/snapshot/
mavenUserName=username
mavenPassword=password
```
- 执行 publish 任务
##### 3、发布至中央仓库
- 项目根路径下创建 gradle.properties 并添加如下参数（参数值根据实际情况修改）
```properties
mavenUploadUrlRelease=https://maven.com/repositories/releases/
mavenUploadUrlSnapshot=https://maven.com/repositories/snapshot/
mavenUserName=username
mavenPassword=password
signing.keyId=shortId
signing.password=keyPassword
signing.secretKeyRingFile=keyFile
```
- 执行 publish 任务

### （四）升级命令
```
    gradlew wrapper --gradle-version=7.2 --distribution-type=all
```

## 三、工程说明
- 工程全局默认使用 UTF-8 字符集
- gradle 目录下为相关配置文件
- common 目录下为基于 Spring Cloud 的基础组件模块，含 Spring Boot Admin 和 GateWay
- cloud 目录下为基于 Spring Cloud 的自定义组件模块
- dockerfiles 所使用的中间件[docker-compose-base.yaml](dockerfiles/docker-compose-base.yaml)
- [数据库表结构](doc/数据结构.docx)
- swagger url : /doc.html

## 四、启停 SpringBoot 应用
- [jvm 参考参数](doc/jvm-params.txt)
- [启停脚本模板(Linux)](doc/script/server.model)，根据实际情况修改第2行 APP_NAME 和第3行 JVM_PARAM 的值即可，和 SpringBoot 应用的 .jar 放在同一路径下
- [启停脚本(windows)](doc/script/server.bat)，根据实际情况修改第1行末尾需要执行的 jar 名称，和SpringBoot应用的 .jar 放在同一路径下
- windows：修改[server.bat](doc/script/server.bat)内相关参数后，直接运行即可
- Linux 命令：

|          命令         |           描述          |
| --------------------- | ----------------------- | 
| ./server.sh           | 查看可用参数            |
| ./server.sh status    | 查看系统运行状态        |
| ./server.sh start     | 启动应用                |
| ./server.sh stop      | 停止应用                |
| ./server.sh restart   | 重启应用                |

## 五、基础中间件环境搭建
基础中间件包括：redis、zookeeper、kafka、zipkin-server、zipkin-dependencies、kafka-manager、elasticsearch、kibana、logstash、zoonavigator-api、zoonavigator-web、prometheus、grafana、setup_grafana_datasource
> - 启动服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yaml up -d
> ```
> - 停止服务
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yaml stop
> ```
> - 停止并删除容器实例
> 
> 命令模式进入dockerfile目录，执行启动命令
> ```bash
> docker-compose -f docker-compose-base.yaml down
> ```
> - docker-compose 文件：[dockerfiles/docker-compose-base.yaml](dockerfiles/docker-compose-base.yaml)

##### 1. kafka-manager kafka队列监控
http://127.0.0.1:9000
![Architecture diagram](doc/images/kafka-manager.png)
##### 2. zoonavigator zookeeper监控
http://127.0.0.1:8004
![Architecture diagram](doc/images/zoonavigator.png)
##### 3. prometheus 通过从zipkin中收集的信息进行性能监控
http://127.0.0.1:9090
![Architecture diagram](doc/images/prometheus.png)
##### 4. kibana elasticsearch内容管理，进行统一日志检索
http://127.0.0.1:5601
![Architecture diagram](doc/images/kibana.png)

## 六、需独立部署的必要组件及监控
### （一）Nacos
> - 用作服务注册/发现中心，配置中心，详情请参考[官网](https://nacos.io/zh-cn/)
> - 独立部署，数据库仅支持 MySQL5.6或5.7
> - 控制台 http://ip:port/nacos
> - 使用时需导入初始化配置信息[doc/nacos_config_nacos_config_export.zip](doc/nacos_config_export.zip)
> - 可监控服务健康状况，管理服务优雅上下线。进行配置项的统一管理、维护、分发

### （三）Zipkin
> - 配合 Spring Cloud Sleuth 进行服务链路追踪分析、统计，Zipkin Server 需要自行安装部署，详情请参考[官网](https://zipkin.io/)
> - 使用方法：
>   - 添加依赖
>        ```groovy
>        implementation "org.springframework.cloud:spring-cloud-starter-sleuth"
>        implementation "org.springframework.cloud:spring-cloud-sleuth-zipkin"
>        ```
>   - bootstrap.yaml 增加配置
>        ```yaml
>        spring:
>          zipkin:
>            sender:
>              type: kafka #默认为http方式，建议使用kafka方式发送链路追踪信息
>        ```
>   - application-xxx.yaml 增加配置
>        ```yaml
>        spring:
>          sleuth:
>            sampler:
>              probability: 1 #样本采集率，默认0.1，测试环境可以配置为1，生产为了不影响系统能行建议维持默认
>        ```
>   - 进行如上配置之后，服务就会自行向kafka推送链路信息
> - 控制台 http://ip:port/zipkin
> - 可监控请求链路 traces、消耗时间、服务依赖关系图等

## 七、系统初始化
### （一）数据库
> - 执行 oauth-server 模块下的 pers.acp.admin.oauth.nobuild.InitData.doInitAll() 单元测试
> - 执行 route-server 模块下的 pers.acp.admin.route.nobuild.InitData.doInitAll() 单元测试
> - [数据库表结构](doc/数据结构.docx)

## 八、服务列表
### （一）[admin-server](common/admin-server/README.md)
可视化监控，监控服务状态
### （二）acp-admin-cloud-constant
> 全局静态常量定义
> 定义服务间内部访问restful路径
> 定义角色编码
> 定义功能权限编码
> 定义路由相关常量
> 定义token相关常量
### （三）[gateway-server](common/gateway-server/README.md)
> - 网关服务
> - 依赖，请查看[build.gradle](common/gateway-server/build.gradle)
> - 动态路由信息保存在 redis
### （四）acp-admin-cloud-dependencies
> - 组件依赖模块
> - controller、domain、feign hystrix、repository 等基类和接口定义
> - 分布式锁实现
> - 全局流水号生成实现
> - 自动配置相关组件
> - 依赖
>   - acp-spring-cloud-starter
>   - spring-boot-starter-data-jpa
>   - spring-boot-starter-data-redis
>   - curator-recipes
>   - cloud:acp-admin-cloud-constant
### （五）[deploy-server](common/deploy-server/README.md)
> - 发布服务
> - 依赖，请查看[build.gradle](common/deploy-server/build.gradle)
### （六）[log-server](cloud/log-server/README.md)
> - 统一日志服务
> - 依赖 
>   - cloud:acp-admin-cloud-dependencies
> - 通过 kafka 收集其余服务的日志信息，统一进行记录
> - 通过 kafka 收集网关消息，记录路由日志
> - 根据配置中心参数，压缩备份日志文件
> - 根据配置中心参数，记录操作日志和登录日志
> - 每日将路由日志、操作日志、登录日志迁移至历史库
> - 根据 oauth 服务中运行参数配置的策略，压缩备份日志文件
> - 提供备份的日志文件查询、下载接口，只有超级管理员有权限访问
### （七）[oauth-server](cloud/oauth-server/README.md)
> - 统一认证服务
> - 依赖 
>   - cloud:acp-admin-cloud-dependencies
> - 提供全套权限体系服务，包含客户端应用管理、用户管理、机构管理、角色管理、权限管理、token管理、运行参数管理等
### （八）[route-server](cloud/route-server/README.md)
> - 路由服务
> - 依赖 
>   - cloud:acp-admin-cloud-dependencies
> - 提供动态路由策略配置及刷新
### （九）[workflow-server](cloud/workflow-server/README.md)
> - 工作流引擎服务
> - 依赖 
>   - cloud:acp-admin-cloud-dependencies
> - 提供工作流相关接口服务
### （十）其他自定义服务
> - 依赖
>   - cloud:acp-admin-cloud-dependencies

## 九、[Sentinel 动态数据源配置](doc/sentinel.md)

## 十、[公共配置信息](doc/atom-server-common-dev.yaml)

## 十一、环境变量及启动参数
|    变量名    |     描述     | 默认值 |  说明  |
| ----------- | ----------- | ----- | ----- |
|acp_profile_active|激活的配置环境|dev|服务器部署时建议java启动命令加入参数 -Dacp_profile_active 或 --acp_profile_active；容器部署时指定环境变量即可
|acp_server_port|服务启动端口|0（随机端口）|服务器部署时建议java启动命令加入参数 -Dacp_server_port 或 --acp_server_port；容器部署时指定环境变量即可。服务不需要外部直接访问时，建议保持默认值。注：admin-server默认值：9099，gateway-server默认值：8771
|acp_log_path|日志路径|logs/${spring.application.name}|服务器部署时建议java启动命令加入参数 -Dacp_log_path 或 --acp_log_path；容器部署时指定环境变量即可
|acp_nacos_addr|nacos地址|127.0.0.1:8848|nacos服务地址，ip:port
|acp_nacos_username|nacos用户名|nacos|nacos用户名，默认nacos，可自行在nacos管理端添加用户
|acp_nacos_password|nacos密码|nacos|nacos用户密码，默认nacos，可自行在nacos管理端添加用户
|acp_nacos_namespace|nacos命名空间|acp-cloud-admin|nacos命名空间，默认nacos，可自行在nacos管理端配置
|acp_jvm_param|JVM启动参数|-server -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms256m -Xmx512m -Djava.library.path=./libs -Dfile.encoding=utf-8|该环境变量在容器部署时使用

## 十二、JVM启动参数
```shell
-server -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms256m -Xmx512m -Djava.library.path=./libs -Dfile.encoding=utf-8
```

## 十三、打包OCI镜像
```groovy
bootBuildImage {
    docker {
        host = "tcp://localhost:2375"
        tlsVerify = false
    }
    imageName = "${group}/${project.name}:${version}"
    environment = [
            "BP_JVM_VERSION"              : "11.*",
            "BPE_APPEND_JAVA_TOOL_OPTIONS": " -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Dfile.encoding=utf-8"
    ]
}
```