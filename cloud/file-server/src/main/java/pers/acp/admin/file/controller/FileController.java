package pers.acp.admin.file.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.file.constant.FileApi;
import pers.acp.admin.file.constant.FileParams;
import pers.acp.admin.file.domain.FileDownLoadDomain;
import pers.acp.admin.file.po.FileDownLoadPO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;
import pers.acp.springcloud.common.log.LogInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.File;

/**
 * @author zhang by 16/04/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(FileApi.basePath)
@Api("文件信息")
public class FileController {

    private final LogInstance logInstance;

    private final FileDownLoadDomain fileDownLoadDomain;

    @Autowired
    public FileController(LogInstance logInstance, FileDownLoadDomain fileDownLoadDomain) {
        this.logInstance = logInstance;
        this.fileDownLoadDomain = fileDownLoadDomain;
    }

    @ApiOperation(value = "文件上传")
    @ApiResponses({
            @ApiResponse(code = 400, message = "上传失败", response = ErrorVO.class)
    })
    @PostMapping(value = FileApi.upLoad, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> upLoad(@RequestParam("file") MultipartFile file,
                                         @ApiParam(value = "文件路径")
                                         @RequestParam String filePath,
                                         @ApiParam(value = "上传文件名")
                                         @RequestParam String destFileName) throws ServerException {
        if (CommonTools.isNullStr(filePath)) {
            filePath = FileParams.upLoadPath;
        }
        String originFileName = file.getOriginalFilename();
        if (CommonTools.isNullStr(destFileName)) {
            destFileName = originFileName + "_" + System.currentTimeMillis();
        }
        if (file.isEmpty()) {
            throw new ServerException("请选择上传文件");
        }
        if (doUpLoad(file, filePath, destFileName)) {
            InfoVO infoVO = new InfoVO();
            infoVO.setMessage(filePath + destFileName);
            return ResponseEntity.ok(infoVO);
        } else {
            throw new ServerException("文件上传失败【" + originFileName + "】");
        }
    }

    @ApiOperation(value = "文件上传")
    @ApiResponses({
            @ApiResponse(code = 400, message = "上传失败", response = ErrorVO.class)
    })
    @PostMapping(value = FileApi.upLoads, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> upLoad(@RequestParam("files") MultipartFile[] files,
                                         @ApiParam(value = "文件路径")
                                         @RequestParam String filePath) {
        if (CommonTools.isNullStr(filePath)) {
            filePath = FileParams.upLoadPath;
        }
        StringBuilder resultPath = new StringBuilder();
        for (MultipartFile file : files) {
            String originFileName = file.getOriginalFilename();
            String destFileName = originFileName + "_" + System.currentTimeMillis();
            if (file.isEmpty()) {
                logInstance.error("文件为空【" + file.getOriginalFilename() + "】");
                break;
            }
            if (doUpLoad(file, filePath, destFileName)) {
                resultPath.append(filePath).append(destFileName);
            } else {
                logInstance.error("文件上传失败【" + originFileName + "】");
                break;
            }
        }
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage(resultPath.toString());
        return ResponseEntity.ok(infoVO);
    }

    private boolean doUpLoad(MultipartFile file, String filePath, String destFileName) {
        String destFileLocation = filePath + destFileName;
        File destFile = new File(destFileLocation);
        try {
            file.transferTo(destFile);
            return true;
        } catch (Exception ex) {
            logInstance.error(ex.getMessage(), ex);
            return false;
        }
    }

    @ApiOperation(value = "日志文件下载",
            notes = "下载指定的日志文件")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PostMapping(value = FileApi.downLoad + "/{fileName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> download(HttpServletRequest request, HttpServletResponse response,
                                           @ApiParam(value = "文件名称", required = true) @NotBlank(message = "文件名称不能为空")
                                           @RequestBody @Valid FileDownLoadPO fileDownLoadPO) throws ServerException {
        fileDownLoadDomain.doDownLoadFile(request, response, fileDownLoadPO.getFilePath());
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("下载文件成功：【" + fileDownLoadPO.getFilePath() + "】");
        return ResponseEntity.ok(infoVO);
    }

}
