package org.spiderflow.core.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON格式的缓存持久化实现
 * 使用FastJSON进行序列化和反序列化
 * @author Administrator
 */
public class JsonCachePersister implements CachePersister {

    private final String filePath;

    /**
     * 构造函数
     * @param filePath 持久化文件路径
     */
    public JsonCachePersister(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(Map<String, CacheItem> cacheData) {
        try {
            // 创建父目录
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            // 序列化缓存数据
            String jsonString = JSON.toJSONString(cacheData);
            
            // 写入文件
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonString);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, CacheItem> load() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new HashMap<>();
            }

            // 读取文件内容
            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
            }

            // 反序列化缓存数据
            return JSON.parseObject(jsonString.toString(), new TypeReference<Map<String, CacheItem>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public boolean exists() {
        return new File(filePath).exists();
    }
}