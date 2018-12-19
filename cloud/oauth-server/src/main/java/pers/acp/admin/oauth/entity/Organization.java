package pers.acp.admin.oauth.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 16:39
 * @since JDK 11
 */
@Entity
@Table(name = "t_organization")
public class Organization {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name = "";

    @Column(length = 100, nullable = false)
    private String code = "";

    @Column(nullable = false)
    private int levels;

    @Column(length = 36, nullable = false)
    private String parentid = "";

    @Column(nullable = false)
    private int sort;

}
