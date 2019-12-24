package pers.acp.admin.log.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import pers.acp.admin.log.base.BaseLogEntity
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation

import javax.persistence.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_log_login",
        uniqueConstraints = [UniqueConstraint(columnNames = ["logId", "requestTime"])],
        indexes = [
            Index(columnList = "requestTime"),
            Index(columnList = "userId"),
            Index(columnList = "clientId,loginDate"),
            Index(columnList = "userId,clientId,loginDate")
        ])
@ApiModel("登录日志")
data class LoginLog(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty("登录日期")
        var loginDate: String = CommonTools.getDateTimeString(null, Calculation.DATE_FORMAT)
) : BaseLogEntity()