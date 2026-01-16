package unit.psbds.demo.services.rates;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.mappers.rates.DolarRateMapper;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import psbds.demo.services.rates.GetDolarRateService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusComponentTest
class GetDolarRateServiceTest {

    @Inject
    GetDolarRateService service;

    @InjectMock
    DolarAPIClientWrapper mockClientWrapper;

    @InjectMock
    DolarRateMapper mockMapper;

    @Test
    void execute_when_called_should_fetchFromAPIAndMapResponse() {
        // Arrange
        DolarAPIGetCotacaoResponse mockApiResponse = mock(DolarAPIGetCotacaoResponse.class);
        GetDolarRateResponse mockMappedResponse = mock(GetDolarRateResponse.class);
        
        when(mockClientWrapper.getCotacaoUSD()).thenReturn(mockApiResponse);
        when(mockMapper.toGetDolarRateResponse(mockApiResponse)).thenReturn(mockMappedResponse);

        // Act
        GetDolarRateResponse result = service.execute();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(mockMappedResponse, result, "Should return the mapped response");
        verify(mockClientWrapper).getCotacaoUSD();
        verify(mockMapper).toGetDolarRateResponse(mockApiResponse);
    }
}
