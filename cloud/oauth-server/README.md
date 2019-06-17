### oauth-server
权限服务，系统核心服务，集成 oauth2

##### 一、说明
- 1、从kafka接收日志消息，进行日志记录
- 2、定时清理历史日志文件，策略参数从配置中心获取
- 3、提供日志备份文件查询及下载接口
- 4、日志信息写入文件的同时，通过logstash发送给elasticsearch进行汇总

##### 二、数据初始化
执行 oauth-server 模块下的 pers.acp.admin.oauth.test.InitData.doInitAll() 单元测试

##### 三、接口功能
提供生成token、验证token、应用管理、机构管理、参数管理、角色管理、用户管理、菜单管理、权限功能管理等接口，接口详情请在浏览器中访问 /swagger-ui.html 页面