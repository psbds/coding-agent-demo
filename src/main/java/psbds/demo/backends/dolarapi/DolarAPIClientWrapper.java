package psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class DolarAPIClientWrapper {

    private DolarAPIClient client;

    @Inject
    public DolarAPIClientWrapper(@RestClient DolarAPIClient client) {
        this.client = client;
    }

    public @Nullable DolarAPICotacoesUsdResponse getCotacoesUsd() {
        try {
            return client.getCotacoesUsd();
        } catch (WebApplicationException ex) {
            throw ex;
        }
    }
}
