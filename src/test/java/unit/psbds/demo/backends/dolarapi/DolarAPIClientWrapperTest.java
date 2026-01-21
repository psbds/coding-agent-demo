package unit.psbds.demo.backends.dolarapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClient;
import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;

import jakarta.ws.rs.WebApplicationException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DolarAPIClientWrapperTest {

    private DolarAPIClientWrapper wrapper;
    private DolarAPIClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(DolarAPIClient.class);
        wrapper = new DolarAPIClientWrapper(mockClient);
    }

    @Test
    void getCotacoesUsd_when_clientReturnsValidResponse_should_returnResponse() {
        // Arrange
        DolarAPICotacoesUsdResponse expectedResponse = new DolarAPICotacoesUsdResponse();
        expectedResponse.setMoeda("USD");
        expectedResponse.setNome("Dólar");
        expectedResponse.setCompra(new BigDecimal("5.371"));
        expectedResponse.setVenda(new BigDecimal("5.374"));
        
        when(mockClient.getCotacoesUsd()).thenReturn(expectedResponse);

        // Act
        DolarAPICotacoesUsdResponse result = wrapper.getCotacoesUsd();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("USD", result.getMoeda(), "Moeda should match");
        assertEquals("Dólar", result.getNome(), "Nome should match");
        verify(mockClient).getCotacoesUsd();
    }

    @Test
    void getCotacoesUsd_when_clientReturnsNull_should_returnNull() {
        // Arrange
        when(mockClient.getCotacoesUsd()).thenReturn(null);

        // Act
        DolarAPICotacoesUsdResponse result = wrapper.getCotacoesUsd();

        // Assert
        assertNull(result, "Result should be null when client returns null");
        verify(mockClient).getCotacoesUsd();
    }

    @Test
    void getCotacoesUsd_when_clientThrowsWebApplicationException_should_propagateException() {
        // Arrange
        WebApplicationException expectedException = new WebApplicationException("External API error", 500);
        when(mockClient.getCotacoesUsd()).thenThrow(expectedException);

        // Act & Assert
        WebApplicationException thrownException = assertThrows(
            WebApplicationException.class,
            () -> wrapper.getCotacoesUsd(),
            "Should propagate WebApplicationException from client"
        );
        
        assertEquals(expectedException, thrownException, "Should throw the same exception");
        verify(mockClient).getCotacoesUsd();
    }
}
