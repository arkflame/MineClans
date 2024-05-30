package com.arkflame.mineclans.managers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import com.arkflame.mineclans.MineClans;

public class LocalCacheManager<K, V> {
    private final ConcurrentHashMap<K, CacheObject<V>> cacheMap;

    public LocalCacheManager(long defaultExpiration, TimeUnit timeUnit) {
        cacheMap = new ConcurrentHashMap<>();
        Bukkit.getScheduler().runTaskTimer(MineClans.getInstance(), () -> {
            long currentTime = System.currentTimeMillis();
            cacheMap.entrySet().removeIf(entry -> currentTime > entry.getValue().expiryTime);
        }, 20L * 60, 20L * 60);
    }

    public V getCache(K key) {
        CacheObject<V> cacheObject = cacheMap.get(key);
        return (cacheObject == null || System.currentTimeMillis() > cacheObject.expiryTime) ? null : cacheObject.value;
    }

    public void setCache(K key, V value, long ttl, TimeUnit timeUnit) {
        long expiryTime = System.currentTimeMillis() + timeUnit.toMillis(ttl);
        cacheMap.put(key, new CacheObject<>(value, expiryTime));
    }

    public void invalidateCache(K key) {
        cacheMap.remove(key);
    }

    public void invalidateCache() {
        cacheMap.clear();
    }

    private static class CacheObject<V> {
        private final V value;
        private final long expiryTime;

        public CacheObject(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}
