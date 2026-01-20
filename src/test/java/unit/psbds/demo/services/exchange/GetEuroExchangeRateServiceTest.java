package unit.psbds.demo.services.exchange;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import psbds.demo.backends.dolarapi.DolarApiAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.repository.cache.EuroExchangeRateCache;
import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;
import psbds.demo.services.exchange.GetEuroExchangeRateService;

import java.math.BigDecimal;

class GetEuroExchangeRateServiceTest {

    private GetEuroExchangeRateService service;
    private DolarApiAPIClientWrapper dolarApiClient;
    private EuroExchangeRateCache cache;
    private EuroExchangeRateMapper mapper;

    private DolarApiAPIGetEurResponse mockApiResponse;
    private GetEurExchangeRateResponse mockPublicResponse;

    @BeforeEach
    void setUp() {
        dolarApiClient = mock(DolarApiAPIClientWrapper.class);
        cache = mock(EuroExchangeRateCache.class);
        mapper = mock(EuroExchangeRateMapper.class);
        
        service = new GetEuroExchangeRateService();
        
        // Use reflection to inject mocks
        try {
            var dolarApiClientField = GetEuroExchangeRateService.class.getDeclaredField("dolarApiClient");
            dolarApiClientField.setAccessible(true);
            dolarApiClientField.set(service, dolarApiClient);
            
            var cacheField = GetEuroExchangeRateService.class.getDeclaredField("cache");
            cacheField.setAccessible(true);
            cacheField.set(service, cache);
            
            var mapperField = GetEuroExchangeRateService.class.getDeclaredField("mapper");
            mapperField.setAccessible(true);
            mapperField.set(service, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }

        mockApiResponse = new DolarApiAPIGetEurResponse(
            "EUR",
            "Euro",
            new BigDecimal("6.125"),
            new BigDecimal("6.129"),
            new BigDecimal("6.118"),
            "2026-01-20T14:30:00.000Z"
        );

        mockPublicResponse = new GetEurExchangeRateResponse(
            "EUR",
            "Euro",
            new BigDecimal("6.125"),
            new BigDecimal("6.129"),
            new BigDecimal("6.118"),
            "2026-01-20T14:30:00.000Z"
        );
    }

    @Test
    void getEuroExchangeRate_when_cacheHasDataAndNoCacheIsFalse_should_returnCachedData() {
        // Arrange
        when(cache.get()).thenReturn(mockPublicResponse);

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(mockPublicResponse, response, "Response should be from cache");
        verify(cache).get();
        verifyNoInteractions(dolarApiClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void getEuroExchangeRate_when_noCacheIsTrue_should_bypassCacheAndFetchFromApi() {
        // Arrange
        when(dolarApiClient.getEur()).thenReturn(mockApiResponse);
        when(mapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(true);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(mockPublicResponse, response, "Response should be from API");
        verify(dolarApiClient).getEur();
        verify(mapper).toPublicResponse(mockApiResponse);
        verify(cache).set(mockPublicResponse);
        verify(cache, never()).get();
    }

    @Test
    void getEuroExchangeRate_when_cacheIsEmptyAndNoCacheIsFalse_should_fetchFromApiAndUpdateCache() {
        // Arrange
        when(cache.get()).thenReturn(null);
        when(dolarApiClient.getEur()).thenReturn(mockApiResponse);
        when(mapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(mockPublicResponse, response, "Response should be from API");
        verify(cache).get();
        verify(dolarApiClient).getEur();
        verify(mapper).toPublicResponse(mockApiResponse);
        verify(cache).set(mockPublicResponse);
    }

    @Test
    void getEuroExchangeRate_when_apiReturnsNull_should_returnNull() {
        // Arrange
        when(cache.get()).thenReturn(null);
        when(dolarApiClient.getEur()).thenReturn(null);

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNull(response, "Response should be null when API returns null");
        verify(cache).get();
        verify(dolarApiClient).getEur();
        verifyNoInteractions(mapper);
        verify(cache, never()).set(any());
    }

    @Test
    void getEuroExchangeRate_when_apiThrowsException_should_returnNull() {
        // Arrange
        when(cache.get()).thenReturn(null);
        when(dolarApiClient.getEur()).thenThrow(new RuntimeException("API error"));

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNull(response, "Response should be null when API throws exception");
        verify(cache).get();
        verify(dolarApiClient).getEur();
        verifyNoInteractions(mapper);
        verify(cache, never()).set(any());
    }

    @Test
    void getEuroExchangeRate_when_cacheThrowsException_should_fallbackToApiAndReturnData() {
        // Arrange
        when(cache.get()).thenThrow(new RuntimeException("Cache error"));
        when(dolarApiClient.getEur()).thenReturn(mockApiResponse);
        when(mapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNotNull(response, "Response should not be null even when cache fails");
        assertEquals(mockPublicResponse, response, "Response should be from API");
        verify(cache).get();
        verify(dolarApiClient).getEur();
        verify(mapper).toPublicResponse(mockApiResponse);
        verify(cache).set(mockPublicResponse);
    }

    @Test
    void getEuroExchangeRate_when_cacheSetThrowsException_should_stillReturnData() {
        // Arrange
        when(cache.get()).thenReturn(null);
        when(dolarApiClient.getEur()).thenReturn(mockApiResponse);
        when(mapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);
        doThrow(new RuntimeException("Cache set error")).when(cache).set(any());

        // Act
        GetEurExchangeRateResponse response = service.getEuroExchangeRate(false);

        // Assert
        assertNotNull(response, "Response should not be null even when cache set fails");
        assertEquals(mockPublicResponse, response, "Response should be from API");
        verify(cache).get();
        verify(dolarApiClient).getEur();
        verify(mapper).toPublicResponse(mockApiResponse);
        verify(cache).set(mockPublicResponse);
    }
}
