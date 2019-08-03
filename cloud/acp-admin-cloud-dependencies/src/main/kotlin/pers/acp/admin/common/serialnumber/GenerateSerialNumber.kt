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

    /**
     * 生成序列号
     * @param keyString 序列号键名称
     * @param expirationTime 过期时间（重新计数超时时间），单位毫秒，默认24小时
     */
    @JvmOverloads
    fun getSerialNumber(keyString: String = CommonTools.getNowString(), expirationTime: Long = 86400000): Long =
            (redisTemplate.execute { connection ->
                connection.execute("incr", keyString.toByteArray())
            } as Long).also {
                if (it == 1L) {
                    GlobalScope.launch {
                        redisTemplate.execute { connection ->
                            connection.execute("pexpire",
                                    keyString.toByteArray(),
                                    expirationTime.toString().toByteArray())
                        }
                    }
                }
            }

}