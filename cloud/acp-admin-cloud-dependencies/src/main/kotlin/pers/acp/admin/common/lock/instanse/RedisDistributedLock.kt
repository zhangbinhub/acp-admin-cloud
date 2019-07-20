package pers.acp.admin.common.lock.instanse

import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.lock.DistributedLock

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
class RedisDistributedLock(private val redisTemplate: RedisTemplate<Any, Any>) : DistributedLock {

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
