### admin-server
监控服务，使用 Spring Boot Admin 通过 eureka 监控所有服务的健康状况

##### 一、说明
- 1、该服务不依赖任何自主开发的包，也不使用oauth2、bus、配置中心、日志服务等其他服务，网关路由不做配置，不暴露给前端
- 2、该服务有单独的安全验证，不加入到oauth2体系中，用户名和密码在[application.dev.yaml](src/main/resources/application-dev.yaml)中如下配置
    ```yaml
    spring:
      security:
        user:
          name: admin
          password: 888888
      cloud:
        nacos:
          discovery:
            metadata:
              user.name: ${spring.security.user.name}
              user.password: ${spring.security.user.password}
    ```