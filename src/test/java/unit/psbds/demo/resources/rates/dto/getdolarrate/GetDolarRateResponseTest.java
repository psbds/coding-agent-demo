package unit.psbds.demo.resources.rates.dto.getdolarrate;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GetDolarRateResponseTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        GetDolarRateResponse response = new GetDolarRateResponse();
        BigDecimal sellRate = new BigDecimal("5.3857");
        BigDecimal buyRate = new BigDecimal("5.3848");
        ZonedDateTime date = ZonedDateTime.now();

        // Act
        response.setSellRate(sellRate);
        response.setBuyRate(buyRate);
        response.setDate(date);

        // Assert
        assertEquals(sellRate, response.getSellRate(), "getSellRate should return the value set by setSellRate");
        assertEquals(buyRate, response.getBuyRate(), "getBuyRate should return the value set by setBuyRate");
        assertEquals(date, response.getDate(), "getDate should return the value set by setDate");
    }

    @Test
    void testSellRateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field sellRateField = GetDolarRateResponse.class.getDeclaredField("sellRate");

        // Act
        JsonProperty jsonProperty = sellRateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "sellRate field should have @JsonProperty annotation");
        assertEquals("sell_rate", jsonProperty.value(), "sellRate field should have correct JsonProperty value");
    }

    @Test
    void testBuyRateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field buyRateField = GetDolarRateResponse.class.getDeclaredField("buyRate");

        // Act
        JsonProperty jsonProperty = buyRateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "buyRate field should have @JsonProperty annotation");
        assertEquals("buy_rate", jsonProperty.value(), "buyRate field should have correct JsonProperty value");
    }

    @Test
    void testDateFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field dateField = GetDolarRateResponse.class.getDeclaredField("date");

        // Act
        JsonProperty jsonProperty = dateField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "date field should have @JsonProperty annotation");
        assertEquals("date", jsonProperty.value(), "date field should have correct JsonProperty value");
    }
}
