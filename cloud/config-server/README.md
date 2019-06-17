### config-server
配置中心服务

##### 一、说明
- 1、配置信息持久化到数据库，其他服务来获取配置信息时，使用jdbc实时从数据库中查询，查询语句配置在[bootstrap.yml](src/main/resources/bootstrap.yml)中
- 2、该服务以下两个配置管理接口需要进行token权限验证，其余接口不做限制
    - /config
    - /config/**
- 3、该服务不使用bus，不收发总线事件
- 4、提供配置信息的维护接口，接口详情请在浏览器中访问 /swagger-ui.html 页面

##### 二、数据初始化
执行 config-server 模块下的 pers.acp.admin.config.test.InitData.doInitAll() 单元测试