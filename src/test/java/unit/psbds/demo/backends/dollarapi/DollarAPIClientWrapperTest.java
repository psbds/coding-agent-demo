package unit.psbds.demo.backends.dollarapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import psbds.demo.backends.dollarapi.DollarAPIClient;
import psbds.demo.backends.dollarapi.DollarAPIClientWrapper;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DollarAPIClientWrapperTest {

    private DollarAPIClientWrapper wrapper;
    private DollarAPIClient client;

    @BeforeEach
    void setUp() {
        client = mock(DollarAPIClient.class);
        wrapper = new DollarAPIClientWrapper(client);
    }

    @Test
    void getUSDQuotations_when_apiReturnsSuccessfully_should_returnQuotationsList() {
        // Arrange
        DollarAPIQuotationResponse response1 = mock(DollarAPIQuotationResponse.class);
        List<DollarAPIQuotationResponse> expectedList = Arrays.asList(response1);
        when(client.getUSDQuotations()).thenReturn(expectedList);

        // Act
        List<DollarAPIQuotationResponse> result = wrapper.getUSDQuotations();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result should contain one element");
        assertEquals(expectedList, result, "Result should match expected list");
        verify(client).getUSDQuotations();
    }

    @Test
    void getUSDQuotations_when_apiReturns404_should_returnNull() {
        // Arrange
        WebApplicationException exception = new WebApplicationException(Response.Status.NOT_FOUND);
        when(client.getUSDQuotations()).thenThrow(exception);

        // Act
        List<DollarAPIQuotationResponse> result = wrapper.getUSDQuotations();

        // Assert
        assertNull(result, "Result should be null when API returns 404");
        verify(client).getUSDQuotations();
    }

    @Test
    void getUSDQuotations_when_apiReturns500_should_throwException() {
        // Arrange
        WebApplicationException exception = new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        when(client.getUSDQuotations()).thenThrow(exception);

        // Act & Assert
        WebApplicationException thrownException = assertThrows(WebApplicationException.class,
                () -> wrapper.getUSDQuotations(),
                "Should throw WebApplicationException for non-404 errors");
        assertEquals(500, thrownException.getResponse().getStatus(),
                "Exception should have status code 500");
        verify(client).getUSDQuotations();
    }

    @Test
    void getUSDQuotations_when_unexpectedErrorOccurs_should_throwRuntimeException() {
        // Arrange
        when(client.getUSDQuotations()).thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class,
                () -> wrapper.getUSDQuotations(),
                "Should throw RuntimeException for unexpected errors");
        assertEquals("Failed to retrieve USD quotations", thrownException.getMessage(),
                "Exception message should indicate failure to retrieve quotations");
        verify(client).getUSDQuotations();
    }
}
