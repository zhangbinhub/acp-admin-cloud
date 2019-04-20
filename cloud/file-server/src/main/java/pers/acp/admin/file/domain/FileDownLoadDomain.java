package pers.acp.admin.file.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.springboot.core.component.FileDownLoadHandle;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collections;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class FileDownLoadDomain {

    private final FileDownLoadHandle fileDownLoadHandle;

    @Autowired
    public FileDownLoadDomain(FileDownLoadHandle fileDownLoadHandle) {
        this.fileDownLoadHandle = fileDownLoadHandle;
    }

    public void doDownLoadFile(HttpServletRequest request, HttpServletResponse response, String filePath) throws ServerException {
        String filePathFormat = filePath.replace("\\", "/");
        if (!new File(filePathFormat).exists()) {
            throw new ServerException("文件下载失败，文件[" + filePath + "]不存在");
        }
        fileDownLoadHandle.downLoadFile(request, response, filePath, false, Collections.singletonList(filePath));
    }

}
