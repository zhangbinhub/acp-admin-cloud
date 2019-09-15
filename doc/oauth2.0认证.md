# oauth2.0 认证
认证步骤：
- 获取 Access Token
- 请求时使用 Access Token
    - url 中增加 access_token="" 参数
    - head 中增加 Authorization="tokentype token"
## 一、Authorization Code
#### 说明
适用范围：此类型可用于有服务端的应用，是最贴近老版本的方式
#### 交互过程
##### 1. Client向Authorization Server发出申请（/oauth/authorize）
###### 请求类型：http/post application/x-www-form-urlencoded
    response_type = code
    client_id
    redirect_uri
    scope
    state
###### 响应：
    code
    state
##### 2. Client向Authorization Server发出申请（/oauth/token）
###### 请求类型：http/post application/x-www-form-urlencoded
    grant_type = authorization_code
    code
    client_id
    client_secret
    redirect_uri
###### 响应：
```json
{
  "access_token" : "",
  "token_type" : "",
  "expires_in" : 100,
  "refresh_token" : ""
}
```
## 二、Implicit Grant
#### 说明
适用范围：此类型可用于没有服务端的应用，比如Javascript应用
#### 交互过程
##### Client向Authorization Server发出申请（/oauth/authorize）
###### 请求类型：http/post application/x-www-form-urlencoded
    response_type = token
    client_id
    redirect_uri
    scope
    state
###### 响应：
```json
{
  "access_token" : "",
  "token_type" : "",
  "expires_in" : 100,
  "scope" : "",
  "state" : ""
}
```
## 三、Resource Owner Password Credentials
#### 说明
适用范围：不管有无服务端，此类型都可用
请求必须通过 http basic 进行验证（使用 client_id 和 client_secret）
#### 交互过程
##### Clien向Authorization Server发出申请（/oauth/token）
###### 请求类型：http/post application/x-www-form-urlencoded
    grant_type = password
    username
    password
    scope
###### 响应：
```json
{
  "access_token": "91c37cb7-1868-45c3-9edd-475a236f0c28",
  "token_type": "bearer",
  "expires_in": 119,
  "scope": "ALL",
  "refresh_token": "3748bdd7-198c-4902-8322-0172954e0631"
}
```
## 四、Client Credentials
#### 说明
适用范围：不管有无服务端，此类型都可用
#### 交互过程
##### Client向Authorization Server发出申请（/oauth/token）
###### 请求类型：http/post application/x-www-form-urlencoded
    grant_type = client_credentials
    client_id
    client_secret
    scope
###### 响应：
```json
{
  "access_token": "91c37cb7-1868-45c3-9edd-475a236f0c28",
  "token_type": "bearer",
  "expires_in": 119,
  "scope": "ALL"
}
```
## 五、用 Refresh Token 刷新有效的Access Token
请求必须通过 http basic 进行验证（使用 client_id 和 client_secret）
#### 交互过程
##### Client向Authorization Server发出申请（/oauth/token）
###### 请求类型：http/post application/x-www-form-urlencoded
    grant_type = refresh_token
    refresh_token
    client_id
    client_secret
    scope
###### 响应：
```json
{
  "access_token": "91c37cb7-1868-45c3-9edd-475a236f0c28",
  "token_type": "bearer",
  "expires_in": 119,
  "scope": "ALL",
  "refresh_token": "3748bdd7-198c-4902-8322-0172954e0631"
}
```
## 六、校验 Access Token
请求必须通过 http basic 进行验证（使用 client_id 和 client_secret）
#### 交互过程
##### Client向Authorization Server发出申请（/oauth/check_token）
###### 请求类型：http/post application/x-www-form-urlencoded
    token
###### 响应：
```json
{
  "scope": [
    "ALL"
  ],
  "active": true,
  "exp": 1523515998,
  "authorities": [
    "ROLE_ADMIN"
  ],
  "client_id": "test"
}
```
## 七、方法级安全配置
##### （一）开启注解
1、在资源服务器的入口类或配置类加上注解
```
@EnableGlobalMethodSecurity(prePostEnabled = true)
```
2、参数说明
- prePostEnabled : 确定 Spring Security 前置注释 [@PreAuthorize,@PostAuthorize,..] 是否应该启用；
- secureEnabled : 确定 Spring Security 安全注释 [@Secured] 是否应该启用；
- jsr250Enabled : 确定 JSR-250注释 [@RolesAllowed..] 是否应该启用；

3、具体注解说明
###### @Secured 
> @Secured注释是用来定义业务方法的安全性配置属性列表。您可以使用@Secured在方法上指定安全性要求[角色/权限等]，只有对应角色/权限的用户才可以调用这些方法。如果有人试图调用一个方法，但是不拥有所需的角色/权限，那会将会拒绝访问将引发异常。
> @Secured是从之前Spring版本中引入进来的。它有一个缺点(限制)就是不支持Spring EL表达式。
> 考虑下面的例子：
> ```java
> package com.yiibai.springsecurity.service;
> 
> import org.springframework.security.access.annotation.Secured;
> 
> public interface UserService {
> 
> 	List<User> findAllUsers();
> 
> 	@Secured("ROLE_ADMIN")
> 	void updateUser(User user);
> 
> 	@Secured({ "ROLE_DBA", "ROLE_ADMIN" })
> 	void deleteUser();
> 	
> }
> ```
###### @PreAuthorize / @PostAuthorize
> Spring 的 @PreAuthorize/@PostAuthorize 注解是首选应用到方法级安全性的方式，并支持Spring表达式语言，也提供基于表达式的访问控制。
> @PreAuthorize适合进入方法之前验证授权。 @PreAuthorize可以兼顾，角色/登录用户权限，参数传递给方法等等。
> @PostAuthorize 虽然不经常使用，检查授权方法之后才被执行，所以它适合用在对返回的值作验证授权。Spring EL提供可在表达式语言来访问并从方法返回 returnObject 对象来反映实际的对象。
> 请参见常见内置表达式了解支持表达式的完整列表。让我们回到之前的例子，这一次使用 @PreAuthorize/@PostAuthorize 。
> ```java
> package com.yiibai.springsecurity.service;
> 
> import org.springframework.security.access.prepost.PostAuthorize;
> import org.springframework.security.access.prepost.PreAuthorize;
> import com.yiibai.springsecurity.model.User;
> 
> public interface UserService {
> 
> 	List<User> findAllUsers();
> 
> 	@PostAuthorize ("returnObject.type == authentication.name")
> 	User findById(int id);
> 
> 	@PreAuthorize("hasRole('ADMIN')")
> 	void updateUser(User user);
> 	
> 	@PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
> 	void deleteUser(int id);
> 
> }
> ```
###### @PreAuthorize / @PostAuthorize 支持的内置表达式

|表达式|描述|
| ---- | ---- | 
| #oauth2.hasScope('requiredScope')|赋予scope为requiredScope的权限
| hasRole([role])| Returns true if the current principal has the specified role. By default if the supplied role does not start with 'ROLE_' it will be added. This can be customized by modifying the defaultRolePrefix on DefaultWebSecurityExpressionHandler.|
| hasAnyRole([role1,role2])| Returns true if the current principal has any of the supplied roles (given as a comma-separated list of strings). By default if the supplied role does not start with 'ROLE_' it will be added. This can be customized by modifying the defaultRolePrefix on DefaultWebSecurityExpressionHandler.|
| hasAuthority([authority])| Returns true if the current principal has the specified authority.|
| hasAnyAuthority([authority1,authority2])| Returns true if the current principal has any of the supplied roles (given as a comma-separated list of strings)|
| principal | Allows direct access to the principal object representing the current user|
| authentication | Allows direct access to the current Authentication object obtained from the SecurityContext|
| permitAll | Always evaluates to true|
| denyAll | Always evaluates to false|
| isAnonymous()| Returns true if the current principal is an anonymous user|
| isRememberMe()| Returns true if the current principal is a remember-me user|
| isAuthenticated()| Returns true if the user is not anonymous|
| isFullyAuthenticated()| Returns true if the user is not an anonymous or a remember-me user|
| hasPermission(Object target, Object permission)| Returns true if the user has access to the provided target for the given permission. For example, hasPermission(domainObject, 'read')|
| hasPermission(Object targetId, String targetType, Object permission)| Returns true if the user has access to the provided target for the given permission. For example, hasPermission(1, 'com.example.domain.Message', 'read')|