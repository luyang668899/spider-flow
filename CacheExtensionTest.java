import org.spiderflow.core.cache.CacheManager;
import org.spiderflow.core.cache.CacheManager.CacheType;
import org.spiderflow.core.executor.function.CacheFunctionExecutor;

import java.util.concurrent.TimeUnit;

/**
 * 缓存扩展功能测试类
 * 测试本地缓存和Redis缓存的切换功能
 */
public class CacheExtensionTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== 缓存扩展功能测试 ===");
        System.out.println();

        // 测试1：默认缓存类型（本地缓存）
        System.out.println("=== 测试1：默认缓存类型 ===");
        String defaultType = CacheFunctionExecutor.getDefaultCacheType();
        System.out.println("当前默认缓存类型: " + defaultType);
        System.out.println("预期: LOCAL");
        System.out.println("结果: " + ("LOCAL".equals(defaultType) ? "通过" : "失败"));
        System.out.println();

        // 测试2：使用本地缓存
        System.out.println("=== 测试2：使用本地缓存 ===");
        CacheFunctionExecutor.switchCacheType("LOCAL");
        CacheFunctionExecutor.set("local_key", "local_value");
        Object localValue = CacheFunctionExecutor.get("local_key");
        System.out.println("本地缓存值: " + localValue);
        System.out.println("预期: local_value");
        System.out.println("结果: " + ("local_value".equals(localValue) ? "通过" : "失败"));
        System.out.println();

        // 测试3：切换到Redis缓存
        System.out.println("=== 测试3：切换到Redis缓存 ===");
        try {
            CacheFunctionExecutor.switchCacheType("REDIS");
            System.out.println("切换到Redis缓存成功");
            
            // 测试Redis缓存操作
            CacheFunctionExecutor.set("redis_key", "redis_value");
            Object redisValue = CacheFunctionExecutor.get("redis_key");
            System.out.println("Redis缓存值: " + redisValue);
            System.out.println("预期: redis_value");
            System.out.println("结果: " + ("redis_value".equals(redisValue) ? "通过" : "失败"));
            
            // 测试Redis缓存过期
            CacheFunctionExecutor.set("redis_expire_key", "redis_expire_value", 2, TimeUnit.SECONDS);
            System.out.println("设置2秒过期的Redis缓存");
            System.out.println("立即获取: " + CacheFunctionExecutor.get("redis_expire_key"));
            Thread.sleep(3000);
            Object expiredRedisValue = CacheFunctionExecutor.get("redis_expire_key");
            System.out.println("3秒后获取: " + expiredRedisValue);
            System.out.println("预期: null");
            System.out.println("结果: " + (expiredRedisValue == null ? "通过" : "失败"));
        } catch (Exception e) {
            System.out.println("Redis缓存测试失败: " + e.getMessage());
            System.out.println("这可能是因为Redis服务未启动，请确保本地Redis服务正在运行");
        }
        System.out.println();

        // 测试4：切换回本地缓存
        System.out.println("=== 测试4：切换回本地缓存 ===");
        CacheFunctionExecutor.switchCacheType("LOCAL");
        String currentType = CacheFunctionExecutor.getDefaultCacheType();
        System.out.println("当前缓存类型: " + currentType);
        System.out.println("预期: LOCAL");
        System.out.println("结果: " + ("LOCAL".equals(currentType) ? "通过" : "失败"));
        
        // 验证本地缓存中的值仍然存在
        Object stillExists = CacheFunctionExecutor.get("local_key");
        System.out.println("本地缓存中的值是否仍然存在: " + stillExists);
        System.out.println("预期: local_value");
        System.out.println("结果: " + ("local_value".equals(stillExists) ? "通过" : "失败"));
        System.out.println();

        // 测试5：缓存大小
        System.out.println("=== 测试5：缓存大小 ===");
        CacheFunctionExecutor.set("size_key1", "value1");
        CacheFunctionExecutor.set("size_key2", "value2");
        CacheFunctionExecutor.set("size_key3", "value3");
        int size = CacheFunctionExecutor.size();
        System.out.println("缓存大小: " + size);
        System.out.println("预期: 4"); // 包括之前的local_key
        System.out.println("结果: " + (size == 4 ? "通过" : "失败"));
        System.out.println();

        // 测试6：清除所有缓存
        System.out.println("=== 测试6：清除所有缓存 ===");
        CacheFunctionExecutor.clear();
        int clearSize = CacheFunctionExecutor.size();
        System.out.println("清除后缓存大小: " + clearSize);
        System.out.println("预期: 0");
        System.out.println("结果: " + (clearSize == 0 ? "通过" : "失败"));
        System.out.println();

        System.out.println("=== 所有测试完成 ===");
    }
}