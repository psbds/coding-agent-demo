package psbds.demo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExchangeResourceTest {

    @InjectMock
    DolarAPIClientWrapper dolarAPIClientWrapper;

    @Test
    void getUsdExchangeRate_when_backendReturnsValidData_should_return200WithCorrectData() {
        // Arrange
        DolarAPICotacoesUsdResponse backendResponse = new DolarAPICotacoesUsdResponse();
        backendResponse.setMoeda("USD");
        backendResponse.setNome("Dólar");
        backendResponse.setCompra(new BigDecimal("5.371"));
        backendResponse.setVenda(new BigDecimal("5.374"));
        backendResponse.setFechoAnterior(new BigDecimal("5.3694"));
        backendResponse.setDataAtualizacao("2026-01-16T19:02:00.000Z");

        when(dolarAPIClientWrapper.getCotacoesUsd()).thenReturn(backendResponse);

        // Act & Assert
        given()
            .when().get("/exchange/usd")
            .then()
                .statusCode(200)
                .body("currencyCode", is("USD"))
                .body("currencyName", is("Dólar"))
                .body("buyRate", is(5.371f))
                .body("sellRate", is(5.374f))
                .body("previousCloseRate", is(5.3694f))
                .body("lastUpdate", is("2026-01-16T19:02:00.000Z"));
    }

    @Test
    void getUsdExchangeRate_when_backendReturnsNull_should_return503() {
        // Arrange
        when(dolarAPIClientWrapper.getCotacoesUsd()).thenReturn(null);

        // Act & Assert
        given()
            .when().get("/exchange/usd")
            .then()
                .statusCode(503);
    }

    @Test
    void getUsdExchangeRate_when_backendThrowsException_should_return503() {
        // Arrange
        when(dolarAPIClientWrapper.getCotacoesUsd())
            .thenThrow(new WebApplicationException("Backend error", Response.Status.INTERNAL_SERVER_ERROR));

        // Act & Assert
        given()
            .when().get("/exchange/usd")
            .then()
                .statusCode(503);
    }

    @Test
    void getUsdExchangeRate_when_called_should_returnJsonContentType() {
        // Arrange
        DolarAPICotacoesUsdResponse backendResponse = new DolarAPICotacoesUsdResponse();
        backendResponse.setMoeda("USD");
        backendResponse.setNome("Dólar");
        backendResponse.setCompra(new BigDecimal("5.371"));
        backendResponse.setVenda(new BigDecimal("5.374"));
        backendResponse.setFechoAnterior(new BigDecimal("5.3694"));
        backendResponse.setDataAtualizacao("2026-01-16T19:02:00.000Z");

        when(dolarAPIClientWrapper.getCotacoesUsd()).thenReturn(backendResponse);

        // Act & Assert
        given()
            .when().get("/exchange/usd")
            .then()
                .statusCode(200)
                .contentType("application/json")
                .body("currencyCode", notNullValue())
                .body("buyRate", notNullValue())
                .body("sellRate", notNullValue());
    }
}
