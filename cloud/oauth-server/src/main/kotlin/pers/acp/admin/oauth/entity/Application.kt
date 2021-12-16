package pers.acp.admin.oauth.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import io.github.zhangbinhub.acp.core.CommonTools

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 14:56
 * @since JDK 11
 */
@Entity
@Table(name = "t_application")
@org.hibernate.annotations.Table(appliesTo = "t_application", comment = "应用信息")
@ApiModel("应用信息")
data class Application(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("应用ID")
        var id: String = "",

        @ApiModelProperty("应用名称")
        @Column(nullable = false)
        var appName: String = "",

        @ApiModelProperty("密钥")
        @Column(nullable = false)
        var secret: String = CommonTools.getUuid32(),

        @ApiModelProperty("权限范围，多个权限“,”分隔")
        var scope: String? = null,

        @ApiModelProperty("应用标识")
        var identify: String? = null,

        @ApiModelProperty("token 有效期")
        @Column(nullable = false)
        var accessTokenValiditySeconds: Int = 86400,

        @ApiModelProperty("refresh token 有效期")
        @Column(nullable = false)
        var refreshTokenValiditySeconds: Int = 2592000,

        @ApiModelProperty("是否可删除")
        @Column(nullable = false)
        var covert: Boolean = true
)