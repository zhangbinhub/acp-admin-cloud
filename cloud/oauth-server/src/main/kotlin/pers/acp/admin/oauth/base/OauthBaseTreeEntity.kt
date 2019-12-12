package pers.acp.admin.oauth.base

import io.swagger.annotations.ApiModelProperty

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.persistence.Transient

/**
 * @author zhang by 20/01/2019
 * @since JDK 11
 */
@MappedSuperclass
abstract class OauthBaseTreeEntity<T : OauthBaseTreeEntity<T>>(
        @Column(length = 36, nullable = false)
        @ApiModelProperty("上级ID")
        var parentId: String = "",
        @Transient
        @ApiModelProperty("子列表")
        var children: MutableList<T> = mutableListOf()
)