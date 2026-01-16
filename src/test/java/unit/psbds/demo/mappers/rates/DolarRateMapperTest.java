package unit.psbds.demo.mappers.rates;

import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.mappers.rates.DolarRateMapper;
import psbds.demo.mappers.rates.GetDolarRateResponseMapping;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusComponentTest
class DolarRateMapperTest {

    @Inject
    DolarRateMapper mapper;

    @InjectMock
    GetDolarRateResponseMapping mockResponseMapping;

    @Test
    void toGetDolarRateResponse_when_apiResponseProvided_should_delegateToMapping() {
        // Arrange
        DolarAPIGetCotacaoResponse apiResponse = mock(DolarAPIGetCotacaoResponse.class);
        GetDolarRateResponse expectedResponse = mock(GetDolarRateResponse.class);
        when(mockResponseMapping.map(apiResponse)).thenReturn(expectedResponse);

        // Act
        GetDolarRateResponse result = mapper.toGetDolarRateResponse(apiResponse);

        // Assert
        assertEquals(expectedResponse, result, "Should return the mapped response from the mapping");
        verify(mockResponseMapping).map(apiResponse);
    }
}
