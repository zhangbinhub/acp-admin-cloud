### route-server
路由服务

##### 一、说明
- 1、提供路由信息配置功能
- 2、提供路由动态更新接口，收到请求后，将路由信息序列化至redis，同时发送更新消息给网关
- 3、接口详情请在浏览器中访问 /doc.html 页面

##### 二、数据初始化
执行 route-server 模块下的 pers.acp.admin.route.nobuild.InitData.doInitAll() 单元测试