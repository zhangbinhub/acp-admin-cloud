package pers.acp.admin.log.constant

import java.io.File

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
object LogBackUp {
    val BACK_UP_PATH = File.separator + "backup"
    const val LOG_BACKUP_DISTRIBUTED_LOCK_KEY = "log_server_log_backup_distributed_lock_key"
    const val LOG_BACKUP_DISTRIBUTED_LOCK_TIME_OUT: Long = 1000
    const val ZIP_FILE_PREFIX = "log_"
    const val EXTENSION = ".zip"
}
