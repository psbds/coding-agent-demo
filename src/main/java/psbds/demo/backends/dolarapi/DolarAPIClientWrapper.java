package psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DolarAPIClientWrapper {

    private final DolarAPIClient client;

    @Inject
    public DolarAPIClientWrapper(@RestClient DolarAPIClient client) {
        this.client = client;
    }

    public @Nullable DolarAPIGetCotacaoResponse getCotacaoUSD() {
        return client.getCotacaoUSD();
    }
}
