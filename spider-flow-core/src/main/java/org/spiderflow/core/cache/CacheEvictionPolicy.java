package org.spiderflow.core.cache;

/**
 * 缓存淘汰策略接口
 * 定义缓存项淘汰的通用方法
 * @param <K> 缓存键类型
 * @author Administrator
 */
public interface CacheEvictionPolicy<K> {

    /**
     * 当缓存项被访问时调用
     * @param key 缓存键
     */
    void onAccess(K key);

    /**
     * 当缓存项被添加时调用
     * @param key 缓存键
     */
    void onAdd(K key);

    /**
     * 当缓存项被移除时调用
     * @param key 缓存键
     */
    void onRemove(K key);

    /**
     * 当需要淘汰缓存项时调用
     * @return 需要淘汰的缓存键
     */
    K evict();

    /**
     * 清除所有缓存项
     */
    void clear();
}