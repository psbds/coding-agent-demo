package psbds.demo.backends.dolar;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import psbds.demo.backends.dolar.model.usd.DolarAPIUsdResponse;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class DolarAPIClientWrapper {

    private static final Logger LOG = Logger.getLogger(DolarAPIClientWrapper.class);
    
    private DolarAPIClient client;

    @Inject
    public DolarAPIClientWrapper(@RestClient DolarAPIClient client) {
        this.client = client;
    }

    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000)
    public @Nullable DolarAPIUsdResponse getUsdQuotation() {
        LOG.info("Fetching USD exchange rate from DolarAPI");
        try {
            DolarAPIUsdResponse response = client.getUsdQuotation();
            LOG.infof("Successfully retrieved USD quotation: buy=%s, sell=%s", 
                     response.getCompra(), response.getVenda());
            return response;
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() == 404) {
                LOG.warn("USD quotation not found (404)");
                return null;
            }
            LOG.errorf(ex, "Failed to fetch USD quotation from DolarAPI");
            throw ex;
        }
    }
}
