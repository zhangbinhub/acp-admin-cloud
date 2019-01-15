# acp-admin-cloud
###### v1.0.0 [版本更新日志](doc/version_history.md)
基于SpringCloud构建的一套后端系统。该项目是前后端分离架构中的“后端部分”。前端工程[请移步](https://github.com/zhangbin1010/acp-admin)
## 相关组件版本及官方文档
- [Spring Boot 2.1.2.RELEASE](https://projects.spring.io/spring-boot)
- [Spring Cloud Greenwich.RC2](http://projects.spring.io/spring-cloud)
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
- cloud 目录下为基于 Spring Cloud 的一整套组件模块
- gradle 目录下为相关配置文件
- test 目录下为测试工程
- swagger url : /swagger-ui.html

## 四、开发 SpringCloud 应用
引入 cloud 下相应模块包，demo 位于 cloud 下
### （一）模块说明
##### 1. cloud:acp-spring-cloud-starter-common
    原子服务公共模块：
    （1）自定义程序入口注解
    （2）oauth2.0 资源服务配置、客户端服务配置及远程单点认证机制
    （3）自定义 feign 并发策略、自定义 feign 请求拦截
    （4）hystrix 断路器
    （5）封装日志服务客户端，发送日志消息至 kafka
    （6）zipkin 链路追踪客户端
##### 2. cloud:admin-server 
###### 2.1 可视化监控，监控服务状态、信息聚合
|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 后台监控管理首页        |
| /hystrix              | 断路信息监控            |
###### 2.2 zipkin 链路追踪
- 服务端
> 从SpringCloud2.0 以后，官方已经不支持自定义服务，官方只提供编译好的jar包供用户使用。可以自行使用多种方式部署zipkin服务，并采用elasticsearch作为zipkin的数据存储器。
- 客户端
> - 依赖 cloud:acp-spring-cloud-starter-common
> - 增加 zipkin 相关配置
> ```yaml
> spring:
>   zipkin:
>     base-url: http://localhost:9411/
>   sleuth:
>     sampler:
>       probability: 1 #样本采集量，默认为0.1，为了测试这里修改为1，正式环境一般使用默认值。
> ```
##### 3. cloud:eureka-server 
服务注册发现

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /                     | 服务状态监控界面        |
##### 4. cloud:gateway-server 
网关服务
##### 5. cloud:oauth-server 
统一认证服务：token 存储于 Redis，user 及 client 信息可扩展配置

|          url          |  描述                   |
| --------------------- | ----------------------- | 
| /oauth/authorize      | 申请授权，basic认证保护      |
| /oauth/token          | 获取token的服务，url中没有client_id和client_secret的，走basic认证保护 |
| /oauth/check_token    | 资源服务器用来校验token，basic认证保护 |
| /oauth/confirm_access | 授权确认，basic认证保护  |
| /oauth/error          | 认证失败，无认证保护     |

[查看认证过程](doc/oauth2.0认证.md)

##### 6. cloud:log-server
日志服务，使用 kafka 作为日志消息队列
### （二）基础中间件环境搭建
基础中间件包括：redis、zookeeper、kafka、zoonavigator-api、zoonavigator-web、elasticsearch、zipkin、zipkin-dependencies、prometheus、grafana、setup_grafana_datasource
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
### （三）组件开发
##### 1. 可视化监控
    cloud:admin-server
    （1）无需改动代码
    （2）修改 yml 配置即可
##### 2. 服务注册发现（支持高可用eureka集群）
    cloud:eureka-server
    （1）无需改动代码
    （2）修改 yml 配置即可
##### 3. 统一配置管理
    需依赖 git 环境，如有需要参照网上教程
##### 4. 网关服务
    cloud:gateway-server
    （1）需自定义限流策略
    （2）修改 yml 进行路由配置
##### 5. 认证服务
    cloud:oauth-server
    （1）需定制 UserPasswordEncoder 用户密码编码器，配置进 WebSecurityConfiguration
    （2）需定制用户（信息、角色、权限）初始化和查询方式 SecurityUserDetailsService，配置进 AuthorizationServerConfiguration
    （3）需定制客户端（信息）初始化和查询方式 SecurityClientDetailsService，配置进 AuthorizationServerConfiguration
    （4）token 持久化方式为 Redis，配置在 AuthorizationServerConfiguration
##### 6. 日志服务
    （1）修改 yml kafka 相关配置
## 五、打包为 docker 镜像
自行编写 dockerfile，使用命令单独执行或使用 docker-compose 批量执行，请自行百度
## 六、系统初始化
> - 执行 InitData 单元测试
> - 执行时需将 pers.acp.admin.oauth.domain.SecurityClientDetailsService.init() 前的 @PostConstruct 注解暂时注释