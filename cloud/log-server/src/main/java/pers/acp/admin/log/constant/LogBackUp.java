package pers.acp.admin.log.constant;

import java.io.File;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
public interface LogBackUp {

    String BACK_UP_PATH = File.separator + "backup";

    String DATE_FORMAT = "yyyy-MM-dd";

    String ZIP_FILE_PREFIX = "log_";

    String EXTENSION = ".zip";
    
}
