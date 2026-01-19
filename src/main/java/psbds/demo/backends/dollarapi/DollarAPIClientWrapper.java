package psbds.demo.backends.dollarapi;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Wrapper for DollarAPIClient providing error handling and null safety.
 * Gracefully handles API unavailability by returning null for 404 responses.
 */
@ApplicationScoped
public class DollarAPIClientWrapper {

    private static final Logger LOG = Logger.getLogger(DollarAPIClientWrapper.class);

    private final DollarAPIClient client;

    @Inject
    public DollarAPIClientWrapper(@RestClient DollarAPIClient client) {
        this.client = client;
    }

    /**
     * Retrieves USD quotations from the Dollar API with error handling.
     *
     * @return List of DollarAPIQuotationResponse, or null if the API returns 404
     * @throws WebApplicationException for non-404 HTTP errors
     */
    @Nullable
    public List<DollarAPIQuotationResponse> getUSDQuotations() {
        try {
            LOG.info("Calling Dollar API to retrieve USD quotations");
            List<DollarAPIQuotationResponse> response = client.getUSDQuotations();
            LOG.info("Successfully retrieved USD quotations from Dollar API");
            return response;
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() == Status.NOT_FOUND.getStatusCode()) {
                LOG.warn("Dollar API returned 404 - quotations not found");
                return null;
            }
            LOG.error("Error calling Dollar API", ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error("Unexpected error calling Dollar API", ex);
            throw new RuntimeException("Failed to retrieve USD quotations", ex);
        }
    }
}
