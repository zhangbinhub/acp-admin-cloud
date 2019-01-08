package pers.acp.admin.oauth.po;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 08/01/2019
 * @since JDK 11
 */
public class UserParam {

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @NotBlank(message = "用户ID不能为空")
    private String id;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private String avatar;

}
