package pers.acp.admin.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@ApiModel("文件下载参数")
public class FileDownLoadPO {

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @ApiModelProperty("文件路径")
    @NotBlank(message = "路径不能为空")
    private String filePath;

}
