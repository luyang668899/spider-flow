package org.spiderflow.core.cache;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FIFO（先进先出）缓存淘汰策略
 * 按照缓存项添加的顺序淘汰最早添加的项
 * @param <K> 缓存键类型
 * @author Administrator
 */
public class FIFOCacheEvictionPolicy<K> implements CacheEvictionPolicy<K> {

    private final Set<K> keySet;

    public FIFOCacheEvictionPolicy() {
        this.keySet = new LinkedHashSet<>();
    }

    @Override
    public void onAccess(K key) {
        // FIFO策略下，访问缓存项不需要更新顺序
    }

    @Override
    public void onAdd(K key) {
        keySet.add(key);
    }

    @Override
    public void onRemove(K key) {
        keySet.remove(key);
    }

    @Override
    public K evict() {
        if (keySet.isEmpty()) {
            return null;
        }
        // LinkedHashSet的迭代器按插入顺序返回元素，第一个元素是最早添加的
        K key = keySet.iterator().next();
        keySet.remove(key);
        return key;
    }

    @Override
    public void clear() {
        keySet.clear();
    }
}