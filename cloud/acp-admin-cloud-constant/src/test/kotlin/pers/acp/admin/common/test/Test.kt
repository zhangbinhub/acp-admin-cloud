package pers.acp.admin.common.test

import kotlinx.coroutines.*
import pers.acp.admin.constant.RoleCode

/**
 * @author zhang by 12/08/2019
 * @since JDK 11
 */
fun main() = runBlocking {
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
        val ss = "[fsdadf2341243]"
        println(ss.replace(Regex("[\\[|\\]]"), ""))

        var totle = 0
        val start = System.currentTimeMillis()
        withContext(Dispatchers.IO) {
            for (index in 0..500000) {
                launch(Dispatchers.IO) {
                    delay(2000)
                    println("$index >>>>>>>> finished " + System.currentTimeMillis())
                    totle++
                }
            }
        }
        println("totle = $totle, time = ${System.currentTimeMillis()-start} ms")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}