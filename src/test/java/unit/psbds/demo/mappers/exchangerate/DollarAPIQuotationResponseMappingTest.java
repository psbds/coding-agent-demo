package unit.psbds.demo.mappers.exchangerate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import psbds.demo.mappers.exchangerate.DollarAPIQuotationResponseMapping;
import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DollarAPIQuotationResponseMappingTest {

    private DollarAPIQuotationResponseMapping mapping;

    @BeforeEach
    void setUp() {
        mapping = new DollarAPIQuotationResponseMapping();
    }

    @Test
    void toGetExchangeRateResponse_when_validApiResponseProvided_should_mapAllFieldsCorrectly() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = new DollarAPIQuotationResponse();
        apiResponse.setCode("USD");
        apiResponse.setName("Dólar Americano/Real Brasileiro");
        apiResponse.setBid("6.0987");
        apiResponse.setAsk("6.0992");
        apiResponse.setHigh("6.1234");
        apiResponse.setLow("6.0456");
        apiResponse.setVarBid("0.0234");
        apiResponse.setPctChange("0.38");
        apiResponse.setTimestamp("1737300123");
        apiResponse.setCreateDate("2026-01-19 12:15:23");

        // Act
        GetExchangeRateResponse result = mapping.toGetExchangeRateResponse(apiResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("USD", result.getCode(), "Code should be mapped correctly");
        assertEquals("Dólar Americano/Real Brasileiro", result.getName(), "Name should be mapped correctly");
        assertEquals(new BigDecimal("6.0987"), result.getBid(), "Bid should be converted to BigDecimal correctly");
        assertEquals(new BigDecimal("6.0992"), result.getAsk(), "Ask should be converted to BigDecimal correctly");
        assertEquals(new BigDecimal("6.1234"), result.getHigh(), "High should be converted to BigDecimal correctly");
        assertEquals(new BigDecimal("6.0456"), result.getLow(), "Low should be converted to BigDecimal correctly");
        assertEquals(new BigDecimal("0.0234"), result.getVariation(), "Variation should be converted to BigDecimal correctly");
        assertEquals(new BigDecimal("0.38"), result.getPercentageChange(), "PercentageChange should be converted to BigDecimal correctly");
        assertEquals("1737300123", result.getTimestamp(), "Timestamp should be mapped correctly");
        assertEquals("2026-01-19 12:15:23", result.getCreatedDate(), "CreatedDate should be mapped correctly");
    }

    @Test
    void toGetExchangeRateResponse_when_nullApiResponse_should_returnNull() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = null;

        // Act
        GetExchangeRateResponse result = mapping.toGetExchangeRateResponse(apiResponse);

        // Assert
        assertNull(result, "Result should be null when API response is null");
    }

    @Test
    void toGetExchangeRateResponse_when_numericFieldsAreNull_should_setThemToNull() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = new DollarAPIQuotationResponse();
        apiResponse.setCode("USD");
        apiResponse.setName("Test Currency");
        apiResponse.setBid(null);
        apiResponse.setAsk(null);
        apiResponse.setHigh(null);
        apiResponse.setLow(null);
        apiResponse.setVarBid(null);
        apiResponse.setPctChange(null);

        // Act
        GetExchangeRateResponse result = mapping.toGetExchangeRateResponse(apiResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertNull(result.getBid(), "Bid should be null when input is null");
        assertNull(result.getAsk(), "Ask should be null when input is null");
        assertNull(result.getHigh(), "High should be null when input is null");
        assertNull(result.getLow(), "Low should be null when input is null");
        assertNull(result.getVariation(), "Variation should be null when input is null");
        assertNull(result.getPercentageChange(), "PercentageChange should be null when input is null");
    }

    @Test
    void toGetExchangeRateResponse_when_numericFieldsAreEmpty_should_setThemToNull() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = new DollarAPIQuotationResponse();
        apiResponse.setCode("USD");
        apiResponse.setName("Test Currency");
        apiResponse.setBid("");
        apiResponse.setAsk("  ");
        apiResponse.setHigh("");

        // Act
        GetExchangeRateResponse result = mapping.toGetExchangeRateResponse(apiResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertNull(result.getBid(), "Bid should be null when input is empty");
        assertNull(result.getAsk(), "Ask should be null when input is blank");
        assertNull(result.getHigh(), "High should be null when input is empty");
    }

    @Test
    void toGetExchangeRateResponse_when_numericFieldsAreInvalid_should_setThemToNull() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = new DollarAPIQuotationResponse();
        apiResponse.setCode("USD");
        apiResponse.setName("Test Currency");
        apiResponse.setBid("invalid");
        apiResponse.setAsk("not-a-number");

        // Act
        GetExchangeRateResponse result = mapping.toGetExchangeRateResponse(apiResponse);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertNull(result.getBid(), "Bid should be null when value is not a valid number");
        assertNull(result.getAsk(), "Ask should be null when value is not a valid number");
    }
}
