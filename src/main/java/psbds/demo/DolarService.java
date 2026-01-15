package psbds.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import psbds.demo.backends.dolar.DolarAPIClientWrapper;
import psbds.demo.backends.dolar.model.usd.DolarAPIUsdResponse;

@ApplicationScoped
public class DolarService {
    
    @Inject
    DolarAPIClientWrapper dolarAPIClientWrapper;
    
    public DolarAPIUsdResponse getUsdRate() {
        return dolarAPIClientWrapper.getUsdQuotation();
    }
}
