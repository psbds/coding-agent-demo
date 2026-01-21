package psbds.demo.repository.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Optional;

/**
 * Base class for Redis cache implementations.
 * Provides common caching functionality with serialization/deserialization.
 *
 * @param <T> the type of object to cache
 */
public abstract class RemoteCache<T> {

    private static final Logger LOG = Logger.getLogger(RemoteCache.class);

    protected ValueCommands<String, String> commands;
    protected ObjectMapper objectMapper;
    protected Class<T> valueType;

    /**
     * No-args constructor required for CDI proxying.
     */
    protected RemoteCache() {
        // Required for CDI
    }

    /**
     * Constructor for RemoteCache.
     *
     * @param redisDataSource the Redis data source
     * @param objectMapper    the Jackson ObjectMapper for serialization
     * @param valueType       the class type of cached values
     */
    protected RemoteCache(RedisDataSource redisDataSource, ObjectMapper objectMapper, Class<T> valueType) {
        this.commands = redisDataSource.value(String.class);
        this.objectMapper = objectMapper;
        this.valueType = valueType;
    }

    /**
     * Gets the cache key for storing/retrieving values.
     *
     * @return the cache key
     */
    protected abstract String getCacheKey();

    /**
     * Gets the TTL (time to live) for cached values.
     *
     * @return the TTL duration
     */
    protected abstract Duration getTtl();

    /**
     * Retrieves a value from cache.
     *
     * @return Optional containing the cached value if present, empty otherwise
     */
    public Optional<T> get() {
        try {
            String key = getCacheKey();
            LOG.debug("Attempting to get value from cache with key: " + key);
            
            String jsonValue = commands.get(key);
            
            if (jsonValue == null) {
                LOG.debug("Cache miss for key: " + key);
                return Optional.empty();
            }
            
            T value = objectMapper.readValue(jsonValue, valueType);
            LOG.debug("Cache hit for key: " + key);
            return Optional.of(value);
            
        } catch (JsonProcessingException e) {
            LOG.error("Error deserializing value from cache: " + e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            LOG.error("Error retrieving value from cache: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Stores a value in cache with the configured TTL.
     *
     * @param value the value to cache
     */
    public void set(T value) {
        if (value == null) {
            LOG.warn("Attempted to cache null value, skipping");
            return;
        }

        try {
            String key = getCacheKey();
            String jsonValue = objectMapper.writeValueAsString(value);
            Duration ttl = getTtl();
            
            LOG.debug("Storing value in cache with key: " + key + ", TTL: " + ttl.getSeconds() + " seconds");
            commands.setex(key, ttl.getSeconds(), jsonValue);
            LOG.debug("Successfully cached value with key: " + key);
            
        } catch (JsonProcessingException e) {
            LOG.error("Error serializing value for cache: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Error storing value in cache: " + e.getMessage(), e);
        }
    }

    /**
     * Invalidates (deletes) the cached value.
     */
    public void invalidate() {
        try {
            String key = getCacheKey();
            LOG.debug("Invalidating cache for key: " + key);
            commands.getdel(key);
            LOG.debug("Successfully invalidated cache for key: " + key);
        } catch (Exception e) {
            LOG.error("Error invalidating cache: " + e.getMessage(), e);
        }
    }
}
