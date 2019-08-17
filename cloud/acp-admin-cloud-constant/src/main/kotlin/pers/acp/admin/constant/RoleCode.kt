package pers.acp.admin.constant

/**
 * 角色编码
 * 新建角色时，需要向该接口中增加对应的编码
 * 系统中配置的角色编码不应包含前缀prefix
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object RoleCode {
    const val prefix = "ROLE_"
    /**
     * 新建角色时默认值
     */
    const val BUSINESS = "BUSINESS"

    /**
     * 超级管理员
     */
    const val SUPER = "SUPER"

    /**
     * 管理员
     */
    const val ADMIN = "ADMIN"

    /**
     * 测试人员
     */
    const val TEST = "TEST"
}
