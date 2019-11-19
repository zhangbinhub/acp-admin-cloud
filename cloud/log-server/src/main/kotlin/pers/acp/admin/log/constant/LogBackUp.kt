package pers.acp.admin.log.constant

import java.io.File

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
object LogBackUp {
    val BACK_UP_PATH = File.separator + "backup"
    const val LOG_BACKUP_DISTRIBUTED_LOCK_KEY = "log_backup_distributed_lock_key"
    const val ZIP_FILE_PREFIX = "log_"
    const val EXTENSION = ".zip"
    /**
     * 登录统计最大月数
     */
    const val LOGIN_LOG_STATISTICS_MAX_MONTH = 3
}
