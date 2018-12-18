package pers.acp.admin.oauth.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 16:59
 * @since JDK 11
 */
@Entity
@Table(name = "t_menu")
public class Menu {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOpentype() {
        return opentype;
    }

    public void setOpentype(int opentype) {
        this.opentype = opentype;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getIconColor() {
        return iconColor;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public int getDialogW() {
        return dialogW;
    }

    public void setDialogW(int dialogW) {
        this.dialogW = dialogW;
    }

    public int getDialogH() {
        return dialogH;
    }

    public void setDialogH(int dialogH) {
        this.dialogH = dialogH;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    private String id;

    @Column(length = 36, nullable = false)
    private String appid;

    @Column(nullable = false)
    private String name = "";

    private String iconClass;

    private String iconColor;

    private String path;

    @Column(length = 36)
    private String parentid;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int type = 1;

    @Column(nullable = false)
    private int opentype = 0;

    @Column
    private int dialogW;

    @Column
    private int dialogH;

    @Column(nullable = false)
    private int sort;

}
