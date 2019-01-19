package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 17/01/2019
 * @since JDK 11
 */
@ApiModel("机构配置参数")
public class OrganizationPO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    @ApiModelProperty("机构ID，更新时必填")
    private String id;

    @ApiModelProperty(value = "机构名称", required = true, position = 1)
    @NotBlank(message = "机构名称不能为空")
    private String name;

    @ApiModelProperty(value = "机构编码", position = 2)
    private String code = "";

    @ApiModelProperty(value = "上级机构ID", required = true, position = 3)
    @NotBlank(message = "上级机构ID不能为空")
    private String parentid;

    @ApiModelProperty(value = "序号", required = true, position = 4)
    private int sort;

    @ApiModelProperty(value = "关联用户ID", position = 5)
    private List<String> userIds = new ArrayList<>();

}
