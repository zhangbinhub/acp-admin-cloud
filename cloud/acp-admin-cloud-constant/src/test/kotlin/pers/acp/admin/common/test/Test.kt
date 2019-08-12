package pers.acp.admin.common.test

import pers.acp.admin.constant.RoleCode
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.reflect

/**
 * @author zhang by 12/08/2019
 * @since JDK 11
 */
fun main(args: Array<String>) {
    try {
        val fields = RoleCode::class.java.declaredFields
        for (field in fields) {
            val code = field.get(RoleCode::class.java).toString()
            if (RoleCode.prefix != code && !code.contains(RoleCode::class.java.canonicalName)) {
                println(code)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}