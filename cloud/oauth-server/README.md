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

##### 四、运行参数

|名称|值|描述|备注|
| --------------------- | --------------------- | --------------------- | ----------------------- | 
|PASSWORD_COMPLEXITY_POLICY|0|密码复杂度策略；0：不限制，1：数字+字母，2：数字+字母+英文特殊符号`~!@#$%^&*()+=&#124;{}':;,\"[].<>|默认0|
|PASSWORD_UPDATE_INTERVAL_TIME|7776000000|修改密码间隔时间，单位：毫秒|密码过期之后，会要求强制修改密码；默认90天|

##### 五、自定义认证方式

- 1、新建
  AuthenticationToken，参考参考[UserPasswordAuthenticationToken](src/main/kotlin/pers/acp/admin/oauth/token/UserPasswordAuthenticationToken.kt)
- 2、新建认证 AuthenticationProvider，并在 WebSecurityConfiguration
  中进行配置，参考[UserPasswordAuthenticationProvider](src/main/kotlin/pers/acp/admin/oauth/token/granter/UserPasswordAuthenticationProvider.kt)
- 3、新建发布器 UserPasswordTokenGranter，设置自定义grantType，并在 AuthorizationServerConfiguration.getDefaultTokenGranters
  方法中进行配置，参考[UserPasswordTokenGranter](src/main/kotlin/pers/acp/admin/oauth/token/granter/UserPasswordTokenGranter.kt)
- 4、[SecurityClientDetailsService](src/main/kotlin/pers/acp/admin/oauth/security/SecurityClientDetailsService.kt)
  中将自定义grantType加入client中