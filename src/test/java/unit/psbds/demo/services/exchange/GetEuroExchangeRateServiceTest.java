package unit.psbds.demo.services.exchange;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarApiClientWrapper;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.repository.cache.EuroExchangeRateCache;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;
import psbds.demo.services.exchange.GetEuroExchangeRateService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetEuroExchangeRateService.
 */
@QuarkusTest
class GetEuroExchangeRateServiceTest {

    @Inject
    GetEuroExchangeRateService service;

    @InjectMock
    DolarApiClientWrapper dolarApiClientWrapper;

    @InjectMock
    EuroExchangeRateMapper euroExchangeRateMapper;

    @InjectMock
    EuroExchangeRateCache euroExchangeRateCache;

    private EuroExchangeRateApiResponse mockApiResponse;
    private GetEuroExchangeRateResponse mockPublicResponse;

    @BeforeEach
    void setUp() {
        // Create mock API response
        mockApiResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("6.125"))
                .venda(new BigDecimal("6.129"))
                .fechoAnterior(new BigDecimal("6.118"))
                .dataAtualizacao("2026-01-20T14:30:00.000Z")
                .build();

        // Create mock public response
        mockPublicResponse = GetEuroExchangeRateResponse.builder()
                .currencyCode("EUR")
                .currencyName("Euro")
                .buyRate(new BigDecimal("6.125"))
                .sellRate(new BigDecimal("6.129"))
                .previousCloseRate(new BigDecimal("6.118"))
                .lastUpdate("2026-01-20T14:30:00.000Z")
                .build();
    }

    @Test
    void testGetEuroExchangeRate_CacheHit() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.of(mockPublicResponse));

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(false);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockPublicResponse);
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, never()).getEuroExchangeRate();
        verify(euroExchangeRateMapper, never()).toPublicResponse(any());
        verify(euroExchangeRateCache, never()).set(any());
    }

    @Test
    void testGetEuroExchangeRate_CacheMiss_ApiSuccess() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));
        when(euroExchangeRateMapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(false);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockPublicResponse);
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateMapper, times(1)).toPublicResponse(mockApiResponse);
        verify(euroExchangeRateCache, times(1)).set(mockPublicResponse);
    }

    @Test
    void testGetEuroExchangeRate_NoCache_ApiSuccess() {
        // Arrange
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));
        when(euroExchangeRateMapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(true);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockPublicResponse);
        verify(euroExchangeRateCache, never()).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateMapper, times(1)).toPublicResponse(mockApiResponse);
        verify(euroExchangeRateCache, times(1)).set(mockPublicResponse);
    }

    @Test
    void testGetEuroExchangeRate_ApiFailure() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.empty());

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(false);

        // Assert
        assertThat(result).isEmpty();
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateMapper, never()).toPublicResponse(any());
        verify(euroExchangeRateCache, never()).set(any());
    }

    @Test
    void testGetEuroExchangeRate_MapperReturnsNull() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));
        when(euroExchangeRateMapper.toPublicResponse(mockApiResponse)).thenReturn(null);

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(false);

        // Assert
        assertThat(result).isEmpty();
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateMapper, times(1)).toPublicResponse(mockApiResponse);
        verify(euroExchangeRateCache, never()).set(any());
    }

    @Test
    void testGetEuroExchangeRate_CacheSetFailure_StillReturnsData() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));
        when(euroExchangeRateMapper.toPublicResponse(mockApiResponse)).thenReturn(mockPublicResponse);
        doThrow(new RuntimeException("Cache failure")).when(euroExchangeRateCache).set(any());

        // Act
        Optional<GetEuroExchangeRateResponse> result = service.getEuroExchangeRate(false);

        // Assert - Should still return data even if cache set fails
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockPublicResponse);
        verify(euroExchangeRateCache, times(1)).set(mockPublicResponse);
    }
}
