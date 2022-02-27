package pers.acp.admin.log.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import pers.acp.admin.log.base.BaseLogEntity

import javax.persistence.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(
    name = "t_log_operate",
    uniqueConstraints = [UniqueConstraint(columnNames = ["logId", "requestTime"])],
    indexes = [Index(columnList = "requestTime")]
)
@org.hibernate.annotations.Table(appliesTo = "t_log_operate", comment = "操作日志")
@ApiModel("操作日志")
data class OperateLog(
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = ""
) : BaseLogEntity()
