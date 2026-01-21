package org.spiderflow.core.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * LFU（最不经常使用）缓存淘汰策略
 * 淘汰访问频率最低的缓存项
 * @param <K> 缓存键类型
 * @author Administrator
 */
public class LFUCacheEvictionPolicy<K> implements CacheEvictionPolicy<K> {

    private final Map<K, Integer> frequencyMap;
    private final PriorityQueue<FrequencyNode<K>> priorityQueue;
    private long sequence;

    /**
     * 频率节点，用于优先队列排序
     */
    private static class FrequencyNode<K> implements Comparable<FrequencyNode<K>> {
        private final K key;
        private final int frequency;
        private final long sequence;

        public FrequencyNode(K key, int frequency, long sequence) {
            this.key = key;
            this.frequency = frequency;
            this.sequence = sequence;
        }

        @Override
        public int compareTo(FrequencyNode<K> o) {
            // 先按频率排序，频率相同按插入顺序排序
            int freqCompare = Integer.compare(this.frequency, o.frequency);
            if (freqCompare != 0) {
                return freqCompare;
            }
            return Long.compare(this.sequence, o.sequence);
        }

        public K getKey() {
            return key;
        }
    }

    public LFUCacheEvictionPolicy() {
        this.frequencyMap = new HashMap<>();
        this.priorityQueue = new PriorityQueue<>();
        this.sequence = 0;
    }

    @Override
    public void onAccess(K key) {
        // 增加访问频率
        int frequency = frequencyMap.getOrDefault(key, 0) + 1;
        frequencyMap.put(key, frequency);
        // 添加到优先队列（注意：优先队列会自动排序）
        priorityQueue.add(new FrequencyNode<>(key, frequency, sequence++));
    }

    @Override
    public void onAdd(K key) {
        // 新添加的缓存项，初始频率为1
        frequencyMap.put(key, 1);
        priorityQueue.add(new FrequencyNode<>(key, 1, sequence++));
    }

    @Override
    public void onRemove(K key) {
        frequencyMap.remove(key);
        // 优先队列中的元素不会自动删除，需要在evict时检查
    }

    @Override
    public K evict() {
        if (frequencyMap.isEmpty()) {
            return null;
        }
        // 清理无效节点（已经被移除但仍在队列中的节点）
        while (!priorityQueue.isEmpty()) {
            FrequencyNode<K> node = priorityQueue.poll();
            K key = node.getKey();
            Integer currentFreq = frequencyMap.get(key);
            // 检查节点是否有效：1. 键存在于frequencyMap中；2. 频率匹配
            if (currentFreq != null && currentFreq == node.frequency) {
                frequencyMap.remove(key);
                return key;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        frequencyMap.clear();
        priorityQueue.clear();
        sequence = 0;
    }
}