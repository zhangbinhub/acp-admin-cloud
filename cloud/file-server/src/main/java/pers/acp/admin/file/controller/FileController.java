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

    private String formatPath(String filePath) {
        if (CommonTools.isNullStr(filePath)) {
            filePath = FileParams.upLoadPath;
        }
        String path = filePath.replace("\\", File.separator).replace("/", File.separator);
        if (path.startsWith(File.separator)) {
            path = path.substring(File.separator.length());
        }
        if (path.lastIndexOf(File.separator) < (path.length() - File.separator.length())) {
            path += File.separator;
        }
        return path;
    }

    @ApiOperation(value = "文件上传")
    @ApiResponses({
            @ApiResponse(code = 400, message = "上传失败", response = ErrorVO.class)
    })
    @PostMapping(value = FileApi.upLoad, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> upLoad(@RequestParam("file") MultipartFile file,
                                         @ApiParam(value = "文件路径")
                                         @RequestParam(required = false) String filePath,
                                         @ApiParam(value = "上传文件名")
                                         @RequestParam(required = false) String destFileName) throws ServerException {
        String path = formatPath(filePath);
        String originFileName = file.getOriginalFilename();
        if (CommonTools.isNullStr(destFileName)) {
            destFileName = System.currentTimeMillis() + "_" + originFileName;
        }
        if (file.isEmpty()) {
            throw new ServerException("请选择上传文件");
        }
        if (doUpLoad(file, path, destFileName)) {
            InfoVO infoVO = new InfoVO();
            infoVO.setMessage(path.replace(File.separator, "/") + destFileName);
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
                                         @RequestParam(required = false) String filePath) {
        String path = formatPath(filePath);
        StringBuilder resultPath = new StringBuilder();
        int total = files.length;
        for (int i = 0; i < total; i++) {
            MultipartFile file = files[i];
            String originFileName = file.getOriginalFilename();
            String destFileName = System.currentTimeMillis() + "_" + originFileName;
            if (file.isEmpty()) {
                logInstance.error("文件为空【" + file.getOriginalFilename() + "】");
                break;
            }
            if (doUpLoad(file, path, destFileName)) {
                String result = path.replace(File.separator, "/") + destFileName;
                if (i == total - 1) {
                    resultPath.append(result);
                } else {
                    resultPath.append(result).append(",");
                }
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
        try {
            File destFile = new File(destFileLocation);
            File fold = destFile.getParentFile();
            if (!fold.exists()) {
                if (!fold.mkdirs()) {
                    throw new ServerException("创建路径失败：" + fold.getAbsolutePath());
                }
            }
            file.transferTo(destFile.getAbsoluteFile());
            return true;
        } catch (Exception ex) {
            logInstance.error(ex.getMessage(), ex);
            return false;
        }
    }

    @ApiOperation(value = "文件下载")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PostMapping(value = FileApi.downLoad, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> download(HttpServletRequest request, HttpServletResponse response,
                                           @RequestBody @Valid FileDownLoadPO fileDownLoadPO) throws ServerException {
        fileDownLoadDomain.doDownLoadFile(request, response, fileDownLoadPO.getFilePath());
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("下载文件成功：【" + fileDownLoadPO.getFilePath() + "】");
        return ResponseEntity.ok(infoVO);
    }

}
