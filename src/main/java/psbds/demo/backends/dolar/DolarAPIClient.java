package psbds.demo.backends.dolar;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import psbds.demo.backends.dolar.model.usd.DolarAPIUsdResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "dolar-api")
public interface DolarAPIClient {

    @GET
    @Path("/v1/cotacoes/usd")
    @Produces(MediaType.APPLICATION_JSON)
    DolarAPIUsdResponse getUsdQuotation();
}
