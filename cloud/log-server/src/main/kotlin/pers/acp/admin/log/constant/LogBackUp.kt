package pers.acp.admin.log.constant

import java.io.File

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
object LogBackUp {
    val BACK_UP_PATH = File.separator + "backup"
    const val ZIP_FILE_PREFIX = "log_"
    const val EXTENSION = ".zip"
}
