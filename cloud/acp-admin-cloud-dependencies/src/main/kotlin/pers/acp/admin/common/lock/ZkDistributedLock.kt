package pers.acp.admin.common.lock

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.locks.InterProcessMutex
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.lock.DistributedLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


/**
 * @author zhang by 30/09/2019
 * @since JDK 11
 */
class ZkDistributedLock(private val curatorFramework: CuratorFramework,
                        private val logAdapter: LogAdapter) : DistributedLock {
    override fun getLock(lockId: String, clientId: String, timeOut: Long): Boolean {
        val lock = InterProcessMutex(curatorFramework, "$distributedLockRootPath/$lockId")
        return try {
            val result = lock.acquire(timeOut, TimeUnit.MILLISECONDS)
            if (result) {
                distributedLockMap[clientId] = lock
            }
            return result
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            false
        }
    }

    override fun releaseLock(lockId: String, clientId: String) {
        try {
            distributedLockMap.remove(clientId)?.release()
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }
    }

    companion object {
        private const val distributedLockRootPath = "/_distribute_lock"
        private val distributedLockMap: ConcurrentHashMap<String, InterProcessMutex> = ConcurrentHashMap()
    }
}