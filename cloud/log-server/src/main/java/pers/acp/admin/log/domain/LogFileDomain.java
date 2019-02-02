package pers.acp.admin.log.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.log.conf.LogServerCustomerConfiguration;
import pers.acp.admin.log.constant.LogBackUp;
import pers.acp.springboot.core.component.FileDownLoadHandle;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class LogFileDomain {

    private final LogServerCustomerConfiguration logServerCustomerConfiguration;

    private final FileDownLoadHandle fileDownLoadHandle;

    @Autowired
    public LogFileDomain(LogServerCustomerConfiguration logServerCustomerConfiguration, FileDownLoadHandle fileDownLoadHandle) {
        this.logServerCustomerConfiguration = logServerCustomerConfiguration;
        this.fileDownLoadHandle = fileDownLoadHandle;
    }

    private void validateFold(File fold) throws ServerException {
        if (!fold.exists()) {
            if (!fold.mkdirs()) {
                throw new ServerException("备份路径不存在");
            }
        }
        if (!fold.isDirectory()) {
            throw new ServerException("路径 " + fold.getAbsolutePath() + " 不是文件夹");
        }
    }

    public List<String> fileList(String startDate, String endDate) throws ServerException {
        File fold = new File(logServerCustomerConfiguration.getLogFilePath() + LogBackUp.BACK_UP_PATH);
        validateFold(fold);
        List<String> fileList = new ArrayList<>();
        File[] files = fold.listFiles(pathname ->
                pathname.getName().compareTo(LogBackUp.ZIP_FILE_PREFIX + startDate + LogBackUp.EXTENSION) >= 0
                        && pathname.getName().compareTo(LogBackUp.ZIP_FILE_PREFIX + endDate + LogBackUp.EXTENSION) <= 0);
        if (files != null) {
            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        fileList.sort(Comparator.reverseOrder());
        return fileList;
    }

    public void doDownLoadFile(HttpServletRequest request, HttpServletResponse response, String fileName) throws ServerException {
        String foldPath = logServerCustomerConfiguration.getLogFilePath() + LogBackUp.BACK_UP_PATH.replace("\\", "/");
        File fold = new File(foldPath);
        validateFold(fold);
        String filePath = foldPath + File.separator + fileName;
        if (!new File(filePath).exists()) {
            throw new ServerException("文件[" + fileName + "]不存在");
        }
        fileDownLoadHandle.downLoadFile(request, response, filePath, false, Collections.singletonList(foldPath + "/.*"));
    }

}
