package unit.psbds.demo.mappers.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;
import psbds.demo.mappers.exchange.ExchangeRateMapper;
import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateMapperTest {

    private ExchangeRateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExchangeRateMapper();
    }

    @Test
    void toGetUsdExchangeRateResponse_when_validBackendResponseProvided_should_mapAllFieldsCorrectly() {
        // Arrange
        DolarAPICotacoesUsdResponse backendResponse = new DolarAPICotacoesUsdResponse();
        backendResponse.setMoeda("USD");
        backendResponse.setNome("Dólar");
        backendResponse.setCompra(new BigDecimal("5.371"));
        backendResponse.setVenda(new BigDecimal("5.374"));
        backendResponse.setFechoAnterior(new BigDecimal("5.3694"));
        backendResponse.setDataAtualizacao("2026-01-16T19:02:00.000Z");

        // Act
        GetUsdExchangeRateResponse result = mapper.toGetUsdExchangeRateResponse(backendResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("USD", result.getCurrencyCode(), "Currency code should be mapped correctly");
        assertEquals("Dólar", result.getCurrencyName(), "Currency name should be mapped correctly");
        assertEquals(new BigDecimal("5.371"), result.getBuyRate(), "Buy rate should be mapped correctly");
        assertEquals(new BigDecimal("5.374"), result.getSellRate(), "Sell rate should be mapped correctly");
        assertEquals(new BigDecimal("5.3694"), result.getPreviousCloseRate(), "Previous close rate should be mapped correctly");
        assertEquals("2026-01-16T19:02:00.000Z", result.getLastUpdate(), "Last update should be mapped correctly");
    }

    @Test
    void toGetUsdExchangeRateResponse_when_nullProvided_should_returnNull() {
        // Arrange
        DolarAPICotacoesUsdResponse backendResponse = null;

        // Act
        GetUsdExchangeRateResponse result = mapper.toGetUsdExchangeRateResponse(backendResponse);

        // Assert
        assertNull(result, "Result should be null when input is null");
    }
}
