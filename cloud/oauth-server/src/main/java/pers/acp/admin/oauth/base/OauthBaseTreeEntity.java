package pers.acp.admin.oauth.base;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 20/01/2019
 * @since JDK 11
 */
@MappedSuperclass
public class OauthBaseTreeEntity<T extends OauthBaseTreeEntity> {

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    @Column(length = 36)
    @ApiModelProperty("上级菜单ID")
    private String parentid;

    @Transient
    @ApiModelProperty("子列表")
    private List<T> children = new ArrayList<>();

}
