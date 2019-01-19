package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("模块功能配置参数")
public class ModuleFuncPO {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "应用ID", required = true, position = 1)
    private String appid;

    @ApiModelProperty(value = "模块名称", required = true, position = 2)
    @NotBlank(message = "模块名称不能为空")
    private String name;

    @ApiModelProperty(value = "模块编码", required = true, position = 3)
    @NotBlank(message = "模块编码不能为空")
    private String code;

    @ApiModelProperty(value = "上级ID", required = true, position = 4)
    @NotBlank(message = "上级ID不能为空")
    private String parentid;

    @ApiModelProperty(value = "关联角色ID", position = 9)
    private List<String> roleIds = new ArrayList<>();

}
