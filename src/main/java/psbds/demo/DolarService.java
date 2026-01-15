package psbds.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.jboss.logging.Logger;

import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class DolarService {
    
    private static final Logger LOG = Logger.getLogger(DolarService.class);
    
    @RestClient
    DolarApiClient dolarApiClient;
    
    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000)
    public DolarQuotation getUsdRate() {
        LOG.info("Fetching USD exchange rate from DolarAPI");
        DolarQuotation quotation = dolarApiClient.getUsdQuotation();
        LOG.infof("Successfully retrieved USD quotation: buy=%s, sell=%s", 
                 quotation.compra(), quotation.venda());
        return quotation;
    }
}
