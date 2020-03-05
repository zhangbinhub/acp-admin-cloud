package pers.acp.admin.common.serialnumber

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.data.redis.core.RedisTemplate

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
class RedisGenerateSerialNumber(private val redisTemplate: RedisTemplate<Any, Any>) : GenerateSerialNumber {

    /**
     * 生成序列号
     * @param keyString 序列号键名称
     * @param expirationTime 过期时间（重新计数超时时间），单位毫秒，默认24小时
     */
    override fun getSerialNumber(keyString: String, expirationTime: Long): Long =
            (redisTemplate.execute { connection ->
                connection.execute("incr", keyString.toByteArray())
            } as Long).also {
                if (it == 1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        redisTemplate.execute { connection ->
                            connection.execute("pexpire",
                                    keyString.toByteArray(),
                                    expirationTime.toString().toByteArray())
                        }
                    }
                }
            }

}