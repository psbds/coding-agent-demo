package psbds.demo.resources.exchange;

import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;
import psbds.demo.services.exchange.GetEuroExchangeRateService;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API endpoint for EUR/BRL exchange rates
 */
@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class EuroExchangeRateResource {

    @Inject
    GetEuroExchangeRateService getEuroExchangeRateService;

    /**
     * Get current EUR/BRL exchange rate
     * 
     * @param noCache When true, bypasses cache and fetches fresh data
     * @return EUR exchange rate information or 503 if service unavailable
     */
    @GET
    @Path("/eur")
    public Response getEurExchangeRate(@HeaderParam("no-cache") @DefaultValue("false") Boolean noCache) {
        try {
            GetEurExchangeRateResponse response = getEuroExchangeRateService.getEuroExchangeRate(noCache);
            
            if (response == null) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\":\"Exchange rate service unavailable\",\"message\":\"Unable to retrieve exchange rates at this time\"}")
                    .build();
            }
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("{\"error\":\"Exchange rate service unavailable\",\"message\":\"Unable to retrieve exchange rates at this time\"}")
                .build();
        }
    }
}
