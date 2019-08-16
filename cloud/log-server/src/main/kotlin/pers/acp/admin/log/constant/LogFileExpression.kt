package pers.acp.admin.log.constant

import pers.acp.admin.permission.BaseExpression

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
object LogFileExpression : BaseExpression() {
    const val superOnly = BaseExpression.superOnly
    const val sysConfig = BaseExpression.sysConfig
}
