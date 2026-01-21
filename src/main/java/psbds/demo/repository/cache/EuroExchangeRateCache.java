package psbds.demo.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;

import java.time.Duration;

/**
 * Redis cache implementation for Euro exchange rates.
 * Caches GetEuroExchangeRateResponse with configurable TTL.
 */
@ApplicationScoped
public class EuroExchangeRateCache extends RemoteCache<GetEuroExchangeRateResponse> {

    private static final String CACHE_KEY = "EUR:exchange-rates";

    private long ttlSeconds;

    /**
     * No-args constructor required for CDI proxying.
     */
    protected EuroExchangeRateCache() {
        super();
    }

    /**
     * Constructor for EuroExchangeRateCache.
     *
     * @param redisDataSource the Redis data source
     * @param objectMapper    the Jackson ObjectMapper for serialization
     * @param ttlSeconds      the TTL in seconds from configuration
     */
    @Inject
    public EuroExchangeRateCache(
            RedisDataSource redisDataSource,
            ObjectMapper objectMapper,
            @ConfigProperty(name = "coding-agent.cache.eur-exchange.ttl-seconds", defaultValue = "60") long ttlSeconds) {
        super(redisDataSource, objectMapper, GetEuroExchangeRateResponse.class);
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    protected String getCacheKey() {
        return CACHE_KEY;
    }

    @Override
    protected Duration getTtl() {
        return Duration.ofSeconds(ttlSeconds);
    }
}
