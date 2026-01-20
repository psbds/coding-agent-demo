package psbds.demo.repository.cache;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Base cache class providing reusable Redis caching functionality
 * Uses Jackson for JSON serialization/deserialization
 */
@ApplicationScoped
public class RemoteCache {

    RedisDataSource datasource;

    private final ValueCommands<String, String> commands;
    private final String dataSourceKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RemoteCache() {
        this.datasource = null;
        this.commands = null;
        this.dataSourceKey = null;
    }

    public RemoteCache(String dataSourceKey, RedisDataSource datasource) {
        this.dataSourceKey = dataSourceKey;
        this.datasource = datasource;
        commands = datasource.value(String.class);
    }

    public void set(String key, String value) {
        commands.setnx(formatKey(key), value);
    }

    public void setex(String key, String value, long duration) {
        commands.setex(formatKey(key), duration, value);
    }

    public String get(String key) {
        return commands.get(formatKey(key));
    }

    public void del(String key) {
        commands.getdel(formatKey(key));
    }

    public Set<String> keys() {
        return datasource.key()
                .keys(formatKey("*"))
                .stream()
                .collect(Collectors.toSet());
    }

    public Map<String, String> list() {
        return commands.mget(formatKey(""));
    }

    private String formatKey(String key) {
        return dataSourceKey + ":" + key;
    }

    /**
     * Retrieve and deserialize a cached value
     * 
     * @param key Cache key
     * @param clazz Class type to deserialize to
     * @return Deserialized object or null if not found or error occurs
     */
    @Nullable
    public <T> T getValue(String key, Class<T> clazz) {
        try {
            String value = get(key);
            if (value != null) {
                return objectMapper.readValue(value, clazz);
            }
            return null;
        } catch (Exception e) {
            Log.errorf(e, "Failed to deserialize cache value for key: %s", key);
            return null;
        }
    }

    /**
     * Serialize and store a value in cache with TTL
     * 
     * @param key Cache key
     * @param value Object to cache
     * @param ttlSeconds Time to live in seconds
     */
    public void setValue(String key, Object value, long ttlSeconds) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            setex(key, jsonValue, ttlSeconds);
        } catch (Exception e) {
            Log.errorf(e, "Failed to save cache value for key: %s", key);
        }
    }
}
