import org.spiderflow.core.executor.function.CacheFunctionExecutor;

import java.util.concurrent.TimeUnit;

/**
 * 缓存功能测试类
 */
public class CacheTest {
    public static void main(String[] args) throws Exception {
        // 测试1：基本缓存设置和获取
        System.out.println("=== 测试1：基本缓存设置和获取 ===");
        CacheFunctionExecutor.set("testKey", "testValue");
        Object value = CacheFunctionExecutor.get("testKey");
        System.out.println("缓存值: " + value);
        System.out.println("预期: testValue");
        System.out.println("结果: " + ("testValue".equals(value) ? "通过" : "失败"));
        System.out.println();

        // 测试2：缓存过期
        System.out.println("=== 测试2：缓存过期 ===");
        CacheFunctionExecutor.set("expireKey", "expireValue", 2, TimeUnit.SECONDS);
        System.out.println("设置2秒过期缓存");
        System.out.println("立即获取: " + CacheFunctionExecutor.get("expireKey"));
        Thread.sleep(3000);
        Object expiredValue = CacheFunctionExecutor.get("expireKey");
        System.out.println("3秒后获取: " + expiredValue);
        System.out.println("预期: null");
        System.out.println("结果: " + (expiredValue == null ? "通过" : "失败"));
        System.out.println();

        // 测试3：默认值
        System.out.println("=== 测试3：默认值 ===");
        Object defaultValue = CacheFunctionExecutor.getOrDefault("nonExistKey", "defaultValue");
        System.out.println("获取不存在的键，使用默认值: " + defaultValue);
        System.out.println("预期: defaultValue");
        System.out.println("结果: " + ("defaultValue".equals(defaultValue) ? "通过" : "失败"));
        System.out.println();

        // 测试4：缓存是否存在
        System.out.println("=== 测试4：缓存是否存在 ===");
        boolean exists = CacheFunctionExecutor.containsKey("testKey");
        System.out.println("testKey是否存在: " + exists);
        System.out.println("预期: true");
        System.out.println("结果: " + (exists ? "通过" : "失败"));
        System.out.println();

        // 测试5：移除缓存
        System.out.println("=== 测试5：移除缓存 ===");
        CacheFunctionExecutor.remove("testKey");
        Object removedValue = CacheFunctionExecutor.get("testKey");
        System.out.println("移除后获取testKey: " + removedValue);
        System.out.println("预期: null");
        System.out.println("结果: " + (removedValue == null ? "通过" : "失败"));
        System.out.println();

        // 测试6：缓存大小
        System.out.println("=== 测试6：缓存大小 ===");
        CacheFunctionExecutor.set("key1", "value1");
        CacheFunctionExecutor.set("key2", "value2");
        CacheFunctionExecutor.set("key3", "value3");
        int size = CacheFunctionExecutor.size();
        System.out.println("缓存大小: " + size);
        System.out.println("预期: 3");
        System.out.println("结果: " + (size == 3 ? "通过" : "失败"));
        System.out.println();

        // 测试7：清除所有缓存
        System.out.println("=== 测试7：清除所有缓存 ===");
        CacheFunctionExecutor.clear();
        int clearSize = CacheFunctionExecutor.size();
        System.out.println("清除后缓存大小: " + clearSize);
        System.out.println("预期: 0");
        System.out.println("结果: " + (clearSize == 0 ? "通过" : "失败"));
    }
}