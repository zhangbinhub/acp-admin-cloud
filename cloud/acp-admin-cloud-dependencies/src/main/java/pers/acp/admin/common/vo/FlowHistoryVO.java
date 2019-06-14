package pers.acp.admin.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("流程历史记录")
public class FlowHistoryVO {

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getLocalParams() {
        return localParams;
    }

    public void setLocalParams(Map<String, Object> localParams) {
        this.localParams = localParams;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @ApiModelProperty(value = "流程实例id", position = 1)
    private String processInstanceId;

    @ApiModelProperty(value = "活动id", position = 2)
    private String activityId;

    @ApiModelProperty(value = "活动名称", position = 3)
    private String activityName;

    @ApiModelProperty(value = "任务id", position = 4)
    private String taskId;

    @ApiModelProperty(value = "执行实例id", position = 5)
    private String executionId;

    @ApiModelProperty(value = "业务键", position = 6)
    private String businessKey;

    @ApiModelProperty(value = "处理人id", position = 7)
    private String userId;

    @ApiModelProperty(value = "审批意见", position = 8)
    private String comment;

    @ApiModelProperty(value = "审批是否通过", position = 9)
    private boolean approved;

    @ApiModelProperty(value = "流程自定义参数", position = 10)
    private Map<String, Object> params = new HashMap<>();

    @ApiModelProperty(value = "任务自定义参数", position = 11)
    private Map<String, Object> localParams = new HashMap<>();

    @ApiModelProperty(value = "开始时间", position = 12)
    private long createTime;

    @ApiModelProperty(value = "结束时间", position = 13)
    private long endTime;

}
