package psbds.demo.resources.exchangerate;

import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;
import psbds.demo.services.exchangerate.GetExchangeRateService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * REST resource for exchange rate endpoints.
 * Provides public API endpoints for retrieving exchange rate information.
 */
@Path("/exchange-rate")
public class ExchangeRateResource {

    private static final Logger LOG = Logger.getLogger(ExchangeRateResource.class);

    private final GetExchangeRateService getExchangeRateService;

    @Inject
    public ExchangeRateResource(GetExchangeRateService getExchangeRateService) {
        this.getExchangeRateService = getExchangeRateService;
    }

    /**
     * Retrieves the current USD exchange rate.
     *
     * @return Response containing the exchange rate data or 503 if service is unavailable
     */
    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUSDExchangeRate() {
        LOG.info("Received request for USD exchange rate");
        
        try {
            GetExchangeRateResponse exchangeRate = getExchangeRateService.getUSDExchangeRate();
            
            if (exchangeRate == null) {
                LOG.warn("External exchange rate service is unavailable");
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity("{\"error\": \"External exchange rate service is currently unavailable\", \"status\": 503}")
                        .build();
            }
            
            LOG.info("Successfully returning USD exchange rate");
            return Response.ok(exchangeRate).build();
            
        } catch (Exception ex) {
            LOG.error("Unexpected error retrieving USD exchange rate", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\", \"status\": 500}")
                    .build();
        }
    }
}
