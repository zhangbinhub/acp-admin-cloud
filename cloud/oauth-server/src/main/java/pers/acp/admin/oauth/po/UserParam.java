package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 08/01/2019
 * @since JDK 11
 */
@ApiModel("更新当前用户信息参数")
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ApiModelProperty(value = "用户ID", required = true)
    @NotBlank(message = "用户ID不能为空")
    private String id;

    @ApiModelProperty(value = "名称", required = true, position = 1)
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty(value = "手机号", required = true, position = 2)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "头像图片base64数据", position = 3)
    private String avatar;

    @ApiModelProperty(value = "原密码", position = 4)
    private String oldPassword;

    @ApiModelProperty(value = "新密码", position = 5)
    private String password;

}
