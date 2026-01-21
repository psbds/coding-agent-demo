package unit.psbds.demo.mappers.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;
import psbds.demo.mappers.exchange.EuroExchangeRateMapper;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EuroExchangeRateMapper.
 */
class EuroExchangeRateMapperTest {

    private EuroExchangeRateMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EuroExchangeRateMapper();
    }

    @Test
    void testToPublicResponse_Success() {
        // Arrange
        EuroExchangeRateApiResponse apiResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("6.125"))
                .venda(new BigDecimal("6.129"))
                .fechoAnterior(new BigDecimal("6.118"))
                .dataAtualizacao("2026-01-20T14:30:00.000Z")
                .build();

        // Act
        GetEuroExchangeRateResponse result = mapper.toPublicResponse(apiResponse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCurrencyCode()).isEqualTo("EUR");
        assertThat(result.getCurrencyName()).isEqualTo("Euro");
        assertThat(result.getBuyRate()).isEqualByComparingTo(new BigDecimal("6.125"));
        assertThat(result.getSellRate()).isEqualByComparingTo(new BigDecimal("6.129"));
        assertThat(result.getPreviousCloseRate()).isEqualByComparingTo(new BigDecimal("6.118"));
        assertThat(result.getLastUpdate()).isEqualTo("2026-01-20T14:30:00.000Z");
    }

    @Test
    void testToPublicResponse_NullInput() {
        // Act
        GetEuroExchangeRateResponse result = mapper.toPublicResponse(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testToPublicResponse_FieldNameMapping() {
        // Arrange
        EuroExchangeRateApiResponse apiResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("5.5"))
                .venda(new BigDecimal("5.6"))
                .fechoAnterior(new BigDecimal("5.4"))
                .dataAtualizacao("2026-01-20T10:00:00.000Z")
                .build();

        // Act
        GetEuroExchangeRateResponse result = mapper.toPublicResponse(apiResponse);

        // Assert - Verify Portuguese to English field mapping
        assertThat(result.getCurrencyCode()).isEqualTo(apiResponse.getMoeda());
        assertThat(result.getCurrencyName()).isEqualTo(apiResponse.getNome());
        assertThat(result.getBuyRate()).isEqualByComparingTo(apiResponse.getCompra());
        assertThat(result.getSellRate()).isEqualByComparingTo(apiResponse.getVenda());
        assertThat(result.getPreviousCloseRate()).isEqualByComparingTo(apiResponse.getFechoAnterior());
        assertThat(result.getLastUpdate()).isEqualTo(apiResponse.getDataAtualizacao());
    }

    @Test
    void testToPublicResponse_PreservesBigDecimalPrecision() {
        // Arrange - Test with high precision values
        EuroExchangeRateApiResponse apiResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("6.12345678"))
                .venda(new BigDecimal("6.12945678"))
                .fechoAnterior(new BigDecimal("6.11845678"))
                .dataAtualizacao("2026-01-20T14:30:00.000Z")
                .build();

        // Act
        GetEuroExchangeRateResponse result = mapper.toPublicResponse(apiResponse);

        // Assert - Verify precision is maintained
        assertThat(result.getBuyRate()).isEqualByComparingTo(new BigDecimal("6.12345678"));
        assertThat(result.getSellRate()).isEqualByComparingTo(new BigDecimal("6.12945678"));
        assertThat(result.getPreviousCloseRate()).isEqualByComparingTo(new BigDecimal("6.11845678"));
    }
}
