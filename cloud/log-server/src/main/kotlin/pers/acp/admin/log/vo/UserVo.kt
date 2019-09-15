package pers.acp.admin.log.vo

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
data class UserVo(
        var id: String = "",
        var name: String = "",
        var loginNo: String = "",
        var mobile: String = "",
        var levels: Int = 0,
        var enabled: Boolean = false,
        var sort: Int = 0
)
