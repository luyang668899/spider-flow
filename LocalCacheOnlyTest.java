import org.spiderflow.core.executor.function.CacheFunctionExecutor;

import java.util.concurrent.TimeUnit;

/**
 * 仅测试本地缓存功能
 * 避免Redis依赖和服务问题
 */
public class LocalCacheOnlyTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== 仅本地缓存功能测试 ===");
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

        // 测试3：本地缓存过期
        System.out.println("=== 测试3：本地缓存过期 ===");
        CacheFunctionExecutor.set("local_expire_key", "local_expire_value", 2, TimeUnit.SECONDS);
        System.out.println("设置2秒过期的本地缓存");
        System.out.println("立即获取: " + CacheFunctionExecutor.get("local_expire_key"));
        Thread.sleep(3000);
        Object expiredLocalValue = CacheFunctionExecutor.get("local_expire_key");
        System.out.println("3秒后获取: " + expiredLocalValue);
        System.out.println("预期: null");
        System.out.println("结果: " + (expiredLocalValue == null ? "通过" : "失败"));
        System.out.println();

        // 测试4：默认值获取
        System.out.println("=== 测试4：默认值获取 ===");
        Object defaultValue = CacheFunctionExecutor.getOrDefault("non_exist_key", "default_value");
        System.out.println("获取不存在的键，使用默认值: " + defaultValue);
        System.out.println("预期: default_value");
        System.out.println("结果: " + ("default_value".equals(defaultValue) ? "通过" : "失败"));
        System.out.println();

        // 测试5：缓存存在性检查
        System.out.println("=== 测试5：缓存存在性检查 ===");
        boolean exists = CacheFunctionExecutor.containsKey("local_key");
        System.out.println("local_key是否存在: " + exists);
        System.out.println("预期: true");
        System.out.println("结果: " + (exists ? "通过" : "失败"));
        
        boolean notExists = CacheFunctionExecutor.containsKey("non_exist_key");
        System.out.println("non_exist_key是否存在: " + notExists);
        System.out.println("预期: false");
        System.out.println("结果: " + (!notExists ? "通过" : "失败"));
        System.out.println();

        // 测试6：移除缓存
        System.out.println("=== 测试6：移除缓存 ===");
        CacheFunctionExecutor.remove("local_key");
        Object removedValue = CacheFunctionExecutor.get("local_key");
        System.out.println("移除后获取local_key: " + removedValue);
        System.out.println("预期: null");
        System.out.println("结果: " + (removedValue == null ? "通过" : "失败"));
        System.out.println();

        // 测试7：缓存大小
        System.out.println("=== 测试7：缓存大小 ===");
        CacheFunctionExecutor.set("size_key1", "value1");
        CacheFunctionExecutor.set("size_key2", "value2");
        CacheFunctionExecutor.set("size_key3", "value3");
        int size = CacheFunctionExecutor.size();
        System.out.println("缓存大小: " + size);
        System.out.println("预期: 3");
        System.out.println("结果: " + (size == 3 ? "通过" : "失败"));
        System.out.println();

        // 测试8：清除所有缓存
        System.out.println("=== 测试8：清除所有缓存 ===");
        CacheFunctionExecutor.clear();
        int clearSize = CacheFunctionExecutor.size();
        System.out.println("清除后缓存大小: " + clearSize);
        System.out.println("预期: 0");
        System.out.println("结果: " + (clearSize == 0 ? "通过" : "失败"));
        System.out.println();

        System.out.println("=== 所有本地缓存测试完成 ===");
    }
}