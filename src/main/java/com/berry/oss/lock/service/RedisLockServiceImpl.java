
package com.berry.oss.lock.service;

import com.berry.oss.lock.exception.LockExistsException;
import com.berry.oss.lock.exception.LockNotHeldException;
import com.berry.oss.lock.model.Lock;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Dave Syer
 */
@Service
public class RedisLockServiceImpl implements LockService {

    private static final String DEFAULT_LOCK_PREFIX = "oss.lock.";

    private String prefix = DEFAULT_LOCK_PREFIX;

    /**
     * 30 seconds
     */
    @Setter
    private long expiry = 30000;

    @Resource
    private StringRedisTemplate stringTemplate;

    /**
     * The prefix for all lock keys.
     *
     * @param prefix the prefix to set for all lock keys
     */
    public void setPrefix(String prefix) {
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }
        this.prefix = prefix;
    }

    @Override
    public Iterable<Lock> findAll() {
        Set<String> keys = stringTemplate.keys(prefix + "*");
        Set<Lock> locks = new LinkedHashSet<>();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Long expire = stringTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                Date expires = new Date(System.currentTimeMillis() + (expire == null ? 0L : expire));
                locks.add(new Lock(nameForKey(key), stringTemplate.opsForValue().get(key), expires));
            }
        }
        return locks;
    }

    @Override
    public Lock create(String name) {
        String stored = getValue(name);
        if (stored != null) {
            throw new LockExistsException();
        }
        String value = UUID.randomUUID().toString();
        String key = keyForName(name);
        // SET if Not exists
        Boolean setSuccess = stringTemplate.opsForValue().setIfAbsent(key, value);
        if (setSuccess == null || !setSuccess) {
            throw new LockExistsException();
        }
        stringTemplate.expire(key, expiry, TimeUnit.MILLISECONDS);
        Date expires = new Date(System.currentTimeMillis() + expiry);
        return new Lock(name, value, expires);
    }

    @Override
    public boolean release(String name, String value) {
        String stored = getValue(name);
        if (value.equals(stored)) {
            String key = keyForName(name);
            stringTemplate.delete(key);
            return true;
        }
        if (stored != null) {
            throw new LockNotHeldException();
        }
        return false;
    }

    @Override
    public Lock refresh(String name, String value) {
        String key = keyForName(name);
        String stored = getValue(name);
        if (value.equals(stored)) {
            Date expires = new Date(System.currentTimeMillis() + expiry);
            stringTemplate.expire(key, expiry, TimeUnit.MILLISECONDS);
            return new Lock(name, value, expires);
        }
        throw new LockNotHeldException();
    }

    private String getValue(String name) {
        String key = keyForName(name);
        return stringTemplate.opsForValue().get(key);
    }

    private String nameForKey(String key) {
        if (!key.startsWith(prefix)) {
            throw new IllegalStateException("Key (" + key + ") does not start with prefix (" + prefix + ")");
        }
        return key.substring(prefix.length());
    }

    private String keyForName(String name) {
        return prefix + name;
    }

}