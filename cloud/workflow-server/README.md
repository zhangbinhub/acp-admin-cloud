# 工作流服务
集成[flowable](https://www.flowable.org)工作流引擎，详情请看[参考文档](https://www.flowable.org/docs/userguide/index.html)

### 流程编辑器
- 1、请前往flowable[官网](https://www.flowable.org)或[GitHub](https://github.com/flowable/flowable-engine/releases)下载并部署 flowable-modeler 至 tomcat9 并启动
- 2、或者下载docker镜像 flowable/all-in-one 并启动
```
docker run -p8080:8080 flowable/all-in-one
```
- 3、浏览器打开网址 http://localhost:8080/flowable-modeler 
- 4、登录用户名及密码：admin/test