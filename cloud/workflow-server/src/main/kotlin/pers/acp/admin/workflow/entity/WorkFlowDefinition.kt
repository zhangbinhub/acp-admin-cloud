package pers.acp.admin.workflow.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_workflow_definition")
@ApiModel("工作流定义信息")
data class WorkFlowDefinition(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("应用ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty("资源文件名")
        var resourceName: String = "",

        @Column(nullable = false)
        @ApiModelProperty("流程定义键")
        var processKey: String = "",

        @Column(nullable = false)
        @ApiModelProperty("流程名称")
        var name: String = "",

        @Lob
        @ApiModelProperty("流程定义内容")
        var content: String = "",

        @Column(nullable = false)
        @ApiModelProperty("版本号")
        var version: Int = 0,

        @ApiModelProperty("备注")
        var remarks: String? = null,

        @Column(nullable = false)
        @ApiModelProperty("创建时间")
        var createTime: Long = System.currentTimeMillis(),

        @Column(nullable = false)
        @ApiModelProperty("最后更新时间")
        var modifyTime: Long = System.currentTimeMillis(),

        @ApiModelProperty("部署时间")
        var deployTime: Long? = null,

        @ApiModelProperty("部署ID")
        var deploymentId: String? = null
)