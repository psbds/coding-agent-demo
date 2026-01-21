package unit.psbds.demo.services.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;
import psbds.demo.mappers.exchange.ExchangeRateMapper;
import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;
import psbds.demo.services.exchange.GetUsdExchangeRateService;

import jakarta.ws.rs.WebApplicationException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUsdExchangeRateServiceTest {

    private GetUsdExchangeRateService service;
    private DolarAPIClientWrapper dolarAPIClientWrapper;
    private ExchangeRateMapper exchangeRateMapper;

    @BeforeEach
    void setUp() {
        dolarAPIClientWrapper = mock(DolarAPIClientWrapper.class);
        exchangeRateMapper = mock(ExchangeRateMapper.class);
        service = new GetUsdExchangeRateService(dolarAPIClientWrapper, exchangeRateMapper);
    }

    @Test
    void getUsdExchangeRate_when_backendReturnsValidResponse_should_returnMappedResponse() {
        // Arrange
        DolarAPICotacoesUsdResponse backendResponse = new DolarAPICotacoesUsdResponse();
        backendResponse.setMoeda("USD");
        backendResponse.setNome("Dólar");
        backendResponse.setCompra(new BigDecimal("5.371"));
        backendResponse.setVenda(new BigDecimal("5.374"));
        
        GetUsdExchangeRateResponse expectedResponse = new GetUsdExchangeRateResponse();
        expectedResponse.setCurrencyCode("USD");
        expectedResponse.setCurrencyName("Dólar");
        expectedResponse.setBuyRate(new BigDecimal("5.371"));
        expectedResponse.setSellRate(new BigDecimal("5.374"));

        when(dolarAPIClientWrapper.getCotacoesUsd()).thenReturn(backendResponse);
        when(exchangeRateMapper.toGetUsdExchangeRateResponse(backendResponse)).thenReturn(expectedResponse);

        // Act
        GetUsdExchangeRateResponse result = service.getUsdExchangeRate();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("USD", result.getCurrencyCode(), "Currency code should match");
        assertEquals("Dólar", result.getCurrencyName(), "Currency name should match");
        verify(dolarAPIClientWrapper).getCotacoesUsd();
        verify(exchangeRateMapper).toGetUsdExchangeRateResponse(backendResponse);
    }

    @Test
    void getUsdExchangeRate_when_backendReturnsNull_should_throwServiceUnavailableException() {
        // Arrange
        when(dolarAPIClientWrapper.getCotacoesUsd()).thenReturn(null);

        // Act & Assert
        WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> service.getUsdExchangeRate(),
            "Should throw WebApplicationException when backend returns null"
        );
        
        assertEquals(503, exception.getResponse().getStatus(), "Should return 503 status code");
        assertEquals("Exchange rate service unavailable", exception.getMessage(), "Should have correct error message");
    }

    @Test
    void getUsdExchangeRate_when_backendThrows5xxException_should_throwServiceUnavailableException() {
        // Arrange
        WebApplicationException backendException = new WebApplicationException("Backend error", 502);
        when(dolarAPIClientWrapper.getCotacoesUsd()).thenThrow(backendException);

        // Act & Assert
        WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> service.getUsdExchangeRate(),
            "Should throw WebApplicationException when backend throws 5xx error"
        );
        
        assertEquals(503, exception.getResponse().getStatus(), "Should return 503 status code");
        assertEquals("Exchange rate service unavailable", exception.getMessage(), "Should have correct error message");
    }

    @Test
    void getUsdExchangeRate_when_backendThrows4xxException_should_propagateException() {
        // Arrange
        WebApplicationException backendException = new WebApplicationException("Bad request", 400);
        when(dolarAPIClientWrapper.getCotacoesUsd()).thenThrow(backendException);

        // Act & Assert
        WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> service.getUsdExchangeRate(),
            "Should throw WebApplicationException when backend throws 4xx error"
        );
        
        assertEquals(400, exception.getResponse().getStatus(), "Should propagate 4xx status code");
    }

    @Test
    void getUsdExchangeRate_when_backendThrowsGenericException_should_throwServiceUnavailableException() {
        // Arrange
        when(dolarAPIClientWrapper.getCotacoesUsd()).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> service.getUsdExchangeRate(),
            "Should throw WebApplicationException when backend throws generic exception"
        );
        
        assertEquals(503, exception.getResponse().getStatus(), "Should return 503 status code");
        assertEquals("Exchange rate service unavailable", exception.getMessage(), "Should have correct error message");
    }
}
