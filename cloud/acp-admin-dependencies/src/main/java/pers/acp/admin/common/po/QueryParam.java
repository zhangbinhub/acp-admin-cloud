package pers.acp.admin.common.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel(value = "分页查询参数", description = "非查询请求时可为空")
public class QueryParam {

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderCommond() {
        return orderCommond;
    }

    public void setOrderCommond(String orderCommond) {
        this.orderCommond = orderCommond;
    }

    @ApiModelProperty(value = "当前页号", required = true, position = 1)
    private int currPage;

    @ApiModelProperty(value = "每页记录数", required = true, position = 2)
    private int pageSize;

    @ApiModelProperty(value = "排序列名", required = true, position = 3)
    @NotBlank(message = "排序列名不能为空")
    private String orderName;

    @ApiModelProperty(value = "排序方式", allowableValues = "asc,desc", required = true, position = 4)
    @NotBlank(message = "排序方式不能为空")
    private String orderCommond;

}
