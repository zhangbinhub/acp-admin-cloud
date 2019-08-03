package pers.acp.admin.common.serialnumber

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.core.CommonTools

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
class GenerateSerialNumber(private val redisTemplate: RedisTemplate<Any, Any>) {

    @JvmOverloads
    fun getSerialNumber(keyString: String = CommonTools.getNowString(), expirationTime: Long = 86400000): Long {
        val result = redisTemplate.execute { connection ->
            connection.execute("incr", keyString.toByteArray())
        } as Long
        if (result == 1L) {
            GlobalScope.launch {
                redisTemplate.execute { connection ->
                    connection.execute("pexpire",
                            keyString.toByteArray(),
                            expirationTime.toString().toByteArray())
                }
            }
        }
        return result
    }
}