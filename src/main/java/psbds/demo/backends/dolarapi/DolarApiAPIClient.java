package psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST client interface for DolarAPI.com Brazil service
 * Fetches EUR/BRL exchange rates from the public DolarAPI
 * 
 * Note: This is a public API and does NOT require authentication,
 * so @OidcClientFilter is not used.
 */
@RegisterRestClient(configKey = "dolarapi-api")
public interface DolarApiAPIClient {

    /**
     * Get current EUR/BRL exchange rate
     * 
     * @return EUR exchange rate information
     */
    @GET
    @Path("/v1/cotacoes/eur")
    @Produces(MediaType.APPLICATION_JSON)
    DolarApiAPIGetEurResponse getEur();
}
