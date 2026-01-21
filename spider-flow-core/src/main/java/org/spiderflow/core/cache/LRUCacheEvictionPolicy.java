package org.spiderflow.core.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU（最近最少使用）缓存淘汰策略
 * 淘汰最久未使用的缓存项
 * @param <K> 缓存键类型
 * @author Administrator
 */
public class LRUCacheEvictionPolicy<K> implements CacheEvictionPolicy<K> {

    private final Map<K, Long> accessMap;

    public LRUCacheEvictionPolicy() {
        // 使用LinkedHashMap保持访问顺序
        this.accessMap = new LinkedHashMap<>();
    }

    @Override
    public void onAccess(K key) {
        // 更新访问时间
        accessMap.remove(key);
        accessMap.put(key, System.currentTimeMillis());
    }

    @Override
    public void onAdd(K key) {
        // 新添加的缓存项，设置访问时间
        accessMap.put(key, System.currentTimeMillis());
    }

    @Override
    public void onRemove(K key) {
        accessMap.remove(key);
    }

    @Override
    public K evict() {
        if (accessMap.isEmpty()) {
            return null;
        }
        // LinkedHashMap的迭代器按插入顺序返回元素，第一个元素是最早访问的
        K key = accessMap.keySet().iterator().next();
        accessMap.remove(key);
        return key;
    }

    @Override
    public void clear() {
        accessMap.clear();
    }
}