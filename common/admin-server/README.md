### admin-server

监控服务，使用 Spring Boot Admin 通过 eureka 监控所有服务的健康状况

##### 一、说明

- 1、该服务不依赖任何自主开发的包，也不使用oauth2、bus、配置中心、日志服务等其他服务，网关路由不做配置，不暴露给前端
- 2、该服务有单独的安全验证，不加入到oauth2体系中，用户名和密码在[application-dev.yaml](src/main/resources/application-dev.yaml)中如下配置
    ```yaml
    spring:
      security:
        user:
          name: ${acp_admin_username:admin}
          password: ${acp_admin_password:888888}
      cloud:
        nacos:
          discovery:
            metadata:
              user.name: ${spring.security.user.name}
              user.password: ${spring.security.user.password}
    ```

#### 二、附加环境变量及启动参数

|    变量名    |     描述     | 默认值 |  说明  |
| ----------- | ----------- | ----- | ----- |
|acp_admin_username|用户名|admin|服务器部署时建议java启动命令加入参数 -Dacp_admin_username 或 --acp_admin_username；容器部署时指定环境变量即可
|acp_admin_password|密码|888888|服务器部署时建议java启动命令加入参数 -Dacp_admin_password 或 --acp_admin_password；容器部署时指定环境变量即可