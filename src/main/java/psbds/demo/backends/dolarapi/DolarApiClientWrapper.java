package psbds.demo.backends.dolarapi;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;

import java.util.Optional;

/**
 * Wrapper class for DolarApiClient that provides error handling and null safety.
 * Handles exceptions from the external API gracefully.
 */
@ApplicationScoped
public class DolarApiClientWrapper {

    private static final Logger LOG = Logger.getLogger(DolarApiClientWrapper.class);

    @RestClient
    DolarApiClient dolarApiClient;

    /**
     * Retrieves EUR/BRL exchange rate with error handling.
     *
     * @return Optional containing EuroExchangeRateApiResponse if successful, empty otherwise
     */
    public Optional<EuroExchangeRateApiResponse> getEuroExchangeRate() {
        try {
            LOG.info("Calling DolarApi.com to fetch EUR exchange rate");
            EuroExchangeRateApiResponse response = dolarApiClient.getEuroExchangeRate();
            
            if (response == null) {
                LOG.warn("DolarApi.com returned null response for EUR exchange rate");
                return Optional.empty();
            }
            
            LOG.info("Successfully fetched EUR exchange rate from DolarApi.com");
            return Optional.of(response);
            
        } catch (Exception e) {
            LOG.error("Error fetching EUR exchange rate from DolarApi.com: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
}
