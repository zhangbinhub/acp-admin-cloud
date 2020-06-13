### workflow-server
工作流服务，集成[FlowAble v6.5.0](https://www.flowable.org)工作流引擎，详情请看[参考文档](https://www.flowable.org/docs/userguide/index.html)

##### 一、操作系统增加字体文件
###### （一）windows
无需添加，已自带字体
###### （二）linux
将宋体字体文件[simsun.ttc](fonts/simsun.ttc)复制到Linux服务器上的 $JAVA_HOME/lib/fonts 路径即可
```
 cd $JAVA_HOME/lib
 mkdir fonts
 cd $JAVA_HOME/lib/fonts
```

##### 二、流程编辑器
- 1、请前往flowable[官网](https://www.flowable.org)或[GitHub](https://github.com/flowable/flowable-engine/releases)下载并部署 flowable-modeler 至 tomcat9 并启动
- 2、或者下载docker镜像 flowable/all-in-one 并启动
```
docker run -p8080:8080 flowable/all-in-one
```
- 3、浏览器打开网址 http://localhost:8080/flowable-modeler 
- 4、登录用户名及密码：admin/test
- 5、编辑好一个流程之后，导出为 xxxx.bpmn20.xml 文件

##### 三、流程部署
- 1、流程更新重新部署后，只有新发起的流程会使用新流程，之前尚未结束的流程，仍然走老版本的流程
- 2、管理界面进行工作流部署，上传 xxxx.bpmn20.xml 文件

##### 四、接口说明
- 1、工作流服务目前已封装如下5个接口，接口详情请在浏览器中访问 /swagger-ui.html 页面
    - 启动流程
    - 获取用户待办任务列表
    - 流程处理
    - 流程处理历史查询
    - 生成流程图
- 2、需动态配置路由策略。

##### 五、固定必要的流程变量，自定义变量不能与之重复
- startUserId - 流程发起人
- businessKey - 业务键
- flowName - 流程名称
- title - 流程标题
- description - 流程描述
- pass - 处理结果：true-通过，false-不通过
- comment - 处理意见
- candidateUser:String ，节点任务候选人ID（一人或多人），多个候选人时使用“,”分隔；任务处理完成后需修改（变更为下一节点人或置空）
- assigneeUser:String ，节点任务处理人ID（只能一人）；任务处理完成后需修改（变更为下一节点人或置空）

##### 六、内置任务动态表单字段
- isTermination:Boolean ，当前任务节点是否可以进行终止操作（流程强制结束）
- isTransfer:Boolean ，当前任务是否可以转办（任务处理人变更，处理完毕后进入下一节点）
- isDelegate:Boolean ，当前任务是否可以委派他人办理（任务处理人变更，处理完毕后返回至当前处理人继续办理）
- selectUser:Boolean ，是否手动选择分配处理人（或候选人）
- orgLevel:Int ，待发送用户部门级别，负数|零|正数；0-当前用户所在部门，-1上一级部门，-2上两级部门...依次类推，1下一级部门，2下两级部门...依次类推，多个code时使用“,”分隔
- roleCode:String ，待发送用户所属角色code，多个code时使用“,”分隔
- taskCode:String ，任务编码，用于自定义判断任务处理方式
- isReject:Boolean ，当前任务是否允许驳回
- rejectToTask:String ，驳回至目标任务的定义ID，多个值时使用“,”分隔

##### 七、流程示例
- 流程图：
![流程图](../../doc/images/diagram.png)