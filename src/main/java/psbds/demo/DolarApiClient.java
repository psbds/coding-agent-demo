package psbds.demo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/cotacoes/usd")
@RegisterRestClient(configKey = "dolar-api")
public interface DolarApiClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    DolarQuotation getUsdQuotation();
}
