package pers.acp.admin.workflow.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("启动审批参数")
public class FlowApprovePO {

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @ApiModelProperty(value = "任务id", required = true, position = 1)
    @NotBlank(message = "任务id不能为空")
    private String taskId;

    @ApiModelProperty(value = "审批结果", required = true, example = "true", position = 2)
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    @ApiModelProperty(value = "审批意见", required = true, example = "true", position = 3)
    private String comment;

    @ApiModelProperty(value = "自定义参数", position = 4)
    private Map<String, Object> params = new HashMap<>();

}
