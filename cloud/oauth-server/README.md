### oauth-server
权限服务，系统核心服务，集成 oauth2

##### 一、说明
- 提供一整套应用、用户、机构、权限全方位配置管理
- 统一认证服务：token 存储于 Redis，user 及 client 信息可扩展配置
  
  |          url          |  描述                   |
  | --------------------- | ----------------------- | 
  | /oauth/authorize      | 申请授权，basic认证保护      |
  | /oauth/token          | 获取token的服务，url中没有client_id和client_secret的，走basic认证保护 |
  | /oauth/check_token    | 资源服务器用来校验token，basic认证保护 |
  | /oauth/confirm_access | 授权确认，basic认证保护  |
  | /oauth/error          | 认证失败，无认证保护     |
  
  [查看认证过程](../../doc/oauth2.0认证.md)

##### 二、数据初始化
执行 oauth-server 模块下的 pers.acp.admin.oauth.nobuild.InitData.doInitAll() 单元测试

##### 三、接口功能
提供生成token、验证token、应用管理、机构管理、参数管理、角色管理、用户管理、菜单管理、权限功能管理等接口，接口详情请在浏览器中访问 /doc.html 页面