package unit.psbds.demo.resources.exchangerate;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;
import psbds.demo.services.exchangerate.GetExchangeRateService;
import io.quarkus.test.InjectMock;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExchangeRateResourceTest {

    @InjectMock
    GetExchangeRateService getExchangeRateService;

    @Test
    void getUSDExchangeRate_when_serviceReturnsData_should_return200WithExchangeRate() {
        // Arrange
        GetExchangeRateResponse mockResponse = new GetExchangeRateResponse();
        mockResponse.setCode("USD");
        mockResponse.setName("Dólar Americano/Real Brasileiro");
        mockResponse.setBid(new BigDecimal("6.0987"));
        mockResponse.setAsk(new BigDecimal("6.0992"));
        mockResponse.setHigh(new BigDecimal("6.1234"));
        mockResponse.setLow(new BigDecimal("6.0456"));
        mockResponse.setVariation(new BigDecimal("0.0234"));
        mockResponse.setPercentageChange(new BigDecimal("0.38"));
        mockResponse.setTimestamp("1737300123");
        mockResponse.setCreatedDate("2026-01-19 12:15:23");

        when(getExchangeRateService.getUSDExchangeRate()).thenReturn(mockResponse);

        // Act & Assert
        given()
            .when()
                .get("/exchange-rate/usd")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("code", equalTo("USD"))
                .body("name", equalTo("Dólar Americano/Real Brasileiro"))
                .body("bid", equalTo(6.0987f))
                .body("ask", equalTo(6.0992f))
                .body("high", equalTo(6.1234f))
                .body("low", equalTo(6.0456f))
                .body("variation", equalTo(0.0234f))
                .body("percentageChange", equalTo(0.38f))
                .body("timestamp", equalTo("1737300123"))
                .body("createdDate", equalTo("2026-01-19 12:15:23"));
    }

    @Test
    void getUSDExchangeRate_when_serviceReturnsNull_should_return503() {
        // Arrange
        when(getExchangeRateService.getUSDExchangeRate()).thenReturn(null);

        // Act & Assert
        given()
            .when()
                .get("/exchange-rate/usd")
            .then()
                .statusCode(503)
                .contentType(ContentType.JSON)
                .body("error", equalTo("External exchange rate service is currently unavailable"))
                .body("status", equalTo(503));
    }

    @Test
    void getUSDExchangeRate_when_serviceThrowsException_should_return500() {
        // Arrange
        when(getExchangeRateService.getUSDExchangeRate()).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        given()
            .when()
                .get("/exchange-rate/usd")
            .then()
                .statusCode(500)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Internal server error"))
                .body("status", equalTo(500));
    }
}
