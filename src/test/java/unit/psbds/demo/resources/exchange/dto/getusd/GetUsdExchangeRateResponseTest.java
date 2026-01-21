package unit.psbds.demo.resources.exchange.dto.getusd;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GetUsdExchangeRateResponseTest {

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String currencyCode = "USD";
        String currencyName = "Dólar";
        BigDecimal buyRate = new BigDecimal("5.371");
        BigDecimal sellRate = new BigDecimal("5.374");
        BigDecimal previousCloseRate = new BigDecimal("5.3694");
        String lastUpdate = "2026-01-16T19:02:00.000Z";

        // Act
        GetUsdExchangeRateResponse response = new GetUsdExchangeRateResponse(
            currencyCode, currencyName, buyRate, sellRate, previousCloseRate, lastUpdate
        );

        // Assert
        assertEquals(currencyCode, response.getCurrencyCode(), "currencyCode should be set correctly by constructor");
        assertEquals(currencyName, response.getCurrencyName(), "currencyName should be set correctly by constructor");
        assertEquals(buyRate, response.getBuyRate(), "buyRate should be set correctly by constructor");
        assertEquals(sellRate, response.getSellRate(), "sellRate should be set correctly by constructor");
        assertEquals(previousCloseRate, response.getPreviousCloseRate(), "previousCloseRate should be set correctly by constructor");
        assertEquals(lastUpdate, response.getLastUpdate(), "lastUpdate should be set correctly by constructor");
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        GetUsdExchangeRateResponse response = new GetUsdExchangeRateResponse();
        String currencyCode = "USD";
        String currencyName = "Dólar";
        BigDecimal buyRate = new BigDecimal("5.371");
        BigDecimal sellRate = new BigDecimal("5.374");
        BigDecimal previousCloseRate = new BigDecimal("5.3694");
        String lastUpdate = "2026-01-16T19:02:00.000Z";

        // Act
        response.setCurrencyCode(currencyCode);
        response.setCurrencyName(currencyName);
        response.setBuyRate(buyRate);
        response.setSellRate(sellRate);
        response.setPreviousCloseRate(previousCloseRate);
        response.setLastUpdate(lastUpdate);

        // Assert
        assertEquals(currencyCode, response.getCurrencyCode(), "getCurrencyCode should return the value set by setCurrencyCode");
        assertEquals(currencyName, response.getCurrencyName(), "getCurrencyName should return the value set by setCurrencyName");
        assertEquals(buyRate, response.getBuyRate(), "getBuyRate should return the value set by setBuyRate");
        assertEquals(sellRate, response.getSellRate(), "getSellRate should return the value set by setSellRate");
        assertEquals(previousCloseRate, response.getPreviousCloseRate(), "getPreviousCloseRate should return the value set by setPreviousCloseRate");
        assertEquals(lastUpdate, response.getLastUpdate(), "getLastUpdate should return the value set by setLastUpdate");
    }

    @Test
    void testCurrencyCodeFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field currencyCodeField = GetUsdExchangeRateResponse.class.getDeclaredField("currencyCode");

        // Act
        JsonProperty jsonProperty = currencyCodeField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "currencyCode field should have @JsonProperty annotation");
        assertEquals("currencyCode", jsonProperty.value(), "currencyCode field should have correct JsonProperty value");
    }

    @Test
    void testCurrencyNameFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field currencyNameField = GetUsdExchangeRateResponse.class.getDeclaredField("currencyName");

        // Act
        JsonProperty jsonProperty = currencyNameField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "currencyName field should have @JsonProperty annotation");
        assertEquals("currencyName", jsonProperty.value(), "currencyName field should have correct JsonProperty value");
    }

    @Test
    void testBuyRateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field buyRateField = GetUsdExchangeRateResponse.class.getDeclaredField("buyRate");

        // Act
        JsonProperty jsonProperty = buyRateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "buyRate field should have @JsonProperty annotation");
        assertEquals("buyRate", jsonProperty.value(), "buyRate field should have correct JsonProperty value");
    }

    @Test
    void testSellRateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field sellRateField = GetUsdExchangeRateResponse.class.getDeclaredField("sellRate");

        // Act
        JsonProperty jsonProperty = sellRateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "sellRate field should have @JsonProperty annotation");
        assertEquals("sellRate", jsonProperty.value(), "sellRate field should have correct JsonProperty value");
    }

    @Test
    void testPreviousCloseRateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field previousCloseRateField = GetUsdExchangeRateResponse.class.getDeclaredField("previousCloseRate");

        // Act
        JsonProperty jsonProperty = previousCloseRateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "previousCloseRate field should have @JsonProperty annotation");
        assertEquals("previousCloseRate", jsonProperty.value(), "previousCloseRate field should have correct JsonProperty value");
    }

    @Test
    void testLastUpdateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field lastUpdateField = GetUsdExchangeRateResponse.class.getDeclaredField("lastUpdate");

        // Act
        JsonProperty jsonProperty = lastUpdateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "lastUpdate field should have @JsonProperty annotation");
        assertEquals("lastUpdate", jsonProperty.value(), "lastUpdate field should have correct JsonProperty value");
    }
}
