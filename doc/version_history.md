## 版本更新记录
##### v4.2.0
> - Global
>   - [Upgrade] 升级 Gradle 至 6.7.1
>   - [Upgrade] gradle/dependencies.gradle 中移除 spring_boot、spring_cloud、alibaba_cloud，gradle.properties 中增加 springBootVersion、springCloudVersion、springCloudAlibabaVersion
>   - [Upgrade] 修改各模块build.gradle写法
>     - $versions.spring_boot → ${springBootVersion}
>     - $versions.spring_cloud → ${springCloudVersion}
>     - $versions.alibaba_cloud → ${springCloudAlibabaVersion}
>   - [Upgrade] logback 配置属性重命名
>     - logging.pattern.rolling-file-name → logging.logback.rollingpolicy.file-name-pattern
>     - logging.file.clean-history-on-start → logging.logback.rollingpolicy.clean-history-on-start
>     - logging.file.max-size → logging.logback.rollingpolicy.max-file-size
>     - logging.file.total-size-cap → logging.logback.rollingpolicy.total-size-cap
>     - logging.file.max-history → logging.logback.rollingpolicy.max-history
>   - [Upgrade] RedisTemplate 更换为 StringRedisTemplate
>   - [Upgrade] 升级依赖
>     - Acp 6.5.0
>     - kotlin 1.4.21
>     - Spring Boot 2.4.1
>     - Spring Cloud 2020.0.0
>     - kotlin coroutines 1.4.2
>     - jupiter 5.7.0
>     - junit-platform 1.7.0
>     - curator 5.1.0
>     - mysql 8.0.22
> - acp-admin-cloud-constant
>   - [Upgrade] 修改常量值
> - acp-admin-cloud-dependencies
>   - [Upgrade] 更新oauth客户端
>   - [Upgrade] 更新workflow客户端
>   - [Upgrade] 调整swagger代码，升级至springfox3.0
>   - [Upgrade] 修改bus事件对象参数名
>   - [Upgrade] ```feign.hystrix.FallbackFactory``` 替换为 ```org.springframework.cloud.openfeign.FallbackFactory
> - log-server
>   - [Upgrade] 路由日志消息消费，改为函数式风格
> - oauth-server
>   - [Upgrade] 优化根据机构号查询机构列表接口，使用模糊查询
>   - [Upgrade] 修改登录密码验证逻辑，时间粒度增加前后容错
>   - [Upgrade] 增加内部接口
>   - [Upgrade] /org-by-code/{code} 修改为 /org-by-code-or-name/{codeOrName}
>   - [Upgrade] 更换废弃的API，Arrays.min() → Arrays.minOrNull()
>   - [Upgrade] 优化单元测试代码
>   - [Upgrade] 增加oauth2自定义异常处理
>   - [Upgrade] 增加oauth2自定义认证方式
>   - [Upgrade] 优化用户查询相关方法名称
>   - [Upgrade] 调整oauth2相关client和user服务类
>   - [Upgrade] pers.acp.admin.oauth.nobuild.InitData中增加初始化运行参数
>   - [Upgrade] 去除自定义token store
>   - [Upgrade] 更新文档
>   - [Fix] 修复内部接口-获取机构列表
> - route-server
>   - [Upgrade] 发送路由更新消息，改为StreamBridge方式
> - gateway-server
>   - [Upgrade] 去除 ribbon 依赖
>   - [Upgrade] 发送路由日志消息，改为StreamBridge方式
>   - [Upgrade] 路由更新消息消费，改为函数式风格
> - deploy-server
>   - [Upgrade] 支持执行sql脚本
> - workflow-server
>   - [Upgrade] 增加内部接口
> - admin-server
>   - [Upgrade] 升级 Spring Boot Admin 2.3.1
>   - [Upgrade] 去除 ribbon 依赖
##### v4.1.7
> - Global
>   - [Upgrade] 升级 Gradle 至 6.6.1
>   - [Upgrade] server.tomcat.max-threads 修改为 server.tomcat.threads.max
>   - [Upgrade] 更新 atom-server-common-dev.yaml
>   - [Upgrade] 更新 nacos_config_export.zip
>   - [Upgrade] 增加 gradle/environment.gradle 环境变量定义
>   - [Upgrade] build.gradle 脚本中，增加环境变量替换
>   - [Upgrade] 修改启动脚本
>   - [Upgrade] 优化kafka消息消费，启动单独的线程来处理消息
>   - [Upgrade] 用线程池接收 kafka 消息，快速应答
> - acp-admin-cloud-dependencies
>   - [Upgrade] ZkDistributedLock 分布式锁支持同线程可重入
>   - [Upgrade] ExecuteBusEvent 的消息内容属性修改为 message
>   - [Upgrade] 修复 WorkFlowServer 客户端，pendingByUser方法返回无权限的异常
>   - [Upgrade] 修改 CommonOauthServerHystrix 处理方式
>   - [Upgrade] 修改 WorkFlowServer 客户端，增加 terminationInner 方法
>   - [Upgrade] 去除 RedisDistributedLock
>   - [Upgrade] 升级依赖
>     - Acp 6.4.5
>     - kotlin 1.4.10
>     - Spring Boot 2.3.4.RELEASE
>     - Spring Cloud Hoxton.SR8
>     - Spring Cloud Alibaba 2.2.3.RELEASE
>     - jupiter 5.6.2
>     - junit platform 1.6.2
>     - kotlin coroutines 1.3.8
>     - curator 5.0.0
>     - mysql 8.0.21
> - log-server
>   - [Upgrade] 每个日志文件大小限制修改为100MB
> - oauth-server
>   - [Upgrade] 使用新的 Sha256 工具类
>   - [Upgrade] 根据用户名或登录号模糊查询时，字符串前后都匹配，且过滤禁用的用户
>   - [Upgrade] 增加通过登录号查询用户的接口
>   - [Upgrade] 角色配置、菜单配置、功能权限配置、机构配置，加强校验，提升系统安全性
>   - [Upgrade] 用户信息实体中，关联的实体改为 FetchType.LAZY
>   - [Upgrade] 增加通过编码获取机构信息接口
>   - [Upgrade] UserDomain.getUserInfoById 方法优化
>   - [Upgrade] SecurityTokenService 方法优化，null判断
>   - [Fix] 优化用户查询，去除重复
> - workflow-server
>   - [Upgrade] /workflow/history 接口限定只查询已结束的流程信息，未结束的流程实例通过 /workflow/instance 接口查询
>   - [Upgrade] 修改流程强制结束接口权限
>   - [Upgrade] 历史流程实例查询时，增加流程删除原因字段返回
>   - [Upgrade] 内部接口获取待办任务时，返回List
>   - [Upgrade] 流程发起人设置为流程变量startUserId
>   - [Upgrade] 优化当前处理人获取逻辑
>   - [Upgrade] 修改controller接口权限，流程实例相关的查询不受控制
>   - [Upgrade] 优化流程相关用户查询，anonymousUser视为空
>   - [Upgrade] 只有待办用户才能查询流程节点任务详细信息
>   - [Upgrade] 增加内部接口，强制结束流程
>   - [Upgrade] 修改流程图生成方法，仅高亮当前节点及流经的所有连线
> - admin-server
>   - [Upgrade] 升级 Spring Boot Admin 2.3.0
> - deploy-server
>   - [Add] 新增部署服务
>     - 1、提供部署文件上传、删除、下载、查询功能
>     - 2、提供部署任务增、删、改、查、立即执行、定时执行功能
>     - 3、部署任务执行时仅支持执行指定脚本文件
>     - 4、接口详情请在浏览器中访问 /doc.html 页面
##### v4.1.6
> - Global And All Server
>   - [Upgrade] 修改 gradle 脚本，release 任务根据参数 -Pactive 打包不同的环境配置
> - acp-admin-cloud-dependencies
>   - [Upgrade] 删除 Acp 的 jar 包，改为依赖 pers.acp.cloud:acp-spring-cloud-starter
>   - [Upgrade] 升级依赖
>     - Spring Boot 2.2.7.RELEASE
>     - Spring Boot Admin 2.2.3
>     - Acp 6.4.4
> - oauth-server
>   - [Upgrade] 优化超级管理员校验
>   - [Upgrade] 优化可编辑机构校验
>   - [Upgrade] 调整应用列表接口数据，返回所有应用列表
>   - [Upgrade] 调整角色列表接口数据，返回所有角色列表
>   - [Upgrade] entity 增加数据库表注释
> - route-server
>   - [Upgrade] entity 增加数据库表注释
> - workflow-server
>   - [Upgrade] entity 增加数据库表注释
> - log-server
>   - [Upgrade] entity 增加数据库表注释
>   - [Upgrade] 历史日志记录删除时，使用如下注解
>   ```
>   @Modifying(flushAutomatically = true, clearAutomatically = true)
>   ```
##### v4.1.5
> - Global And All Server
>   - [Upgrade] 升级 Gradle 至 6.3
>   - [Upgrade] 升级 Kotlin 至 1.3.72
>   - [Upgrade] 修改 nacos 命名空间id
>   - [Upgrade] feign 关闭 sentinel
>   - [Upgrade] sentinel 配置移入配置中心全局配置中
>   - [Upgrade] 注释 sentinel nacos 数据源配置，待 sentinel 支持 nacos 1.2+ 动态数据源时再开启
>   - [Upgrade] security.oauth2.resource.token-info-uri 指向 oauth-server 新的内部接口 /open/inner/check-token，同时去除 security.client.client-id 和 security.client.client-secret 配置
> - acp-admin-cloud-constant
>   - [Upgrade] OauthApi 增加 token 验证、获取可管理机构、获取所有机构（所属机构 and 管理机构）
>   - [Upgrade] 修改流程实例对象属性名
> - acp-admin-cloud-dependencies
>   - [Upgrade] 升级核心库至 6.4.3
>   - [Upgrade] 抽象序列号生成接口，方便扩展
>   - [Upgrade] 修改bus事件消息
>   - [Upgrade] BaseController 增加权限检查方法
>   - [Upgrade] QueryParam 增加分页查询参数校验
>   - [Upgrade] CommonOauthServer 中增加获取可管理机构、获取所有机构（所属机构 and 管理机构）
>   - [Upgrade] 升级依赖项
>     - Spring Boot 2.2.6.RELEASE
>     - Spring Cloud Hoxton.SR4
>     - Spring Cloud Alibaba 2.2.1.RELEASE
>     - okhttp 3.14.7
>     - Postgresql 42.2.11
>     - Kotlin Coroutines 1.3.5
>     - Netty 4.1.48.Final
>     - Jackson 2.10.3
>     - Zip4j 2.5.1
> - oauth-server 
>   - [Upgrade] 增加用户查询接口，修改接口文档
>   - [Upgrade] 增加用户权限检查接口
>   - [Upgrade] 增加内部调用接口，判断用户是否具有指定的功能权限
>   - [Upgrade] 增加内部调用接口，获取机构及其所有子机构列表、获取可管理机构、获取所有机构（所属机构 and 管理机构）
>   - [Upgrade] 增加内部调用接口，token 验证
> - workflow-server 
>   - [Upgrade] 增加内部调用接口，流程启动、流程处理、待办任务获取
>   - [Upgrade] 流程实例中增加当前处理人
>   - [Upgrade] 优化异常信息
>   - [Fix] 修复流程实例查询时变量获取为空
>   - [Fix] 修复分页查询
>   - [Fix] 我处理的流程信息表中，流程发起人字段修改为可空
> - log-server
>   - [Fix] 修复操作日志只记录 HttpStatus 200 的问题
> - 更新文档
##### v4.1.4
> - [Upgrade] workflow的feign客户端增加内部启动流程接口
> - [Upgrade] 修改sentinel熔断配置
> - [Upgrade] 日志服务配置类修改
> - [Upgrade] 工作流服务增加“我处理过的实例”查询接口，并且在任务处理过后，记录处理过的实例信息
> - [Upgrade] 配置中心增加公共配置 atom-server-common-${spring.profiles.active}.yaml
> - [Upgrade] gateway 增加nacos配置中心依赖
> - [Upgrade] 简化各服务配置，公共配置信息移入配置中心 atom-server-common-${spring.profiles.active}.yaml
##### v4.1.3
> - [Upgrade] dependencies 中增加公共总线事件对象
> - [Upgrade] 工作流查询参数增加流程定义key
> - [Upgrade] 自动配置类使用@Configuration(proxyBeanMethods=false)
> - [Upgrade] 升级核心库至 6.4.2
> - [Upgrade] 升级 Spring Cloud 至 Hoxton.SR2
> - [Upgrade] 升级 Gradle 至 6.2
> - [Upgrade] 修改logback-spring.xml配置
> - [Upgrade] 显示依赖 spring-boot-starter-validation
> - [Upgrade] 工作流查询参数增加业务键
> - [Upgrade] 工作流实例查询返回信息增加processInstanceId字段
> - [Fix] 修复偶尔提示 busJsonConverter 无法注册的问题
##### v4.1.2
> - [Upgrade] 升级 flowable 至 6.5.0
> - [Upgrade] Spring Boot Admin 升级至 2.2.2
> - [Upgrade] Spring Cloud Alibaba 升级至 2.2.0.RELEASE
> - [Upgrade] gateway 中启用 spring-cloud-loadbalancer
> - [Upgrade] Spring Boot Admin 中启用 spring-cloud-loadbalancer
> - [Upgrade] 优化工作流图片生成
> - [Upgrade] 路由增加元数据配置
##### v4.1.1
> - [Upgrade] 去除多余无用依赖
> - [Upgrade] 修改feign客户端写法
> - [Upgrade] 修改zk端口号
> - [Upgrade] 优化HTTP返回状态码
> - [Upgrade] 增加运行参数全量查询接口
> - [Upgrade] 优化菜单排序
> - [Upgrade] 优化用户列表查询，去除头像字段
> - [Upgrade] 优化jpa删除写法
> - [Upgrade] 机构信息增加区域字段（必填），编码修改为必填
> - [Upgrade] 工作流任务对象，增加 unClaimed、delegated、taskDefinitionKey 字段
> - [Upgrade] 修改用户查询接口，支持多个机构号、角色编码
> - [Upgrade] constant 增加工作流相关常量
> - [Upgrade] 优化工作流服务，返回对象中的用户信息均使用UserVo对象
> - [Upgrade] 优化工作流服务生成流程图，增加文档
> - [Upgrade] 修改 token 附加信息key值
> - [Upgrade] 升级核心库至6.4.1
> - [Upgrade] 升级 Spring Boot 至 2.2.4.RELEASE
> - [Upgrade] 升级 Gradle 至 6.1.1
> - [Upgrade] 升级依赖项
>   - netty to 4.1.45.Final
>   - okhttp to 3.14.6
>   - mysql to 8.0.19
>   - postgresql to 42.2.9
>   - slf4j to 1.7.30
>   - jackson to 2.10.2
>   - hikaricp to 3.4.2
##### v4.1.0
> - [Upgrade] 用户手机号放入token额外附加信息中
> - [Upgrade] oauth增加一系列用户查询接口
> - [Upgrade] dependency中的oauth客户端增加对应查询接口
> - [Upgrade] 规范API接口URL命名
> - [Upgrade] 升级核心库至6.4.0
> - [Upgrade] 升级 Spring Boot 至 2.2.2.RELEASE
> - [Upgrade] 升级 Spring Cloud 至 Hoxton.SR1
> - [Upgrade] 修改application.yaml中logging配置
> - [Upgrade] 去除application.yaml中对hibernate.dialect方言的显示配置
> - [Upgrade] MediaType.APPLICATION_JSON_UTF8_VALUE 替换为 MediaType.APPLICATION_JSON_VALUE
> - [Upgrade] 修改test依赖
> - [Upgrade] cloud中使用spring-cloud-loadbalancer替换ribbon
> - [Upgrade] cloud中修改feign相关配置
> - [Upgrade] gateway中logRequestFilter修改
> - [Upgrade] 升级依赖项
>   - jupiter to 5.5.2
>   - junit_platform to 1.5.2
>   - kotlin_coroutines to 1.3.3
>   - commons_codec to 1.13
>   - bouncycastle to 1.64
>   - jackson to 2.10.1
>   - zip4j to 2.2.8
>   - hikaricp to 3.4.1
>   - mssqljdbc to 7.4.1.jre8
>   - spring boot admin to 2.2.1
>   - flying_saucer to 9.1.19
>   - okhttp to 3.12.0
> - [Fix] 修改dependencies中AcpAdminFeignClientAutoConfiguration，修复feign偶尔无法找到loadbalancer的异常
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