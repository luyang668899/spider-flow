package org.spiderflow.core.cache;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存统计类
 * 用于统计缓存的使用情况，包括命中率、访问次数等
 * @author Administrator
 */
public class CacheStatistics {

    // 访问次数
    private final AtomicLong accessCount = new AtomicLong(0);
    
    // 命中次数
    private final AtomicLong hitCount = new AtomicLong(0);
    
    // 未命中次数
    private final AtomicLong missCount = new AtomicLong(0);
    
    // 新增缓存次数
    private final AtomicLong putCount = new AtomicLong(0);
    
    // 移除缓存次数
    private final AtomicLong removeCount = new AtomicLong(0);
    
    // 清除缓存次数
    private final AtomicLong clearCount = new AtomicLong(0);
    
    // 缓存淘汰次数
    private final AtomicLong evictCount = new AtomicLong(0);

    /**
     * 记录缓存访问
     * @param hit 是否命中
     */
    public void recordAccess(boolean hit) {
        accessCount.incrementAndGet();
        if (hit) {
            hitCount.incrementAndGet();
        } else {
            missCount.incrementAndGet();
        }
    }

    /**
     * 记录缓存新增
     */
    public void recordPut() {
        putCount.incrementAndGet();
    }

    /**
     * 记录缓存移除
     */
    public void recordRemove() {
        removeCount.incrementAndGet();
    }

    /**
     * 记录缓存清除
     */
    public void recordClear() {
        clearCount.incrementAndGet();
    }

    /**
     * 记录缓存淘汰
     */
    public void recordEvict() {
        evictCount.incrementAndGet();
    }

    /**
     * 获取缓存命中率
     * @return 命中率，范围0-1
     */
    public double getHitRate() {
        long total = accessCount.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) hitCount.get() / total;
    }

    /**
     * 获取访问次数
     * @return 访问次数
     */
    public long getAccessCount() {
        return accessCount.get();
    }

    /**
     * 获取命中次数
     * @return 命中次数
     */
    public long getHitCount() {
        return hitCount.get();
    }

    /**
     * 获取未命中次数
     * @return 未命中次数
     */
    public long getMissCount() {
        return missCount.get();
    }

    /**
     * 获取新增缓存次数
     * @return 新增缓存次数
     */
    public long getPutCount() {
        return putCount.get();
    }

    /**
     * 获取移除缓存次数
     * @return 移除缓存次数
     */
    public long getRemoveCount() {
        return removeCount.get();
    }

    /**
     * 获取清除缓存次数
     * @return 清除缓存次数
     */
    public long getClearCount() {
        return clearCount.get();
    }

    /**
     * 获取缓存淘汰次数
     * @return 缓存淘汰次数
     */
    public long getEvictCount() {
        return evictCount.get();
    }

    /**
     * 重置统计数据
     */
    public void reset() {
        accessCount.set(0);
        hitCount.set(0);
        missCount.set(0);
        putCount.set(0);
        removeCount.set(0);
        clearCount.set(0);
        evictCount.set(0);
    }

    @Override
    public String toString() {
        return "CacheStatistics{
" +
                "  accessCount=" + accessCount.get() + ",
" +
                "  hitCount=" + hitCount.get() + ",
" +
                "  missCount=" + missCount.get() + ",
" +
                "  putCount=" + putCount.get() + ",
" +
                "  removeCount=" + removeCount.get() + ",
" +
                "  clearCount=" + clearCount.get() + ",
" +
                "  evictCount=" + evictCount.get() + ",
" +
                "  hitRate=" + String.format("%.2f%%", getHitRate() * 100) + 
                "}";
    }
}