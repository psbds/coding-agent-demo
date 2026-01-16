package unit.psbds.demo.mappers.rates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.mappers.rates.GetDolarRateResponseMapping;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GetDolarRateResponseMappingTest {

    private GetDolarRateResponseMapping mapping;

    @BeforeEach
    void setUp() {
        mapping = new GetDolarRateResponseMapping();
    }

    @Test
    void map_when_validApiResponseProvided_should_mapToGetDolarRateResponse() {
        // Arrange
        DolarAPIGetCotacaoResponse apiResponse = new DolarAPIGetCotacaoResponse();
        apiResponse.setVenda(new BigDecimal("5.3857"));
        apiResponse.setCompra(new BigDecimal("5.3848"));
        apiResponse.setDataAtualizacao(ZonedDateTime.parse("2026-01-14T14:00:00.000Z"));

        // Act
        GetDolarRateResponse result = mapping.map(apiResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(apiResponse.getVenda(), result.getSellRate(), "Sell rate should be mapped from venda");
        assertEquals(apiResponse.getCompra(), result.getBuyRate(), "Buy rate should be mapped from compra");
        assertEquals(apiResponse.getDataAtualizacao(), result.getDate(), "Date should be mapped from dataAtualizacao");
    }

    @Test
    void map_when_nullApiResponseProvided_should_returnNull() {
        // Arrange
        DolarAPIGetCotacaoResponse apiResponse = null;

        // Act
        GetDolarRateResponse result = mapping.map(apiResponse);

        // Assert
        assertNull(result, "Result should be null when api response is null");
    }
}
