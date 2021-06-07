package pers.acp.admin.common.serialnumber

import org.springframework.data.redis.core.StringRedisTemplate

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
class RedisGenerateSerialNumber(private val stringRedisTemplate: StringRedisTemplate) : GenerateSerialNumber {

    /**
     * 生成序列号
     * @param keyString 序列号键名称
     * @param expirationTime 过期时间（重新计数超时时间），单位毫秒，默认24小时
     */
    override fun getSerialNumber(keyString: String, expirationTime: Long): Long =
        (stringRedisTemplate.execute { connection ->
            connection.execute("incr", keyString.toByteArray())
        } as Long).also {
            if (it == 1L) {
                stringRedisTemplate.execute { connection ->
                    connection.execute(
                        "pexpire",
                        keyString.toByteArray(),
                        expirationTime.toString().toByteArray()
                    )
                }
            }
        }

}