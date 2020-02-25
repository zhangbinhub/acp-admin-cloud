package pers.acp.admin.workflow.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_my_process_instance", indexes = [
    Index(columnList = "userId,processInstanceId", unique = true),
    Index(columnList = "userId,processDefinitionKey,businessKey,startUserId,startTime"),
    Index(columnList = "userId,startTime"),
    Index(columnList = "userId,startUserId")
])
@ApiModel("我处理过的流程实例")
data class MyProcessInstance(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "流程实例id")
        var processInstanceId: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "流程定义键")
        var processDefinitionKey: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "业务键")
        var businessKey: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "发起人")
        var startUserId: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "处理人")
        var userId: String = "",

        @Column(nullable = false)
        @ApiModelProperty(value = "开始时间")
        var startTime: Long = 0
)
