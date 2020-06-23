package pers.acp.admin.oauth.jpa

import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

/**
 * @author zhangbin by 28/04/2018 12:57
 * @since JDK 11
 */
@Entity
@Table(name = "table2")
data class TableTwo(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        var name: String = "",

        var value: Double = 0.00,

        @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], mappedBy = "two")
        var memberSet: MutableSet<MemberTwo> = mutableSetOf()
)