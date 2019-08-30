### gateway-server
网关服务

##### 一、说明
- 1、该服务不依赖任何自主开发的包，也不使用oauth2、bus、配置中心、日志服务等其他服务
- 2、固定路由配置定义于[bootstrap.yml](src/main/resources/bootstrap.yml)中
- 3、动态路由配置由[路由服务](../../cloud/route-server/README.md)进行管理
- 4、接收路由服务发送的“更新路由”消息，从redis中获取路由信息并进行动态更新