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

    /**
     * 获取分布式锁
     * 获取锁的过程会阻塞
     * 成功时，返回true
     * 失败时，一直阻塞直到成功获取锁，阻塞时间超过超时时间时返回false
     * @param lockId   锁ID
     * @param clientId 客户端ID
     * @param timeOut  超时时间
     * @param reentrant 是否可重入
     * @return true|false 是否成功获取锁
     */
    override fun getLock(lockId: String, clientId: String, timeOut: Long, reentrant: Boolean): Boolean {
        val key = "${lockId}_${clientId}_${Thread.currentThread().hashCode()}"
        if (reentrant) {
            distributedLockMap[key]?.let { lock ->
                if (lock.isOwnedByCurrentThread) {
                    lock
                } else {
                    InterProcessMutex(curatorFramework, "$distributedLockRootPath/$lockId")
                }
            } ?: InterProcessMutex(curatorFramework, "$distributedLockRootPath/$lockId")
        } else {
            InterProcessMutex(curatorFramework, "$distributedLockRootPath/$lockId")
        }.let { lock ->
            return try {
                val result = lock.acquire(timeOut, TimeUnit.MILLISECONDS)
                if (result) {
                    distributedLockMap[key] = lock
                }
                return result
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                false
            }
        }
    }

    override fun releaseLock(lockId: String, clientId: String) {
        val key = "${lockId}_${clientId}_${Thread.currentThread().hashCode()}"
        try {
            distributedLockMap[key]?.let { lock ->
                lock.release()
                if (!lock.isAcquiredInThisProcess) {
                    distributedLockMap.remove(key)
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }
    }

    companion object {
        private const val distributedLockRootPath = "/_distribute_lock"
        private val distributedLockMap: ConcurrentHashMap<String, InterProcessMutex> = ConcurrentHashMap()
    }
}