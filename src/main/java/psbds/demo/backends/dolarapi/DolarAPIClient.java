package psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "dolarapi-api")
public interface DolarAPIClient {

    @GET
    @Path("/v1/cotacoes/usd")
    @Produces(MediaType.APPLICATION_JSON)
    DolarAPICotacoesUsdResponse getCotacoesUsd();
}
