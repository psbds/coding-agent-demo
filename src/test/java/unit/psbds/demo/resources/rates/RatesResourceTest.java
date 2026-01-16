package unit.psbds.demo.resources.rates;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import psbds.demo.resources.rates.RatesResource;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import psbds.demo.services.rates.GetDolarRateService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusComponentTest
class RatesResourceTest {

    @Inject
    RatesResource resource;

    @InjectMock
    GetDolarRateService mockService;

    @Test
    void getDolarRate_when_called_should_delegateToService() {
        // Arrange
        GetDolarRateResponse mockResponse = mock(GetDolarRateResponse.class);
        when(mockService.execute()).thenReturn(mockResponse);

        // Act
        GetDolarRateResponse result = resource.getDolarRate();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockResponse, result, "Should return the response from the service");
        verify(mockService).execute();
    }
}
