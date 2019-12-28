# Sentinel 动态数据源配置
## 一、[动态数据源](https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel)
SentinelProperties 内部提供了 TreeMap 类型的 datasource 属性用于配置数据源信息。
比如配置 4 个数据源：
```yaml
spring.cloud.sentinel.datasource.ds1.file.file=classpath: degraderule.json
spring.cloud.sentinel.datasource.ds1.file.rule-type=flow

#spring.cloud.sentinel.datasource.ds1.file.file=classpath: flowrule.json
#spring.cloud.sentinel.datasource.ds1.file.data-type=custom
#spring.cloud.sentinel.datasource.ds1.file.converter-class=com.alibaba.cloud.examples.JsonFlowRuleListConverter
#spring.cloud.sentinel.datasource.ds1.file.rule-type=flow

spring.cloud.sentinel.datasource.ds2.nacos.server-addr=localhost:8848
spring.cloud.sentinel.datasource.ds2.nacos.data-id=sentinel
spring.cloud.sentinel.datasource.ds2.nacos.group-id=DEFAULT_GROUP
spring.cloud.sentinel.datasource.ds2.nacos.data-type=json
spring.cloud.sentinel.datasource.ds2.nacos.rule-type=degrade

spring.cloud.sentinel.datasource.ds3.zk.path = /Sentinel-Demo/SYSTEM-CODE-DEMO-FLOW
spring.cloud.sentinel.datasource.ds3.zk.server-addr = localhost:2181
spring.cloud.sentinel.datasource.ds3.zk.rule-type=authority

spring.cloud.sentinel.datasource.ds4.apollo.namespace-name = application
spring.cloud.sentinel.datasource.ds4.apollo.flow-rules-key = sentinel
spring.cloud.sentinel.datasource.ds4.apollo.default-flow-rule-value = test
spring.cloud.sentinel.datasource.ds4.apollo.rule-type=param-flow
```
- 这种配置方式参考了 Spring Cloud Stream Binder 的配置，内部使用了 TreeMap 进行存储，comparator 为 String.CASE_INSENSITIVE_ORDER 。
- d1, ds2, ds3, ds4 是 ReadableDataSource 的名字，可随意编写。后面的 file ，zk ，nacos , apollo 就是对应具体的数据源。 它们后面的配置就是这些具体数据源各自的配置。
- rule-type 配置表示该数据源中的规则属于哪种类型的规则(flow，degrade，authority，system, param-flow, gw-flow, gw-api-group)。
- 默认情况下，xml 格式是不支持的。需要添加 jackson-dataformat-xml 依赖后才会自动生效。

## 二、[资源命名](https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel)
### （一）Feign 支持
- 配置文件打开 Sentinel 对 Feign 的支持：
    ```yaml
    feign.sentinel.enabled=true
    ```
- Feign 对应的接口中的资源名策略定义：httpmethod:protocol://requesturl。@FeignClient 注解中的所有属性，Sentinel 都做了兼容，例如：
    ```yaml
    GET:http://service-provider/echo/{str}
    ```
### （二）RestTemplate 支持
- httpmethod:schema://host:port/path：协议、主机、端口和路径
- httpmethod:schema://host:port：协议、主机和端口
- 以 https://www.taobao.com/test 这个 url 并使用 GET 方法为例。对应的资源名有两种粒度，分别是 GET:https://www.taobao.com 以及 GET:https://www.taobao.com/test

## 三、[规则配置](https://github.com/alibaba/Sentinel/wiki/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8)
### （一）流量控制规则
rule-type: flow

|Field|说明|默认值|
| ---- | ---- | ---- |
|resource|资源名，资源名是限流规则的作用对象	
|count|限流阈值
|grade|限流阈值类型，QPS 或线程数模式(0: thread count, 1: QPS)|QPS 模式
|limitApp|流控针对的调用来源|default，代表不区分调用来源
|strategy|调用关系限流策略：直接、链路、关联(0: 直接，1: 关联,2: 链路)|根据资源本身（直接）
|controlBehavior|流控效果（直接拒绝 / 排队等待 / 慢启动模式），不支持按调用关系限流(0. default(reject directly), 1. warm up, 2. rate limiter, 3. warm up + rate limiter)|直接拒绝
### （二）熔断降级规则
rule-type: degrade

|Field|说明|默认值|
| ---- | ---- | ---- |
|resource|资源名，即限流规则的作用对象	
|count|阈值
|grade|熔断策略，支持秒级 RT/秒级异常比例/分钟级异常数(0: average RT, 1: exception ratio, 2: exception count)
|timeWindow|降级的时间，单位为 s	
### （三）系统保护规则
rule-type: system

|Field|说明|默认值|
| ---- | ---- | ---- |
|highestSystemLoad|load1 阈值，参考值|-1 (不生效)
|avgRt|所有入口流量的平均响应时间|-1 (不生效)
|maxThread|入口流量的最大并发数|-1 (不生效)
|qps|所有入口资源的 QPS|-1 (不生效)
|highestCpuUsage|当前系统的 CPU 使用率（0.0-1.0）|-1 (不生效)
### （四）访问控制规则
rule-type: authority

|Field|说明|默认值|
| ---- | ---- | ---- |
|resource|资源名，即限流规则的作用对象
|limitApp|对应的黑名单/白名单，不同 origin 用 , 分隔，如 appA,appB
|strategy|限制模式，黑名单/白名单(0 for whitelist; 1 for blacklist)|白名单
### （五）热点规则
rule-type: param-flow

|Field|说明|默认值|
| ---- | ---- | ---- |
|resource|资源名，必填
|count|限流阈值，必填
|grade|限流模式(0: thread count, 1: QPS)|QPS 模式
|durationInSec|统计窗口时间长度（单位为秒），1.6.0 版本开始支持|1s
|controlBehavior|流控效果（支持快速失败和匀速排队模式），1.6.0 版本开始支持（0：CONTROL_BEHAVIOR_DEFAULT，1：CONTROL_BEHAVIOR_WARM_UP，2：CONTROL_BEHAVIOR_RATE_LIMITER，3：CONTROL_BEHAVIOR_WARM_UP_RATE_LIMITER）|快速失败
|maxQueueingTimeMs|最大排队等待时长（仅在匀速排队模式生效），1.6.0 版本开始支持|0ms
|paramIdx|热点参数的索引，必填，对应 SphU.entry(xxx, args) 中的参数索引位置
|paramFlowItemList|参数例外项，可以针对指定的参数值单独设置限流阈值，不受前面 count 阈值的限制。仅支持基本类型和字符串类型	
|clusterMode|是否是集群参数流控规则|false
|clusterConfig|集群流控相关配置

## 四、配置中心配置样例
```json
[{
    "resource": "GET:http://oauth2-server/inner/application",
    "grade": 0,
    "count": 60000,
    "timeWindow": 10
},{
    "resource": "GET:http://oauth2-server/inner/tokeninfo",
    "grade": 0,
    "count": 60000,
    "timeWindow": 10
}]
```