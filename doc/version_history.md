## 版本更新记录
##### v3.1.0
> - [Upgrade] 升级 gradle 至 5.6.2
> - [Upgrade] 升级核心库至 6.1.6
> - [Upgrade] gateway-server 增加是否允许跨域配置项
> - [Upgrade] 调整工程结构，无需依赖Acp核心库的模块放入common
> - [Upgrade] 调整BaseDomain中查询排序封装
> - [Upgrade] 调整机构信息 entity，编码可为空
> - [Upgrade] 升级 Spring Boot 至 2.1.8.RELEASE
> - [Upgrade] 升级依赖项
>   - netty to "4.1.39.Final"
> - [Upgrade] 去除 file-server 模块
> - [Upgrade] 应用信息表增加 scope 字段
##### v3.0.5
> - [Upgrade] 升级核心库至 6.1.4
> - [Upgrade] 角色编码可自定义输入
> - [Upgrade] 升级 kotlin 至 1.3.50
##### v3.0.4
> - [Upgrade] 优化 gradle 脚本，每个模块重新设置 group
> - [Upgrade] Config server 增加api文档配置
> - [Upgrade] 升级核心库至 6.1.3
> - [Upgrade] 优化日志服务清理策略
> - [Upgrade] 优化 gradle 脚本，build任务之后将打好的jar包放入项目根路径下的release目录
> - [Upgrade] 优化 gradle 脚本，resources文件一同放入release目录，并生成脚本server.sh
> - [Upgrade] 增加 acp-admin-cloud-constant 公共模块，公共的编码、权限等静态变量移入该模块
> - [Upgrade] 调整权限，除超级管理员外其余角色只能编辑当前应用下的角色及权限
> - [Fix] kotlin 反射获取属性时，内置对象也取出来的问题（获取角色编码、权限编码）
> - [Fix] 修改角色信息时，超级管理员无法添加其他应用角色的问题
> - [Fix] 重写 SecurityTokenStoreRedis 中 removeAccessToken 方法，解决从redis获取对象反序列化后再进行序列化字节码不一致导致lRem执行不成功的问题
##### v3.0.3
> - [Upgrade] 调整 gradle 脚本
> - [Upgrade] 升级核心库至 6.1.1
> - [Upgrade] 分布式锁接口及防重请求移入核心库，仅保留分布式锁实现
> - [Upgrade] 防重请求处理移入核心库
> - [Upgrade] 公共模块中增加基于Redis的全局序列号生成组件 **pers.acp.admin.common.serialnumber.GenerateSerialNumber**
##### v3.0.2
> - [Upgrade] 调整 gradle 脚本
> - [Upgrade] 升级核心库至 6.1.0
> - [Upgrade] 修改各服务配置，bus中的服务id和注册中心的instance-id进行统一
> - [Upgrade] 修改日志写法，使用日志适配器
> - [Upgrade] 优化日志服务 logback 配置
> - [Upgrade] 升级 flowable 至 6.4.2
> - [Upgrade] 优化代码
##### v3.0.1
> - [Upgrade] 升级 docker 中间件 elasticsearch 相关组件为 7.2.0
> - [Upgrade] 升级核心库至 6.0.1
> - [Upgrade] 更新压缩文件写法
##### v3.0.0
> - [Upgrade] 升级核心库至 6.0.0
> - [Upgrade] 升级 gradle 至 5.5.1
> - [Upgrade] 使用 kotlin 重写
> - [Upgrade] annotationProcessor 替换为 kapt
> - [Upgrade] 更新 Spring Boot Admin 至 2.1.6
> - [Upgrade] 数据库字段名称修改，entity严格使用驼峰命名
> - [Upgrade] 修改配置，所有服务的 actuator 端口不向注册中心开放，保证服务安全
> - [Upgrade] 增加 config-refresh-server 服务，仅用于向注册中心开放 actuator 端口，接收请求并向总线发送配置刷新事件
> - [Upgrade] 升级版本号至 3.0.0
> - [Upgrade] 更新文档
> - [Fix] 修复日志文件查询时，文件名不匹配导致查询结果异常的 bug
##### v2.0.1
> - [Upgrade] 升级核心库至 5.2.1
> - [Upgrade] 升级 gradle 至 5.5
> - [Upgrade] 升级版本号至 2.0.1
> - [Upgrade] 更新依赖项，Apache HttpClient 替换为 OKHttp
> - [Upgrade] 更新文档
##### v2.0.0
> - [Upgrade] 升级核心库至 5.2.0
> - [Upgrade] 升级 Spring Boot 至 2.1.6.RELEASE
> - [Upgrade] 更新 Spring Cloud 至 Greenwich.SR2
> - [Upgrade] 增加工作流服务，整合flowable工作流引擎
> - [Upgrade] 初始化数据中增加工作流相关权限配置
> - [Upgrade] 更新文档
> - [Upgrade] 升级依赖项
>   - Apache HttpClient to 4.5.9
>   - jackson to 2.9.9
##### v1.4.3
> - [Upgrade] 更新文档
> - [Upgrade] 升级 gradle 至 5.4.1
> - [Upgrade] 更新核心库至 5.1.5
> - [Upgrade] 升级 Spring Boot 至 2.1.5.RELEASE
> - [Upgrade] 升级 Spring Boot Admin 至 2.1.5
> - [Upgrade] 使用 joda-time 替换 jdk 日期时间及日历处理
> - [Upgrade] 更新路由信息的 binding 和 topic 写入自动配置
> - [Upgrade] 新建route-server模块，路由配置相关功能移入route-server
> - [Upgrade] 配置中心相关接口移入config-server
> - [Upgrade] 网关通过kafka发送消息给route-server模块，进行路由日志记录
> - [Upgrade] route-server模块增加路由日志查询接口
> - [Upgrade] 利用分布式锁增加防重请求
##### v1.4.2
> - [Upgrade] 升级 Spring Boot 至 2.1.4.RELEASE
> - [Upgrade] 更新核心库至 5.1.4.2
> - [Upgrade] 移动 acp-admin-cloud-dependencies 中部分代码至具体的服务中
> - [Upgrade] 增加 file-server，提供文件上传/下载接口
> - [Upgrade] gateway-server 默认路由中添加文件服务
> - [Upgrade] 升级 gradle 至 5.4
##### v1.4.1
> - [Upgrade] 更新 Spring Cloud 至 Greenwich.SR1
> - [Upgrade] 更新核心库至 5.1.3.2
> - [Upgrade] 升级 gradle 至 5.3.1
##### v1.4.0
> - [Upgrade] 升级gradle至5.3
> - [Upgrade] 优化gradle脚本
> - [Upgrade] 更新核心库至 5.1.3.1
> - [Upgrade] acp-admin-cloud-dependencies 增加基于Redis的分布式锁封装 pers.acp.admin.common.lock.instanse.RedisDistributedLock
> - [Upgrade] 网关服务增加动态路由配置
> - [Upgrade] 增加网关动态路由配置及刷新接口
> - [Upgrade] 配置中心取消 bus-kafka 依赖，广播刷新时配置中心不需要进行刷新；刷新接口调用 oauth2-server
> - [Upgrade] 修改应用信息和运行参数配置时，通过bus广播更新缓存事件进行刷新
> - [Upgrade] 优化日志服务，日志记录格式
> - [Upgrade] 自定义 RedisTokenStore，规范 Redis 写法，兼容 Redis 集群
> - [Fix] 优化日志备份，兼容分布式多实例
##### v1.3.0
> - [Upgrade] 更新核心库，增加资源服务器自定义token异常和权限异常响应
> - [Upgrade] json 转换及接口参数统一改为驼峰命名
> - [Upgrade] 扩展 token 信息，加入自定义用户信息
> - [Upgrade] 增加登录统计、在线用户统计、在线用户清理等接口
> - [Upgrade] 增加注销登录接口
##### v1.2.0
> - [Upgrade] 更新 gradle 脚本
> - [Upgrade] 规范 yml 配置文件
> - [Upgrade] 增加配置中心服务，配置信息持久化到数据库
> - [Upgrade] 日志服务从 oauth 服务获取运行时参数，改为从配置中心获取配置信息
> - [Upgrade] 更新文档
> - [Upgrade] 更新核心库，使用 netty 替换 mina
> - [Fix] 修复角色配置时角色级别控制
##### v1.1.0
> - 应用配置，进行增、删、改、更新密钥操作时候，在oauth中重新装载client信息，实时生效
> - 更新 SpringBoot 至 2.1.3.RELEASE
> - 更新核心库至 5.1.3
> - 更新依赖包版本
> - 更新 spring boot admin 至 2.1.3
> - 更新 eureka 相关配置
> - 集成 ELK 日志收集；集成链路分析服务 zipkin；集成各类监控服务；中间件均部署在docker，详细请看 docker-compose-base.yml 文件
> - oauth 服务 token 持久化到 redis
> - 启用日志服务，其余各服务通过 kafka 发送日志消息给日志服务
> - oauth 服务运行参数及app信息装载到内存，增、删、改时通过 kafka 广播通知所有 oauth 服务刷新内存
> - 日志服务增加定时任务，压缩备份前一天的所有日志文件
> - 日志服务压缩定时任务中增加清理历史文件，从 oauth 服务获取配置参数
> - 日志服务增加备份文件查询、下载接口
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