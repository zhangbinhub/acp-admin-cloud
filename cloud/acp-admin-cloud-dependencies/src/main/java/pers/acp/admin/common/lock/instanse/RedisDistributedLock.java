package pers.acp.admin.common.lock.instanse;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReturnType;
import pers.acp.admin.common.lock.DistributedLock;

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
public class RedisDistributedLock implements DistributedLock {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisDistributedLock(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public boolean getLock(String lockId, String clientId, long timeOut) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        Object lock;
        try {
            lock = connection.execute("set",
                    lockId.getBytes(),
                    clientId.getBytes(),
                    "NX".getBytes(),
                    "PX".getBytes(),
                    String.valueOf(timeOut).getBytes());
        } finally {
            connection.close();
        }
        return lock != null;
    }

    @Override
    public void releaseLock(String lockId, String clientId) {
        //lua script
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisConnection connection = redisConnectionFactory.getConnection();
        try {
            connection.eval(script.getBytes(), ReturnType.INTEGER, 1, lockId.getBytes(), clientId.getBytes());
        } finally {
            connection.close();
        }
    }

}
