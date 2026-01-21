package org.spiderflow.core.executor.function;

import org.spiderflow.annotation.Comment;
import org.spiderflow.annotation.Example;
import org.spiderflow.core.cache.CacheManager;
import org.spiderflow.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存功能执行器
 * 提供缓存相关操作，支持本地缓存和分布式缓存切换
 * @author Administrator
 */
@Component
@Comment("缓存常用方法")
public class CacheFunctionExecutor implements FunctionExecutor{

    @Override
    public String getFunctionPrefix() {
        return "cache";
    }

    @Comment("设置缓存值，永久有效")
    @Example("${cache.set('key', 'value')}")
    public static void set(String key, Object value) {
        CacheManager.set(key, value);
    }

    @Comment("设置带过期时间的缓存值")
    @Example("${cache.set('key', 'value', 3600, 'SECONDS')}")
    public static void set(String key, Object value, long timeout, TimeUnit unit) {
        CacheManager.set(key, value, timeout, unit);
    }

    @Comment("获取缓存值，如果不存在返回null")
    @Example("${cache.get('key')}")
    public static Object get(String key) {
        return CacheManager.get(key);
    }

    @Comment("获取缓存值，如果不存在返回默认值")
    @Example("${cache.getOrDefault('key', 'default')}")
    public static Object getOrDefault(String key, Object defaultValue) {
        return CacheManager.getOrDefault(key, defaultValue);
    }

    @Comment("移除缓存值")
    @Example("${cache.remove('key')}")
    public static void remove(String key) {
        CacheManager.remove(key);
    }

    @Comment("清除所有缓存")
    @Example("${cache.clear()}")
    public static void clear() {
        CacheManager.clear();
    }

    @Comment("检查缓存是否存在")
    @Example("${cache.containsKey('key')}")
    public static boolean containsKey(String key) {
        return CacheManager.containsKey(key);
    }

    @Comment("获取缓存大小")
    @Example("${cache.size()}")
    public static int size() {
        return CacheManager.size();
    }

    @Comment("切换默认缓存类型")
    @Example("${cache.switchCacheType('LOCAL')}")
    public static void switchCacheType(String cacheType) {
        CacheManager.getInstance().setDefaultCacheType(CacheManager.CacheType.valueOf(cacheType));
    }

    @Comment("获取当前默认缓存类型")
    @Example("${cache.getDefaultCacheType()}")
    public static String getDefaultCacheType() {
        return CacheManager.getInstance().getDefaultCacheType().name();
    }
}