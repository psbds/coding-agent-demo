package psbds.demo.resources.exchange;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import psbds.demo.resources.exchange.dto.ErrorResponse;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;
import psbds.demo.services.exchange.GetEuroExchangeRateService;

import java.util.Optional;

/**
 * REST resource for Euro exchange rate endpoints.
 * Provides EUR/BRL exchange rate information with Redis caching.
 */
@Path("/exchange")
public class ExchangeResource {

    private static final Logger LOG = Logger.getLogger(ExchangeResource.class);

    @Inject
    GetEuroExchangeRateService getEuroExchangeRateService;

    /**
     * GET endpoint to retrieve current EUR/BRL exchange rate.
     * Supports cache bypass via optional no-cache header.
     *
     * @param noCacheHeader optional header to bypass cache (default: false)
     * @return Response with exchange rate data or error
     */
    @GET
    @Path("/eur")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEuroExchangeRate(@HeaderParam("no-cache") String noCacheHeader) {
        LOG.info("Received request for EUR exchange rate");

        // Parse no-cache header
        boolean noCache = "true".equalsIgnoreCase(noCacheHeader);
        if (noCache) {
            LOG.debug("Cache bypass requested");
        }

        // Call service to get exchange rate
        Optional<GetEuroExchangeRateResponse> response = getEuroExchangeRateService.getEuroExchangeRate(noCache);

        if (response.isEmpty()) {
            LOG.error("Unable to retrieve EUR exchange rate");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(ErrorResponse.builder()
                            .error("Exchange rate service unavailable")
                            .message("Unable to retrieve exchange rates at this time")
                            .build())
                    .build();
        }

        LOG.info("Successfully returning EUR exchange rate");
        return Response.ok(response.get()).build();
    }
}
