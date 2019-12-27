## 版本更新记录
##### v4.x
> - [Upgrade] 用户手机号放入token额外附加信息中
> - [Upgrade] oauth增加一系列用户查询接口
> - [Upgrade] dependency中的oauth客户端增加对应查询接口
> - [Upgrade] 规范API接口URL命名
##### v4.0.7
> - [Upgrade] 优化菜单管理、模块管理、机构管理，删除时候校验下级是否存在的写法
> - [Upgrade] 修改@Api写法
> - [Upgrade] 完善WorkFlow服务
> - [Upgrade] 升级核心库至6.3.4
> - [Upgrade] 优化代码，去除冗余
##### v4.0.6
> - [Upgrade] 各服务增加feign配置
> - [Upgrade] 优化entity超类写法
> - [Upgrade] controller中update时校验id
> - [Upgrade] 删除多余代码
> - [Upgrade] 优化gradle脚本
> - [Upgrade] 更新初始化方法，规范菜单指向url
> - [Upgrade] 优化log-server代码，避免在集群模式下多实例重复执行历史数据迁移定时任务
> - [Upgrade] dependencies 增加 oauthServer 的 feign 客户端
> - [Upgrade] 升级核心库至 6.3.3
> - [Upgrade] 升级 Spring Boot 至 2.1.11.RELEASE
> - [Upgrade] 升级 kotlin 至 1.3.61
> - [Upgrade] 升级依赖项
>   - zip4j to 2.2.7
>   - sshd-sftp to 2.3.0
>   - freemarker to 2.3.29
>   - batik to 1.12
>   - poi to 4.1.1
##### v4.0.5
> - [Upgrade] 修改应用列表获取排序规则
> - [Upgrade] 调整所有 Entity 写法，统一使用 data class
> - [Upgrade] 修改 log-server 历史数据迁移策略，保留T-1和T日数据
> - [Upgrade] 优化 log-server 代码
> - [Upgrade] 升级核心库至 6.3.2
> - [Upgrade] 分页查询参数使用单独的po对象，分页参数使用Valid校验
##### v4.0.4
> - [Upgrade] acp-admin-cloud-dependencies 模块增加feign client，获取运行参数
> - [Upgrade] 去除模块功能编码校验
> - [Upgrade] 升级 spring-cloud-alibaba 至 2.1.1.RELEASE
> - [Upgrade] 更新 Spring Cloud 至 Greenwich.SR4
> - [Upgrade] 升级 kotlin 至 1.3.60
> - [Upgrade] 升级 gradle 至 6.0.1
> - [Upgrade] 升级核心库至 6.3.1
> - [Upgrade] 修改各模块中的 nacos 配置
> - [Upgrade] junit5 降级至 5.3.2
> - [Upgrade] log-server 增加数据库日志记录历史信息清理策略
> - [Upgrade] 更新文档
##### v4.0.3
> - [Upgrade] 基于Redis和Zookeeper的分布式锁实现增加注释，两者对于超时时间的作用完全不一样
> - [Upgrade] log-server 优化日志文件清理时间复杂度，频繁contains的list容器换为set
> - [Upgrade] hystrix最大线程数设置为1000
> - [Upgrade] 各服务增加spring-boot-configuration-processor依赖
> - [Upgrade] 修改feign熔断配置，启用sentinel
> - [Upgrade] sentinel动态规则源使用nacos
> - [Upgrade] 升级 Spring Boot 至 2.1.10.RELEASE
> - [Upgrade] 升级 gradle 至 6.0
> - [Upgrade] 升级核心库至 6.3.0
> - [Upgrade] 升级依赖项
>   - junit5 to 5.5.2
>   - joda time to 2.10.5
>   - commons-text to 1.8
>   - slf4j to 1.7.29
>   - jackson to 2.9.10
>   - zip4j to 2.2.4
>   - mysql to 8.0.18
>   - netty to 4.1.43.Final
> - [Fix] 修复分页查询参数校验
##### v4.0.2
> - [Upgrade] 优化 build.gradle 脚本，支持junit5
> - [Upgrade] 更新文档
> - [Upgrade] 优化 dependencies 模块 AcpAdminDistributedLockAutoConfiguration 分布式锁定义
> - [Upgrade] 优化日志记录迁移，分批处理避免内存溢出
> - [Upgrade] gateway-server 去除 hystrix，不进行熔断处理
> - [Upgrade] gateway-server 删除链路监控 zipkin 相关配置
> - [Upgrade] 日志文件下载请求不进行权限验证
> - [Upgrade] 日志文件下载时文件名Base64加密传输，且支持大文件断点续传
> - [Upgrade] 升级核心库至 6.2.2
> - [Upgrade] 升级 gradle 至 5.6.3
> - [Upgrade] 规范文件绝对路径写法，使用canonicalPath
> - [Upgrade] log-server 中对各服务的error日志进行单独存储
##### v4.0.1
> - [Upgrade] 升级核心库至 6.2.1
> - [Upgrade] 升级 Spring Boot 至 2.1.9.RELEASE
> - [Upgrade] 升级依赖项
>   - joda time to "2.10.4"
>   - kotlinx-coroutines-core to "1.3.2"
>   - Postgresql to "42.2.8"
##### v4.0.0
> - [Upgrade] 调整工程结构，无需依赖Acp核心库的模块放入common
> - [Upgrade] 去除 file-server 模块
> - [Upgrade] 取消热部署配置
> - [Upgrade] 调整BaseDomain中查询排序封装
> - [Upgrade] oauth-server 调整机构信息 entity，编码可为空
> - [Upgrade] oauth-server 应用信息表增加 scope、identify 字段，默认可为空
> - [Upgrade] oauth-server 中的 TokenStore 取消存储登录次数信息
> - [Upgrade] oauth-server 取消登录信息统计接口
> - [Upgrade] gateway-server 重写 gateway 路由请求、响应过滤器，路由日志消息
> - [Upgrade] gateway-server 默认路由增加 log-server 配置
> - [Upgrade] gateway-server 增加是否允许跨域配置项
> - [Upgrade] route-server 路由日志相关功能移入 log-server
> - [Upgrade] log-server 重写路由日志，定时任务将截止前一日的所有数据移入历史表
> - [Upgrade] log-server 增加接口调用操作日志记录，定时任务将截止前一日的所有数据移入历史表
> - [Upgrade] log-server 增加用户登录日志记录，登录用户信息取缓存 token 的详细信息，，定时任务将截止前一日的所有数据移入历史表
> - [Upgrade] log-server 增加登录信息统计接口
> - [Upgrade] log-server 增加操作日志查询、登录日志查询接口
> - [Upgrade] log-server 优化日志文件备份策略
> - [Upgrade] 升级 gradle 至 5.6.2
> - [Upgrade] 升级核心库至 6.2.0
> - [Upgrade] 升级 Spring Boot 至 2.1.8.RELEASE
> - [Upgrade] 更新 Spring Cloud 至 Greenwich.SR3
> - [Upgrade] 修改配置文件，服务注册发现中心和配置中心使用nacos
> - [Upgrade] 去除 eureka-server、config-server、config-refresh-server 模块
> - [Upgrade] Hystrix 熔断组件更换为 Sentinel
> - [Upgrade] zipkin-server 单独安装，修改 zipkin client 相关配置
> - [Upgrade] 分布式锁默认切换为zookeeper实现
> - [Upgrade] 升级依赖项
>   - netty to "4.1.39.Final"
>   - kotlinx-coroutines-core to "1.3.1"
>   - commons-codec to "1.12"
>   - slf4j to "1.7.28"
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