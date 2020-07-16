package pers.acp.admin.deploy.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "t_deploy")
@ApiModel("部署任务")
data class DeployTask(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty("任务名称")
        var name: String = "",

        @Column(nullable = false)
        @ApiModelProperty("脚本文件")
        var scriptFile: String = "",

        @ApiModelProperty("执行的服务器IP正则表达式")
        var serverIpRegex: String? = null,

        @ApiModelProperty("备注")
        var remarks: String? = null,

        @Column(nullable = false)
        @ApiModelProperty("创建人账号")
        var createLoginNo: String = "",

        @Column(nullable = false)
        @ApiModelProperty("创建人名称")
        var createUserName: String = "",

        @Column(nullable = false)
        @ApiModelProperty("创建时间")
        var createTime: Long = 0,

        @ApiModelProperty("执行人账号")
        var execLoginNo: String? = null,

        @ApiModelProperty("执行人名称")
        var execUserName: String? = null,

        @ApiModelProperty("执行时间")
        var execTime: Long? = null
)