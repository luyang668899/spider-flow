package org.spiderflow.core.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存管理器
 * 用于管理不同类型的缓存实现，支持本地缓存和分布式缓存的切换
 * @author Administrator
 */
@Component
public class CacheManager {

    /**
     * 缓存类型枚举
     */
    public enum CacheType {
        LOCAL,  // 本地缓存
        REDIS   // Redis分布式缓存
    }

    private LocalCache localCache;
    private Cache redisCache;
    private CacheType defaultCacheType = CacheType.LOCAL;

    public CacheManager() {
        // 初始化本地缓存，默认使用LRU淘汰策略，最大容量为1000
        this.localCache = new LocalCache(1000, LocalCache.EvictionPolicyType.LRU);
        // Redis缓存延迟初始化，只在实际使用时创建
        this.redisCache = null;
    }

    /**
     * 获取默认缓存实例
     * @return 默认缓存实例
     */
    public Cache getCache() {
        return getCache(defaultCacheType);
    }

    /**
     * 根据类型获取缓存实例
     * @param cacheType 缓存类型
     * @return 缓存实例
     */
    public Cache getCache(CacheType cacheType) {
        switch (cacheType) {
            case REDIS:
                if (redisCache == null) {
                    // 延迟初始化Redis缓存
                    try {
                        this.redisCache = new RedisCache();
                        System.out.println("Redis缓存初始化成功");
                    } catch (Exception e) {
                        // Redis连接失败时，打印日志并降级使用本地缓存
                        System.err.println("Redis缓存初始化失败，降级使用本地缓存: " + e.getMessage());
                        return localCache;
                    }
                }
                return redisCache;
            case LOCAL:
            default:
                return localCache;
        }
    }

    /**
     * 设置默认缓存类型
     * @param cacheType 默认缓存类型
     */
    public void setDefaultCacheType(CacheType cacheType) {
        this.defaultCacheType = cacheType;
    }

    /**
     * 获取默认缓存类型
     * @return 默认缓存类型
     */
    public CacheType getDefaultCacheType() {
        return defaultCacheType;
    }

    /**
     * 设置本地缓存的最大容量
     * @param maxSize 最大容量
     */
    public void setLocalCacheMaxSize(long maxSize) {
        this.localCache.setMaxSize(maxSize);
    }

    /**
     * 设置本地缓存的淘汰策略
     * @param policyType 淘汰策略类型
     */
    public void setLocalCachePolicyType(LocalCache.EvictionPolicyType policyType) {
        this.localCache.setPolicyType(policyType);
    }

    /**
     * 设置本地缓存的持久化器
     * @param cachePersister 持久化器
     */
    public void setLocalCachePersister(CachePersister cachePersister) {
        this.localCache.setCachePersister(cachePersister);
    }

    /**
     * 设置本地缓存是否自动持久化
     * @param autoPersist true表示自动持久化，false表示手动持久化
     */
    public void setLocalCacheAutoPersist(boolean autoPersist) {
        this.localCache.setAutoPersist(autoPersist);
    }

    /**
     * 加载本地缓存数据
     */
    public void loadLocalCache() {
        this.localCache.load();
    }

    /**
     * 持久化本地缓存数据
     */
    public void persistLocalCache() {
        this.localCache.persist();
    }

    /**
     * 获取本地缓存统计信息
     * @return 缓存统计对象
     */
    public CacheStatistics getLocalCacheStatistics() {
        return this.localCache.getStatistics();
    }

    /**
     * 重置本地缓存统计信息
     */
    public void resetLocalCacheStatistics() {
        this.localCache.resetStatistics();
    }

    // 以下是静态便捷方法，直接使用默认缓存

    /**
     * 设置永久缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    public static void set(String key, Object value) {
        INSTANCE.getCache().set(key, value);
    }

    /**
     * 设置带过期时间的缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public static void set(String key, Object value, long timeout, TimeUnit unit) {
        INSTANCE.getCache().set(key, value, timeout, unit);
    }

    /**
     * 获取缓存值
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期返回null
     */
    public static Object get(String key) {
        return INSTANCE.getCache().get(key);
    }

    /**
     * 获取缓存值，带默认值
     * @param key 缓存键
     * @param defaultValue 默认值
     * @return 缓存值，如果不存在或已过期返回默认值
     */
    public static Object getOrDefault(String key, Object defaultValue) {
        return INSTANCE.getCache().getOrDefault(key, defaultValue);
    }

    /**
     * 检查缓存是否存在
     * @param key 缓存键
     * @return true表示存在且未过期，false表示不存在或已过期
     */
    public static boolean containsKey(String key) {
        return INSTANCE.getCache().containsKey(key);
    }

    /**
     * 移除缓存
     * @param key 缓存键
     */
    public static void remove(String key) {
        INSTANCE.getCache().remove(key);
    }

    /**
     * 清除所有缓存
     */
    public static void clear() {
        INSTANCE.getCache().clear();
    }

    /**
     * 获取缓存大小
     * @return 缓存大小
     */
    public static int size() {
        return INSTANCE.getCache().size();
    }

    // 单例实例
    private static final CacheManager INSTANCE = new CacheManager();

    /**
     * 获取单例实例
     * @return 缓存管理器单例
     */
    public static CacheManager getInstance() {
        return INSTANCE;
    }
}