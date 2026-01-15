package psbds.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class DolarService {
    @RestClient
    DolarApiClient dolarApiClient;
    
    public DolarQuotation getUsdRate() {
        return dolarApiClient.getUsdQuotation();
    }
}
