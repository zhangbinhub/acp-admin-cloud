package pers.acp.admin.oauth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 17/01/2019
 * @since JDK 11
 */
@ApiModel("机构详细信息")
public class OrganizationVO {

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

    @ApiModelProperty("机构ID")
    private String id;

    @ApiModelProperty(value = "机构名称", position = 1)
    private String name;

    @ApiModelProperty(value = "机构编码", position = 2)
    private String code;

    @ApiModelProperty(value = "上级机构ID", position = 3)
    private String parentid;

    @ApiModelProperty(value = "序号", position = 4)
    private int sort;

    @ApiModelProperty(value = "关联用户ID", position = 5)
    private List<String> userIds = new ArrayList<>();

}
