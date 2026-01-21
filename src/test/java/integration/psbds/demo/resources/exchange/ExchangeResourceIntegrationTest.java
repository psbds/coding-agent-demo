package integration.psbds.demo.resources.exchange;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarApiClientWrapper;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;
import psbds.demo.repository.cache.EuroExchangeRateCache;

import java.math.BigDecimal;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for ExchangeResource EUR endpoint.
 */
@QuarkusTest
class ExchangeResourceIntegrationTest {

    @InjectMock
    DolarApiClientWrapper dolarApiClientWrapper;

    @InjectMock
    EuroExchangeRateCache euroExchangeRateCache;

    private EuroExchangeRateApiResponse mockApiResponse;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(dolarApiClientWrapper, euroExchangeRateCache);

        // Create mock API response
        mockApiResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("6.125"))
                .venda(new BigDecimal("6.129"))
                .fechoAnterior(new BigDecimal("6.118"))
                .dataAtualizacao("2026-01-20T14:30:00.000Z")
                .build();
    }

    @Test
    void testGetEuroExchangeRate_Success() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));

        // Act & Assert
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("currencyCode", equalTo("EUR"))
            .body("currencyName", equalTo("Euro"))
            .body("buyRate", is(notNullValue()))
            .body("sellRate", is(notNullValue()))
            .body("previousCloseRate", is(notNullValue()))
            .body("lastUpdate", equalTo("2026-01-20T14:30:00.000Z"));

        // Verify cache was checked and updated
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateCache, times(1)).set(any());
    }

    @Test
    void testGetEuroExchangeRate_ApiUnavailable() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.empty());

        // Act & Assert
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(503)
            .contentType(ContentType.JSON)
            .body("error", equalTo("Exchange rate service unavailable"))
            .body("message", equalTo("Unable to retrieve exchange rates at this time"));

        // Verify cache was checked but not updated
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateCache, never()).set(any());
    }

    @Test
    void testGetEuroExchangeRate_WithNoCacheHeader() {
        // Arrange
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));

        // Act & Assert
        given()
            .header("no-cache", "true")
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("currencyCode", equalTo("EUR"))
            .body("currencyName", equalTo("Euro"));

        // Verify cache was bypassed
        verify(euroExchangeRateCache, never()).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
        verify(euroExchangeRateCache, times(1)).set(any());
    }

    @Test
    void testGetEuroExchangeRate_NoCacheHeaderFalse() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));

        // Act & Assert
        given()
            .header("no-cache", "false")
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("currencyCode", equalTo("EUR"));

        // Verify cache was checked
        verify(euroExchangeRateCache, times(1)).get();
        verify(dolarApiClientWrapper, times(1)).getEuroExchangeRate();
    }

    @Test
    void testGetEuroExchangeRate_ResponseStructure() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));

        // Act & Assert - Verify all required fields are present
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasKey("currencyCode"))
            .body("$", hasKey("currencyName"))
            .body("$", hasKey("buyRate"))
            .body("$", hasKey("sellRate"))
            .body("$", hasKey("previousCloseRate"))
            .body("$", hasKey("lastUpdate"));
    }

    @Test
    void testGetEuroExchangeRate_BigDecimalPrecision() {
        // Arrange - High precision values
        EuroExchangeRateApiResponse highPrecisionResponse = EuroExchangeRateApiResponse.builder()
                .moeda("EUR")
                .nome("Euro")
                .compra(new BigDecimal("6.12345"))
                .venda(new BigDecimal("6.12945"))
                .fechoAnterior(new BigDecimal("6.11845"))
                .dataAtualizacao("2026-01-20T14:30:00.000Z")
                .build();

        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(highPrecisionResponse));

        // Act & Assert - Verify precision is maintained
        String responseBody = given()
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .body()
            .asString();

        // Parse and verify BigDecimal values are precise
        assertThat(responseBody).contains("6.12345");
        assertThat(responseBody).contains("6.12945");
        assertThat(responseBody).contains("6.11845");
    }

    @Test
    void testGetEuroExchangeRate_FieldMapping() {
        // Arrange
        when(euroExchangeRateCache.get()).thenReturn(Optional.empty());
        when(dolarApiClientWrapper.getEuroExchangeRate()).thenReturn(Optional.of(mockApiResponse));

        // Act & Assert - Verify Portuguese fields are mapped to English
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/exchange/eur")
        .then()
            .statusCode(200)
            .body("currencyCode", equalTo(mockApiResponse.getMoeda()))
            .body("currencyName", equalTo(mockApiResponse.getNome()))
            .body("lastUpdate", equalTo(mockApiResponse.getDataAtualizacao()))
            .body("buyRate", is(notNullValue()))
            .body("sellRate", is(notNullValue()))
            .body("previousCloseRate", is(notNullValue()));
    }
}
