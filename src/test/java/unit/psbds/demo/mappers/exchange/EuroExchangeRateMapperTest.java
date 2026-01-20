package unit.psbds.demo.mappers.exchange;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;

import java.math.BigDecimal;

class EuroExchangeRateMapperTest {

    private EuroExchangeRateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EuroExchangeRateMapper();
    }

    @Test
    void toPublicResponse_when_validApiResponseProvided_should_mapAllFieldsCorrectly() {
        // Arrange
        DolarApiAPIGetEurResponse apiResponse = new DolarApiAPIGetEurResponse(
            "EUR",
            "Euro",
            new BigDecimal("6.125"),
            new BigDecimal("6.129"),
            new BigDecimal("6.118"),
            "2026-01-20T14:30:00.000Z"
        );

        // Act
        GetEurExchangeRateResponse publicResponse = mapper.toPublicResponse(apiResponse);

        // Assert
        assertNotNull(publicResponse, "Public response should not be null");
        assertEquals("EUR", publicResponse.getCurrencyCode(), 
            "moeda should be mapped to currencyCode");
        assertEquals("Euro", publicResponse.getCurrencyName(), 
            "nome should be mapped to currencyName");
        assertEquals(new BigDecimal("6.125"), publicResponse.getBuyRate(), 
            "compra should be mapped to buyRate");
        assertEquals(new BigDecimal("6.129"), publicResponse.getSellRate(), 
            "venda should be mapped to sellRate");
        assertEquals(new BigDecimal("6.118"), publicResponse.getPreviousCloseRate(), 
            "fechoAnterior should be mapped to previousCloseRate");
        assertEquals("2026-01-20T14:30:00.000Z", publicResponse.getLastUpdate(), 
            "dataAtualizacao should be mapped to lastUpdate");
    }

    @Test
    void toPublicResponse_when_nullApiResponseProvided_should_returnNull() {
        // Arrange
        DolarApiAPIGetEurResponse apiResponse = null;

        // Act
        GetEurExchangeRateResponse publicResponse = mapper.toPublicResponse(apiResponse);

        // Assert
        assertNull(publicResponse, "Public response should be null when API response is null");
    }
}
