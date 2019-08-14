package pers.acp.admin.common.test

import pers.acp.admin.constant.RoleCode

/**
 * @author zhang by 12/08/2019
 * @since JDK 11
 */
fun main() {
    try {
        val fields = RoleCode::class.java.declaredFields
        for (field in fields) {
            val value = field.get(RoleCode::class.java)
            if (value is String) {
                if (RoleCode.prefix != value) {
                    println(value)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}