package unit.psbds.demo.services.exchangerate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dollarapi.DollarAPIClientWrapper;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import psbds.demo.mappers.exchangerate.DollarAPIQuotationResponseMapping;
import psbds.demo.mappers.exchangerate.ExchangeRateMapper;
import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;
import psbds.demo.services.exchangerate.GetExchangeRateService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetExchangeRateServiceTest {

    private GetExchangeRateService service;
    private DollarAPIClientWrapper dollarAPIClientWrapper;
    private ExchangeRateMapper exchangeRateMapper;

    @BeforeEach
    void setUp() {
        dollarAPIClientWrapper = mock(DollarAPIClientWrapper.class);
        exchangeRateMapper = mock(ExchangeRateMapper.class);
        service = new GetExchangeRateService(dollarAPIClientWrapper, exchangeRateMapper);
    }

    @Test
    void getUSDExchangeRate_when_validQuotationReturned_should_returnMappedResponse() {
        // Arrange
        DollarAPIQuotationResponse apiResponse = mock(DollarAPIQuotationResponse.class);
        List<DollarAPIQuotationResponse> quotations = Arrays.asList(apiResponse);
        
        GetExchangeRateResponse expectedResponse = new GetExchangeRateResponse();
        expectedResponse.setCode("USD");
        expectedResponse.setBid(new BigDecimal("6.0987"));
        
        DollarAPIQuotationResponseMapping mockMapping = mock(DollarAPIQuotationResponseMapping.class);
        when(dollarAPIClientWrapper.getUSDQuotations()).thenReturn(quotations);
        when(exchangeRateMapper.getDollarAPIQuotationResponseMapping()).thenReturn(mockMapping);
        when(mockMapping.toGetExchangeRateResponse(apiResponse)).thenReturn(expectedResponse);

        // Act
        GetExchangeRateResponse result = service.getUSDExchangeRate();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("USD", result.getCode(), "Code should match expected value");
        assertEquals(new BigDecimal("6.0987"), result.getBid(), "Bid should match expected value");
        verify(dollarAPIClientWrapper).getUSDQuotations();
        verify(exchangeRateMapper).getDollarAPIQuotationResponseMapping();
        verify(mockMapping).toGetExchangeRateResponse(apiResponse);
    }

    @Test
    void getUSDExchangeRate_when_wrapperReturnsNull_should_returnNull() {
        // Arrange
        when(dollarAPIClientWrapper.getUSDQuotations()).thenReturn(null);

        // Act
        GetExchangeRateResponse result = service.getUSDExchangeRate();

        // Assert
        assertNull(result, "Result should be null when wrapper returns null");
        verify(dollarAPIClientWrapper).getUSDQuotations();
        verify(exchangeRateMapper, never()).getDollarAPIQuotationResponseMapping();
    }

    @Test
    void getUSDExchangeRate_when_wrapperReturnsEmptyList_should_returnNull() {
        // Arrange
        List<DollarAPIQuotationResponse> emptyList = new ArrayList<>();
        when(dollarAPIClientWrapper.getUSDQuotations()).thenReturn(emptyList);

        // Act
        GetExchangeRateResponse result = service.getUSDExchangeRate();

        // Assert
        assertNull(result, "Result should be null when wrapper returns empty list");
        verify(dollarAPIClientWrapper).getUSDQuotations();
        verify(exchangeRateMapper, never()).getDollarAPIQuotationResponseMapping();
    }

    @Test
    void getUSDExchangeRate_when_called_should_extractFirstElementFromList() {
        // Arrange
        DollarAPIQuotationResponse firstResponse = mock(DollarAPIQuotationResponse.class);
        DollarAPIQuotationResponse secondResponse = mock(DollarAPIQuotationResponse.class);
        List<DollarAPIQuotationResponse> quotations = Arrays.asList(firstResponse, secondResponse);
        
        GetExchangeRateResponse expectedResponse = new GetExchangeRateResponse();
        DollarAPIQuotationResponseMapping mockMapping = mock(DollarAPIQuotationResponseMapping.class);
        
        when(dollarAPIClientWrapper.getUSDQuotations()).thenReturn(quotations);
        when(exchangeRateMapper.getDollarAPIQuotationResponseMapping()).thenReturn(mockMapping);
        when(mockMapping.toGetExchangeRateResponse(firstResponse)).thenReturn(expectedResponse);

        // Act
        GetExchangeRateResponse result = service.getUSDExchangeRate();

        // Assert
        assertNotNull(result, "Result should not be null");
        verify(mockMapping).toGetExchangeRateResponse(firstResponse);
        verify(mockMapping, never()).toGetExchangeRateResponse(secondResponse);
    }
}
