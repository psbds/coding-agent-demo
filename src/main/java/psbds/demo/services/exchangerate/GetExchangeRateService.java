package psbds.demo.services.exchangerate;

import psbds.demo.backends.dollarapi.DollarAPIClientWrapper;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import psbds.demo.mappers.exchangerate.ExchangeRateMapper;
import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Service for retrieving and transforming USD exchange rate data.
 * Orchestrates calls to the Dollar API client and applies necessary transformations.
 */
@ApplicationScoped
public class GetExchangeRateService {

    private static final Logger LOG = Logger.getLogger(GetExchangeRateService.class);

    private final DollarAPIClientWrapper dollarAPIClientWrapper;
    private final ExchangeRateMapper exchangeRateMapper;

    @Inject
    public GetExchangeRateService(DollarAPIClientWrapper dollarAPIClientWrapper,
                                  ExchangeRateMapper exchangeRateMapper) {
        this.dollarAPIClientWrapper = dollarAPIClientWrapper;
        this.exchangeRateMapper = exchangeRateMapper;
    }

    /**
     * Retrieves the current USD exchange rate.
     * The Dollar API returns a list with a single element containing the current USD quotation.
     *
     * @return GetExchangeRateResponse containing the exchange rate data, or null if unavailable
     */
    @Nullable
    public GetExchangeRateResponse getUSDExchangeRate() {
        LOG.info("Retrieving USD exchange rate");
        
        List<DollarAPIQuotationResponse> quotations = dollarAPIClientWrapper.getUSDQuotations();
        
        if (quotations == null || quotations.isEmpty()) {
            LOG.warn("No USD quotations available from Dollar API");
            return null;
        }

        // The API returns an array with a single element
        DollarAPIQuotationResponse quotation = quotations.get(0);
        
        GetExchangeRateResponse response = exchangeRateMapper
                .getDollarAPIQuotationResponseMapping()
                .toGetExchangeRateResponse(quotation);
        
        LOG.info("Successfully retrieved and mapped USD exchange rate");
        return response;
    }
}
