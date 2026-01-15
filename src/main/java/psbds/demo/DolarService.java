package psbds.demo;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import psbds.demo.backends.dolar.DolarAPIClientWrapper;
import psbds.demo.backends.dolar.model.usd.DolarAPIUsdResponse;

@ApplicationScoped
public class DolarService {
    
    @Inject
    DolarAPIClientWrapper dolarAPIClientWrapper;
    
    public @Nullable DolarAPIUsdResponse getUsdRate() {
        return dolarAPIClientWrapper.getUsdQuotation();
    }
}
