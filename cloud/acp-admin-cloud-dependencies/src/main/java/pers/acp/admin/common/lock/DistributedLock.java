package pers.acp.admin.common.lock;

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
public interface DistributedLock {

    /**
     * 获取分布式锁
     *
     * @param lockId   锁ID
     * @param clientId 客户端ID
     * @param timeOut  锁超时时间，单位毫秒
     * @return true|false
     */
    boolean getLock(String lockId, String clientId, long timeOut);

    /**
     * 释放分布式锁
     *
     * @param lockId   锁ID
     * @param clientId 客户端ID
     */
    void releaseLock(String lockId, String clientId);

}
