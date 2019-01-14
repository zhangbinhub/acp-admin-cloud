package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
public class ApplicationPO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public int getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    @Id
    @ApiModelProperty("应用ID")
    private String id;

    @ApiModelProperty(value = "应用名称", required = true, position = 1)
    @NotBlank(message = "应用名称不能为空")
    private String appname;

    @ApiModelProperty(value = "token 有效期，单位秒", required = true, position = 2)
    private int accessTokenValiditySeconds = 86400;

    @ApiModelProperty(value = "refresh token 有效期，单位秒", required = true, position = 3)
    private int refreshTokenValiditySeconds = 2592000;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
