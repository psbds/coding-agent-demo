package psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

/**
 * Wrapper class for DolarApiAPIClient providing error handling and null safety
 */
@ApplicationScoped
public class DolarApiAPIClientWrapper {

    private DolarApiAPIClient client;

    @Inject
    public DolarApiAPIClientWrapper(@RestClient DolarApiAPIClient client) {
        this.client = client;
    }

    /**
     * Get current EUR/BRL exchange rate with error handling
     * 
     * @return EUR exchange rate information, or null if an error occurs
     * @throws WebApplicationException if a non-404 HTTP error occurs
     */
    public @Nullable DolarApiAPIGetEurResponse getEur() {
        try {
            return client.getEur();
        } catch (WebApplicationException ex) {
            throw ex;
        }
    }
}
