package org.spiderflow.core.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存实现
 * 使用Jedis客户端操作Redis，支持所有缓存操作方法
 * @author Administrator
 */
public class RedisCache implements Cache {

    private final JedisPool jedisPool;

    /**
     * 默认构造函数，使用本地Redis实例
     */
    public RedisCache() {
        this("localhost", 6379, 0, null);
    }

    /**
     * 构造函数，支持自定义Redis配置
     * @param host Redis主机
     * @param port Redis端口
     * @param database Redis数据库
     * @param password Redis密码
     */
    public RedisCache(String host, int port, int database, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setTestOnBorrow(true);
        
        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(config, host, port, 2000, password, database);
        } else {
            this.jedisPool = new JedisPool(config, host, port, 2000, null, database);
        }
    }

    @Override
    public void set(String key, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value.toString());
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, (int) unit.toSeconds(timeout), value.toString());
        }
    }

    @Override
    public Object get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean containsKey(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    @Override
    public void remove(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        }
    }

    @Override
    public int size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.dbSize().intValue();
        }
    }

    /**
     * 关闭Redis连接池
     */
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}