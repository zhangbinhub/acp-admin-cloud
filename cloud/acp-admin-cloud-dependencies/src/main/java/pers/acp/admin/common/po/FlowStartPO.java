package pers.acp.admin.common.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("启动流程参数")
public class FlowStartPO {

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @ApiModelProperty(value = "流程定义键", required = true, position = 1)
    @NotBlank(message = "流程定义键不能为空")
    private String processDefinitionKey;

    @ApiModelProperty(value = "业务键", required = true, position = 2)
    @NotBlank(message = "业务键不能为空")
    private String businessKey;

    @ApiModelProperty(value = "自定义参数", position = 3)
    private Map<String, Object> params = new HashMap<>();

}
