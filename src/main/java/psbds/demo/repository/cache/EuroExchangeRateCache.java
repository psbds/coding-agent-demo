package psbds.demo.repository.cache;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;
import io.quarkus.redis.client.RedisClientName;
import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Cache for EUR exchange rate data with 60-second TTL
 */
@ApplicationScoped
public class EuroExchangeRateCache {

    @ConfigProperty(name = "coding-agent.cache.eur-exchange.ttl-seconds")
    public long ttlSeconds;

    private static final String EUR_EXCHANGE_RATES_KEY = "EUR:exchange-rates";

    protected final RemoteCache redisCache;

    public EuroExchangeRateCache(@RedisClientName("redis-coding-agent") RedisDataSource redisDataSource) {
        this.redisCache = new RemoteCache("CODING_AGENT", redisDataSource);
    }

    /**
     * Get cached EUR exchange rate
     * 
     * @return Cached exchange rate response or null if not found
     */
    @Nullable
    public GetEurExchangeRateResponse get() {
        return this.redisCache.getValue(EUR_EXCHANGE_RATES_KEY, GetEurExchangeRateResponse.class);
    }

    /**
     * Cache EUR exchange rate with configured TTL
     * 
     * @param response Exchange rate response to cache
     */
    public void set(GetEurExchangeRateResponse response) {
        this.redisCache.setValue(EUR_EXCHANGE_RATES_KEY, response, ttlSeconds);
    }

    /**
     * Invalidate EUR exchange rate cache
     */
    public void invalidate() {
        this.redisCache.del(EUR_EXCHANGE_RATES_KEY);
    }
}
