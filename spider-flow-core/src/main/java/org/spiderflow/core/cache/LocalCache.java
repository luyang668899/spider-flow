package org.spiderflow.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存实现
 * 基于ConcurrentHashMap的内存缓存，支持过期时间、多种缓存淘汰策略和持久化
 * @author Administrator
 */
public class LocalCache implements Cache {

    /**
     * 缓存淘汰策略枚举
     */
    public enum EvictionPolicyType {
        NONE,   // 无淘汰策略，缓存大小无限制
        FIFO,   // 先进先出
        LRU,    // 最近最少使用
        LFU     // 最不经常使用
    }

    /**
     * 缓存项，包含值和过期时间
     */
    private static class CacheItem {
        private Object value;
        private long expireTime;

        public CacheItem(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return expireTime > 0 && System.currentTimeMillis() > expireTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }

    /**
     * 缓存存储
     */
    private final Map<String, CacheItem> cacheMap = new ConcurrentHashMap<>();

    /**
     * 缓存最大容量，0表示无限制
     */
    private long maxSize = 0;

    /**
     * 缓存淘汰策略
     */
    private EvictionPolicyType policyType = EvictionPolicyType.NONE;

    /**
     * 缓存淘汰策略实例
     */
    private CacheEvictionPolicy<String> evictionPolicy;

    /**
     * 缓存持久化器
     */
    private CachePersister cachePersister;

    /**
     * 是否自动持久化
     */
    private boolean autoPersist = false;

    /**
     * 缓存统计对象
     */
    private final CacheStatistics statistics = new CacheStatistics();

    public LocalCache() {
        this(0, EvictionPolicyType.NONE);
    }

    public LocalCache(long maxSize, EvictionPolicyType policyType) {
        this.maxSize = maxSize;
        this.policyType = policyType;
        initEvictionPolicy();
    }

    /**
     * 初始化缓存淘汰策略
     */
    private void initEvictionPolicy() {
        switch (policyType) {
            case FIFO:
                evictionPolicy = new FIFOCacheEvictionPolicy<>();
                break;
            case LRU:
                evictionPolicy = new LRUCacheEvictionPolicy<>();
                break;
            case LFU:
                evictionPolicy = new LFUCacheEvictionPolicy<>();
                break;
            case NONE:
            default:
                evictionPolicy = null;
                break;
        }
    }

    /**
     * 设置缓存最大容量
     * @param maxSize 最大容量，0表示无限制
     */
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 设置缓存淘汰策略
     * @param policyType 淘汰策略类型
     */
    public void setPolicyType(EvictionPolicyType policyType) {
        this.policyType = policyType;
        initEvictionPolicy();
    }

    /**
     * 设置缓存持久化器
     * @param cachePersister 缓存持久化器
     */
    public void setCachePersister(CachePersister cachePersister) {
        this.cachePersister = cachePersister;
    }

    /**
     * 设置是否自动持久化
     * @param autoPersist true表示自动持久化，false表示手动持久化
     */
    public void setAutoPersist(boolean autoPersist) {
        this.autoPersist = autoPersist;
    }

    /**
     * 获取缓存统计信息
     * @return 缓存统计对象
     */
    public CacheStatistics getStatistics() {
        return statistics;
    }

    /**
     * 重置缓存统计信息
     */
    public void resetStatistics() {
        statistics.reset();
    }

    /**
     * 加载缓存数据
     */
    public void load() {
        if (cachePersister != null && cachePersister.exists()) {
            Map<String, CachePersister.CacheItem> loadedData = cachePersister.load();
            if (loadedData != null) {
                // 过滤掉已过期的缓存项
                for (Map.Entry<String, CachePersister.CacheItem> entry : loadedData.entrySet()) {
                    String key = entry.getKey();
                    CachePersister.CacheItem loadedItem = entry.getValue();
                    long expireTime = loadedItem.getExpireTime();
                    // 只加载未过期的缓存项
                    if (expireTime == 0 || System.currentTimeMillis() <= expireTime) {
                        CacheItem cacheItem = new CacheItem(loadedItem.getValue(), expireTime);
                        cacheMap.put(key, cacheItem);
                        // 通知淘汰策略
                        if (evictionPolicy != null) {
                            evictionPolicy.onAdd(key);
                        }
                    }
                }
            }
        }
    }

    /**
     * 持久化缓存数据
     */
    public void persist() {
        if (cachePersister != null) {
            // 转换为持久化缓存项
            Map<String, CachePersister.CacheItem> persistData = new HashMap<>();
            for (Map.Entry<String, CacheItem> entry : cacheMap.entrySet()) {
                String key = entry.getKey();
                CacheItem cacheItem = entry.getValue();
                // 只持久化未过期的缓存项
                if (!cacheItem.isExpired()) {
                    CachePersister.CacheItem persistItem = new CachePersister.CacheItem(
                            cacheItem.getValue(), cacheItem.getExpireTime());
                    persistData.put(key, persistItem);
                }
            }
            cachePersister.persist(persistData);
        }
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, 0, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        if (key != null) {
            // 1. 移除旧缓存项（如果存在）
            remove(key);

            // 2. 检查缓存大小，如果超过最大容量，执行淘汰
            if (maxSize > 0 && cacheMap.size() >= maxSize && evictionPolicy != null) {
                String evictKey = evictionPolicy.evict();
                if (evictKey != null) {
                    cacheMap.remove(evictKey);
                    // 记录缓存淘汰
                    statistics.recordEvict();
                }
            }

            // 3. 添加新缓存项
            long expireTime = timeout <= 0 ? 0 : System.currentTimeMillis() + unit.toMillis(timeout);
            cacheMap.put(key, new CacheItem(value, expireTime));

            // 4. 通知淘汰策略
            if (evictionPolicy != null) {
                evictionPolicy.onAdd(key);
            }

            // 5. 记录缓存新增
            statistics.recordPut();

            // 6. 自动持久化
            if (autoPersist) {
                persist();
            }
        }
    }

    @Override
    public Object get(String key) {
        if (key == null) {
            // 记录缓存访问，未命中
            statistics.recordAccess(false);
            return null;
        }
        CacheItem item = cacheMap.get(key);
        if (item == null) {
            // 记录缓存访问，未命中
            statistics.recordAccess(false);
            return null;
        }
        if (item.isExpired()) {
            cacheMap.remove(key);
            if (evictionPolicy != null) {
                evictionPolicy.onRemove(key);
            }
            // 记录缓存访问，未命中
            statistics.recordAccess(false);
            // 记录缓存移除
            statistics.recordRemove();
            // 自动持久化
            if (autoPersist) {
                persist();
            }
            return null;
        }
        // 通知淘汰策略，缓存项被访问
        if (evictionPolicy != null) {
            evictionPolicy.onAccess(key);
        }
        // 记录缓存访问，命中
        statistics.recordAccess(true);
        return item.getValue();
    }

    @Override
    public Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    @Override
    public void remove(String key) {
        if (key != null) {
            cacheMap.remove(key);
            if (evictionPolicy != null) {
                evictionPolicy.onRemove(key);
            }
            // 记录缓存移除
            statistics.recordRemove();
            // 自动持久化
            if (autoPersist) {
                persist();
            }
        }
    }

    @Override
    public void clear() {
        cacheMap.clear();
        if (evictionPolicy != null) {
            evictionPolicy.clear();
        }
        // 记录缓存清除
        statistics.recordClear();
        // 自动持久化
        if (autoPersist) {
            persist();
        }
    }

    @Override
    public int size() {
        // 清理过期缓存
        cacheMap.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                if (evictionPolicy != null) {
                    evictionPolicy.onRemove(entry.getKey());
                }
                // 自动持久化
                if (autoPersist) {
                    persist();
                }
                return true;
            }
            return false;
        });
        return cacheMap.size();
    }
}