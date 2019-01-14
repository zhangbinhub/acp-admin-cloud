package pers.acp.admin.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 14:56
 * @since JDK 11
 */
@Entity
@Table(name = "t_application")
@ApiModel("应用信息")
public class Application {

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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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

    public boolean isCovert() {
        return covert;
    }

    public void setCovert(boolean covert) {
        this.covert = covert;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    private String id;

    @ApiModelProperty("应用名称")
    @Column(nullable = false)
    private String appname;

    @ApiModelProperty("密钥")
    @Column(nullable = false)
    private String secret;

    @ApiModelProperty("token 有效期")
    @Column(nullable = false)
    private int accessTokenValiditySeconds = 86400;

    @ApiModelProperty("refresh token 有效期")
    @Column(nullable = false)
    private int refreshTokenValiditySeconds = 2592000;

    @ApiModelProperty("是否可删除")
    @Column(nullable = false)
    private boolean covert = true;

}
