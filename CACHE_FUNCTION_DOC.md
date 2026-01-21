# 缓存功能使用文档

## 功能介绍

缓存功能执行器（CacheFunctionExecutor）提供了缓存相关操作，支持本地缓存和Redis分布式缓存两种模式，可以用于提高爬虫的爬取效率和减少重复请求。

### 缓存类型

1. **LOCAL**：本地内存缓存，基于ConcurrentHashMap实现，重启应用后缓存会丢失
2. **REDIS**：Redis分布式缓存，支持跨实例缓存共享，重启应用后缓存依然存在

默认缓存类型为LOCAL，可通过`switchCacheType`方法切换。

## 支持的方法

### 1. 设置永久缓存

**方法签名**：`cache.set(key, value)`

**功能描述**：设置一个永久有效的缓存值

**参数说明**：
- `key`：缓存键，字符串类型，不能为空
- `value`：缓存值，可以是任意类型

**使用示例**：
```
${cache.set('user_info', userData)}
```

### 2. 设置带过期时间的缓存

**方法签名**：`cache.set(key, value, timeout, unit)`

**功能描述**：设置一个带过期时间的缓存值

**参数说明**：
- `key`：缓存键，字符串类型，不能为空
- `value`：缓存值，可以是任意类型
- `timeout`：过期时间数值，long类型
- `unit`：时间单位，TimeUnit枚举类型（如SECONDS、MINUTES、HOURS等）

**使用示例**：
```
${cache.set('weather_data', weatherInfo, 3600, 'SECONDS')}
${cache.set('token', tokenValue, 1, 'HOURS')}
```

### 3. 获取缓存值

**方法签名**：`cache.get(key)`

**功能描述**：根据缓存键获取缓存值，如果缓存不存在或已过期，返回null

**参数说明**：
- `key`：缓存键，字符串类型

**返回值**：缓存值，如果不存在或已过期返回null

**使用示例**：
```
${cache.get('user_info')}
```

### 4. 获取缓存值（带默认值）

**方法签名**：`cache.getOrDefault(key, defaultValue)`

**功能描述**：根据缓存键获取缓存值，如果缓存不存在或已过期，返回默认值

**参数说明**：
- `key`：缓存键，字符串类型
- `defaultValue`：默认值，可以是任意类型

**返回值**：缓存值，如果不存在或已过期返回默认值

**使用示例**：
```
${cache.getOrDefault('config', 'default_config')}
```

### 5. 检查缓存是否存在

**方法签名**：`cache.containsKey(key)`

**功能描述**：检查指定缓存键是否存在且未过期

**参数说明**：
- `key`：缓存键，字符串类型

**返回值**：布尔值，表示缓存是否存在且未过期

**使用示例**：
```
${cache.containsKey('token') ? '已登录' : '未登录'}
```

### 6. 移除缓存

**方法签名**：`cache.remove(key)`

**功能描述**：移除指定缓存键的缓存

**参数说明**：
- `key`：缓存键，字符串类型

**使用示例**：
```
${cache.remove('user_info')}
```

### 7. 清除所有缓存

**方法签名**：`cache.clear()`

**功能描述**：清除所有缓存

**使用示例**：
```
${cache.clear()}
```

### 8. 获取缓存大小

**方法签名**：`cache.size()`

**功能描述**：获取当前缓存的大小（会自动清理过期缓存）

**返回值**：整数，表示当前缓存的大小

**使用示例**：
```
${cache.size()}
```

### 9. 切换缓存类型

**方法签名**：`cache.switchCacheType(cacheType)`

**功能描述**：切换默认缓存类型

**参数说明**：
- `cacheType`：缓存类型，字符串类型，可选值："LOCAL"或"REDIS"

**使用示例**：
```
${cache.switchCacheType('REDIS')}
${cache.switchCacheType('LOCAL')}
```

### 10. 获取当前缓存类型

**方法签名**：`cache.getDefaultCacheType()`

**功能描述**：获取当前默认缓存类型

**返回值**：字符串，表示当前缓存类型（"LOCAL"或"REDIS"）

**使用示例**：
```
${cache.getDefaultCacheType()}
```

## 应用场景

1. **减少重复请求**：对于频繁访问的同一URL，可以将结果缓存一段时间，减少对目标网站的请求压力
2. **缓存配置信息**：将配置信息缓存起来，避免频繁读取文件或数据库
3. **缓存计算结果**：对于复杂计算的结果，可以缓存起来，提高后续使用效率
4. **会话管理**：可以用于缓存用户会话信息，实现简单的登录状态管理
5. **限流控制**：可以用于实现简单的限流逻辑，如限制同一IP的请求频率
6. **跨实例数据共享**：使用Redis缓存可以实现多个爬虫实例之间的数据共享

## 注意事项

1. **本地缓存**：基于内存，重启应用后缓存会丢失，适合临时缓存
2. **Redis缓存**：需要Redis服务支持，默认连接本地Redis实例（localhost:6379），可根据需要修改配置
3. **缓存大小**：本地缓存大小没有限制，使用时请注意内存占用；Redis缓存大小受Redis服务器配置限制
4. **缓存过期时间**：最小单位是毫秒，超过过期时间后缓存会自动失效
5. **缓存键**：不区分大小写，建议统一使用小写或大写
6. **缓存值**：会被序列化为字符串存储，对于复杂对象请确保可序列化
7. **Redis连接失败**：当Redis连接失败时，系统会自动降级为本地缓存，不会影响程序运行

## 示例：结合爬虫使用

### 示例1：使用本地缓存

```
# 1. 设置缓存类型为LOCAL
${cache.switchCacheType('LOCAL')}

# 2. 检查缓存是否存在，如果存在直接使用缓存结果
${if(cache.containsKey(url), cache.get(url), '')}

# 3. 如果缓存不存在，发起请求获取数据
${request.url(url).method('GET').execute()}

# 4. 将结果缓存起来，设置1小时过期
${cache.set(url, resp.text, 3600, 'SECONDS')}

# 5. 使用获取的数据
${resp.text}
```

### 示例2：使用Redis分布式缓存

```
# 1. 设置缓存类型为REDIS
${cache.switchCacheType('REDIS')}

# 2. 检查缓存是否存在，如果存在直接使用缓存结果
${if(cache.containsKey(url), cache.get(url), '')}

# 3. 如果缓存不存在，发起请求获取数据
${request.url(url).method('GET').execute()}

# 4. 将结果缓存起来，设置1小时过期
${cache.set(url, resp.text, 3600, 'SECONDS')}

# 5. 使用获取的数据
${resp.text}
```

### 示例3：根据环境动态切换缓存类型

```
# 根据环境变量或配置动态选择缓存类型
${if(env == 'production', cache.switchCacheType('REDIS'), cache.switchCacheType('LOCAL'))}

# 后续缓存操作会使用选定的缓存类型
${cache.set('key', 'value')}
```

通过上述示例，可以实现对同一URL的请求结果进行缓存，避免重复请求，提高爬虫效率。根据实际需求选择合适的缓存类型。