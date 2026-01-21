package psbds.demo.resources.exchange;

import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;
import psbds.demo.services.exchange.GetUsdExchangeRateService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST resource for exchange rate operations.
 * <p>
 * Provides endpoints to retrieve current exchange rate information for various currencies.
 * </p>
 */
@Path("/exchange")
public class ExchangeResource {

    private final GetUsdExchangeRateService getUsdExchangeRateService;

    @Inject
    public ExchangeResource(GetUsdExchangeRateService getUsdExchangeRateService) {
        this.getUsdExchangeRateService = getUsdExchangeRateService;
    }

    /**
     * Retrieves the current USD to BRL exchange rate.
     * <p>
     * This endpoint fetches real-time exchange rate information from the DolarApi.com service,
     * including buy rate, sell rate, previous close rate, and last update timestamp.
     * </p>
     *
     * @return {@link GetUsdExchangeRateResponse} containing current USD/BRL exchange rate data
     * @throws jakarta.ws.rs.WebApplicationException with status 503 (Service Unavailable) 
     *         if the external exchange rate service is unavailable or returns an error
     */
    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    public GetUsdExchangeRateResponse getUsdExchangeRate() {
        return getUsdExchangeRateService.getUsdExchangeRate();
    }
}
