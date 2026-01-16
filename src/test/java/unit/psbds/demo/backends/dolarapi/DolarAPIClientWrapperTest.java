package unit.psbds.demo.backends.dolarapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClient;
import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

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
    void getCotacaoUSD_when_apiReturnsSuccessfully_should_returnResponse() {
        // Arrange
        DolarAPIGetCotacaoResponse mockResponse = mock(DolarAPIGetCotacaoResponse.class);
        when(mockClient.getCotacaoUSD()).thenReturn(mockResponse);

        // Act
        DolarAPIGetCotacaoResponse result = wrapper.getCotacaoUSD();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockResponse, result, "Should return the response from the client");
        verify(mockClient).getCotacaoUSD();
    }

    @Test
    void getCotacaoUSD_when_apiThrowsWebApplicationException_should_propagateException() {
        // Arrange
        WebApplicationException mockException = new WebApplicationException("API Error", Response.Status.INTERNAL_SERVER_ERROR);
        when(mockClient.getCotacaoUSD()).thenThrow(mockException);

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class,
                () -> wrapper.getCotacaoUSD(),
                "Should throw WebApplicationException when API call fails");

        assertEquals(mockException, exception, "Should propagate the same exception");
        verify(mockClient).getCotacaoUSD();
    }
}
