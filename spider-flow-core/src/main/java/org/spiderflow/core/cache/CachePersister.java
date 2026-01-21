package org.spiderflow.core.cache;

import java.util.Map;

/**
 * 缓存持久化接口
 * 定义缓存的持久化和加载方法
 * @author Administrator
 */
public interface CachePersister {

    /**
     * 持久化缓存数据
     * @param cacheData 缓存数据，键为缓存键，值为缓存项（包含值和过期时间）
     */
    void persist(Map<String, CacheItem> cacheData);

    /**
     * 加载缓存数据
     * @return 缓存数据，键为缓存键，值为缓存项（包含值和过期时间）
     */
    Map<String, CacheItem> load();

    /**
     * 检查持久化文件是否存在
     * @return true表示存在，false表示不存在
     */
    boolean exists();

    /**
     * 缓存项，包含值和过期时间，用于持久化
     */
    class CacheItem {
        private Object value;
        private long expireTime;

        public CacheItem(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        public Object getValue() {
            return value;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
}