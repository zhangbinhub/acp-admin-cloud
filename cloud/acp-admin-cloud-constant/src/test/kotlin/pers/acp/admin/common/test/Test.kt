package pers.acp.admin.common.test

import kotlinx.coroutines.*
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
        val ss = "[fsdadf2341243]"
        println(ss.replace(Regex("[\\[|\\]]"), ""))

        runBlocking {
            GlobalScope.launch(Dispatchers.Unconfined) {
                delay(1000)
                print("1 >>>>>>>>> ")
                print(Thread.currentThread())
                println(" " + System.currentTimeMillis())
            }
            GlobalScope.launch(Dispatchers.Unconfined) {
                delay(1000)
                print("2 >>>>>>>>> ")
                print(Thread.currentThread())
                println(" " + System.currentTimeMillis())
            }
            GlobalScope.launch(Dispatchers.Unconfined) {
                delay(1000)
                print("3 >>>>>>>>> ")
                print(Thread.currentThread())
                println(" " + System.currentTimeMillis())
            }
        }
        println(4)
        Thread.sleep(5000)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}