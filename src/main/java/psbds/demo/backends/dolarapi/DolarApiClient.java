package psbds.demo.backends.dolarapi;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;

/**
 * REST client interface for DolarApi.com EUR exchange rate endpoint.
 * This client retrieves EUR/BRL exchange rate information.
 */
@RegisterRestClient(configKey = "dolarapi-api")
@Path("/v1/cotacoes")
public interface DolarApiClient {

    /**
     * Fetches current EUR/BRL exchange rate from DolarApi.com
     *
     * @return EuroExchangeRateApiResponse containing exchange rate information
     */
    @GET
    @Path("/eur")
    @Produces(MediaType.APPLICATION_JSON)
    EuroExchangeRateApiResponse getEuroExchangeRate();
}
