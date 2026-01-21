package org.spiderflow.core.executor.shape;

import org.spiderflow.context.SpiderContext;
import org.spiderflow.core.cache.CacheManager;
import org.spiderflow.core.cache.CacheManager.CacheType;
import org.spiderflow.core.utils.ExpressionUtils;
import org.spiderflow.executor.ShapeExecutor;
import org.spiderflow.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存形状执行器
 * 支持缓存的设置、获取、移除和清除操作
 * @author Administrator
 */
@Component
public class CacheExecutor implements ShapeExecutor {

    /**
     * 缓存操作类型枚举
     */
    public enum CacheOperation {
        SET,     // 设置缓存
        GET,     // 获取缓存
        REMOVE,  // 移除缓存
        CLEAR    // 清除所有缓存
    }

    // 配置项常量
    private static final String OPERATION_TYPE = "operation-type";
    private static final String CACHE_KEY = "cache-key";
    private static final String CACHE_VALUE = "cache-value";
    private static final String EXPIRE_TIME = "expire-time";
    private static final String TIME_UNIT = "time-unit";
    private static final String CACHE_TYPE = "cache-type";
    private static final String TARGET_VARIABLE = "target-variable";

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 1. 获取操作类型
        String operationType = node.getStringJsonValue(OPERATION_TYPE, "SET");
        CacheOperation operation = CacheOperation.valueOf(operationType.toUpperCase());

        // 2. 获取缓存类型
        String cacheTypeStr = node.getStringJsonValue(CACHE_TYPE, "LOCAL");
        CacheType cacheType = CacheType.valueOf(cacheTypeStr.toUpperCase());
        CacheManager.getInstance().setDefaultCacheType(cacheType);

        switch (operation) {
            case SET:
                executeSetOperation(node, context, variables);
                break;
            case GET:
                executeGetOperation(node, context, variables);
                break;
            case REMOVE:
                executeRemoveOperation(node, context, variables);
                break;
            case CLEAR:
                executeClearOperation(node, context, variables);
                break;
        }
    }

    /**
     * 执行设置缓存操作
     */
    private void executeSetOperation(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取缓存键
        String keyExpr = node.getStringJsonValue(CACHE_KEY);
        String key = (String) ExpressionUtils.execute(keyExpr, variables);

        // 获取缓存值
        String valueExpr = node.getStringJsonValue(CACHE_VALUE);
        Object value = ExpressionUtils.execute(valueExpr, variables);

        // 获取过期时间和时间单位
        String expireTimeExpr = node.getStringJsonValue(EXPIRE_TIME, "0");
        long expireTime = Long.parseLong(expireTimeExpr);

        String timeUnitStr = node.getStringJsonValue(TIME_UNIT, "SECONDS");
        TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());

        // 设置缓存
        if (expireTime > 0) {
            CacheManager.set(key, value, expireTime, timeUnit);
        } else {
            CacheManager.set(key, value);
        }

        // 记录日志
        context.pause(node.getNodeId(), "cache", "set", key + "=" + value);
    }

    /**
     * 执行获取缓存操作
     */
    private void executeGetOperation(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取缓存键
        String keyExpr = node.getStringJsonValue(CACHE_KEY);
        String key = (String) ExpressionUtils.execute(keyExpr, variables);

        // 获取缓存
        Object value = CacheManager.get(key);

        // 获取目标变量
        String targetVariable = node.getStringJsonValue(TARGET_VARIABLE, "cacheResult");

        // 将结果存储到变量
        variables.put(targetVariable, value);

        // 记录日志
        context.pause(node.getNodeId(), "cache", "get", key + "=" + value);
    }

    /**
     * 执行移除缓存操作
     */
    private void executeRemoveOperation(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 获取缓存键
        String keyExpr = node.getStringJsonValue(CACHE_KEY);
        String key = (String) ExpressionUtils.execute(keyExpr, variables);

        // 移除缓存
        CacheManager.remove(key);

        // 记录日志
        context.pause(node.getNodeId(), "cache", "remove", key);
    }

    /**
     * 执行清除所有缓存操作
     */
    private void executeClearOperation(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 清除所有缓存
        CacheManager.clear();

        // 记录日志
        context.pause(node.getNodeId(), "cache", "clear", "all");
    }

    @Override
    public String supportShape() {
        return "cache";
    }

    @Override
    public boolean isThread() {
        return false;
    }
}