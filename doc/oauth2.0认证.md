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