package unit.psbds.demo.repository.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import psbds.demo.repository.cache.EuroExchangeRateCache;
import psbds.demo.repository.cache.RemoteCache;
import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;
import io.quarkus.redis.datasource.RedisDataSource;

import java.math.BigDecimal;

class EuroExchangeRateCacheTest {

    private static final long TEST_TTL_SECONDS = 60L;
    private static final String EUR_EXCHANGE_RATES_KEY = "EUR:exchange-rates";

    private EuroExchangeRateCache cache;
    private RemoteCache mockRemoteCache;
    private RedisDataSource mockRedisDataSource;

    @BeforeEach
    void setUp() {
        mockRedisDataSource = mock(RedisDataSource.class);
        mockRemoteCache = mock(RemoteCache.class);

        cache = new EuroExchangeRateCache(mockRedisDataSource);
        cache.ttlSeconds = TEST_TTL_SECONDS;

        // Inject the mock using reflection
        try {
            var field = EuroExchangeRateCache.class.getDeclaredField("redisCache");
            field.setAccessible(true);
            field.set(cache, mockRemoteCache);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock RemoteCache", e);
        }
    }

    @Test
    void get_when_cacheHasData_should_returnCachedExchangeRate() {
        // Arrange
        GetEurExchangeRateResponse expectedResponse = createMockResponse();
        when(mockRemoteCache.getValue(EUR_EXCHANGE_RATES_KEY, GetEurExchangeRateResponse.class))
            .thenReturn(expectedResponse);

        // Act
        GetEurExchangeRateResponse actualResponse = cache.get();

        // Assert
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(expectedResponse, actualResponse, "Response should match cached data");
        verify(mockRemoteCache).getValue(EUR_EXCHANGE_RATES_KEY, GetEurExchangeRateResponse.class);
    }

    @Test
    void get_when_cacheIsEmpty_should_returnNull() {
        // Arrange
        when(mockRemoteCache.getValue(EUR_EXCHANGE_RATES_KEY, GetEurExchangeRateResponse.class))
            .thenReturn(null);

        // Act
        GetEurExchangeRateResponse actualResponse = cache.get();

        // Assert
        assertNull(actualResponse, "Response should be null when cache is empty");
        verify(mockRemoteCache).getValue(EUR_EXCHANGE_RATES_KEY, GetEurExchangeRateResponse.class);
    }

    @Test
    void set_when_responseProvided_should_delegateToRemoteCacheWithCorrectTtl() {
        // Arrange
        GetEurExchangeRateResponse testResponse = createMockResponse();

        // Act
        cache.set(testResponse);

        // Assert
        verify(mockRemoteCache).setValue(EUR_EXCHANGE_RATES_KEY, testResponse, TEST_TTL_SECONDS);
    }

    @Test
    void invalidate_when_called_should_delegateToRemoteCacheDelete() {
        // Act
        cache.invalidate();

        // Assert
        verify(mockRemoteCache).del(EUR_EXCHANGE_RATES_KEY);
    }

    private GetEurExchangeRateResponse createMockResponse() {
        return new GetEurExchangeRateResponse(
            "EUR",
            "Euro",
            new BigDecimal("6.125"),
            new BigDecimal("6.129"),
            new BigDecimal("6.118"),
            "2026-01-20T14:30:00.000Z"
        );
    }
}
