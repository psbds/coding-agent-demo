package psbds.demo.backends.dollarapi;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST client interface for the Brazilian Dollar API.
 * This is a public API and does not require authentication.
 */
@RegisterRestClient(configKey = "dollar-api")
public interface DollarAPIClient {

    /**
     * Retrieves the current USD quotations from the Dollar API.
     * The API returns an array with a single element containing the current USD exchange rate.
     *
     * @return List of DollarAPIQuotationResponse containing USD exchange rate information
     */
    @GET
    @Path("/v1/cotacoes/usd")
    @Produces(MediaType.APPLICATION_JSON)
    List<DollarAPIQuotationResponse> getUSDQuotations();
}
