package pers.acp.admin.common.lock.instanse;

import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import pers.acp.admin.common.lock.DistributedLock;

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
public class RedisDistributedLock implements DistributedLock {

    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean getLock(String lockId, String clientId, long timeOut) {
        Object lock = redisTemplate.execute((RedisCallback<Object>) connection -> connection.execute("set",
                lockId.getBytes(),
                clientId.getBytes(),
                "NX".getBytes(),
                "PX".getBytes(),
                String.valueOf(timeOut).getBytes()));
        return lock != null;
    }

    @Override
    public void releaseLock(String lockId, String clientId) {
        //lua script
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        redisTemplate.execute((RedisCallback<Object>) connection -> connection.eval(script.getBytes(), ReturnType.INTEGER, 1, lockId.getBytes(), clientId.getBytes()));
    }

}
