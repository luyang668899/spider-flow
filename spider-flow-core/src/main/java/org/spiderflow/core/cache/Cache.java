package org.spiderflow.core.cache;

import java.util.concurrent.TimeUnit;

/**
 * 通用缓存接口
 * 定义所有缓存操作方法，支持本地缓存和分布式缓存实现
 * @author Administrator
 */
public interface Cache {

    /**
     * 设置永久缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置带过期时间的缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取缓存值
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期返回null
     */
    Object get(String key);

    /**
     * 获取缓存值，带默认值
     * @param key 缓存键
     * @param defaultValue 默认值
     * @return 缓存值，如果不存在或已过期返回默认值
     */
    Object getOrDefault(String key, Object defaultValue);

    /**
     * 检查缓存是否存在
     * @param key 缓存键
     * @return true表示存在且未过期，false表示不存在或已过期
     */
    boolean containsKey(String key);

    /**
     * 移除缓存
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 清除所有缓存
     */
    void clear();

    /**
     * 获取缓存大小
     * @return 缓存大小
     */
    int size();
}