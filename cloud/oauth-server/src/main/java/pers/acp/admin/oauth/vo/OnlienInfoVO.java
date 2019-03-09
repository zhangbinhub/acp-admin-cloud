package pers.acp.admin.oauth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
@ApiModel("在线用户数统计")
public class OnlienInfoVO {

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @ApiModelProperty(value = "应用ID", position = 1)
    private String appid;

    @ApiModelProperty(value = "应用名称", position = 2)
    private String appname;

    @ApiModelProperty(value = "有效token数", position = 3)
    private long count;

}
