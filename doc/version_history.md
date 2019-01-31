## 版本更新记录
##### v1.1.0
> - 应用配置，进行增、删、改、更新密钥操作时候，在oauth中重新装载client信息，实时生效
> - 更新 spring boot admin 至 2.1.2
> - 更新 eureka 相关配置
> - 集成 ELK 日志收集；集成链路分析服务 zipkin；集成各类监控服务；中间件均部署在docker，详细请看 docker-compose-base.yml 文件
> - oauth 服务 token 持久化到 redis
> - 启用日志服务，其余各服务通过 kafka 发送日志消息给日志服务
> - oauth 服务运行参数及app信息装载到内存，增、删、改时通过 kafka 广播通知所有 oauth 服务刷新内存
> - 日志服务增加定时任务，压缩备份前一天的所有日志文件
##### v1.0.0
> - 初始化项目，以 acp 核心库为框架进行搭建
> - 完成的服务：
>   - eureka-server 服务注册于发现中心
>   - gateway-server 统一网关服务
>   - oauth-server 权限管理配置服务
>       - 用户登录、权限验证
>       - 应用配置
>       - 角色配置
>       - 权限配置
>       - 用户配置
>       - 机构配置
>       - 运行参数配置