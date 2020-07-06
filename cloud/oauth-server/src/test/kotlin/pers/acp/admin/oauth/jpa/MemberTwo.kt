package pers.acp.admin.oauth.jpa

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "table2_member")
data class MemberTwo(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        @Column(length = 100, nullable = false)
        @ApiModelProperty("用户登录号")
        var loginNo: String = "",

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "groupId")
        var two: TableTwo? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val groupMember = other as MemberTwo
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, groupMember.id)
                .append(loginNo, groupMember.loginNo)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(loginNo)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("MemberTwo(")
                    .append("id=$id")
                    .append(",loginNo=$loginNo")
                    .append(")")
                    .toString()
}