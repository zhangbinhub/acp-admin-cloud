package pers.acp.admin.common.lock

import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.spring.cloud.lock.DistributedLock

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
class RedisDistributedLock(private val redisTemplate: RedisTemplate<Any, Any>) : DistributedLock {

    /**
     * 获取分布式锁
     * 获取锁的过程不会阻塞；
     * 成功时，返回true，超时时间为redis中锁的有效时间，超过该时间则其他客户端可以获取锁；
     * 失败时，立马返回false
     * @param lockId   锁ID
     * @param clientId 客户端ID
     * @param timeOut  超时时间
     * @return true|false 是否成功获取锁
     */
    override fun getLock(lockId: String, clientId: String, timeOut: Long): Boolean {
        val lock = redisTemplate.execute { connection ->
            connection.execute("set",
                    lockId.toByteArray(),
                    clientId.toByteArray(),
                    "NX".toByteArray(),
                    "PX".toByteArray(),
                    timeOut.toString().toByteArray())
        }
        return lock != null
    }

    override fun releaseLock(lockId: String, clientId: String) {
        //lua script
        val script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end"
        redisTemplate.execute(RedisCallback<Any> { connection -> connection.eval(script.toByteArray(), ReturnType.INTEGER, 1, lockId.toByteArray(), clientId.toByteArray()) })
    }

}
