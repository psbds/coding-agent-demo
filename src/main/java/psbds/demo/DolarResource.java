package psbds.demo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/dolar")
public class DolarResource {
    @Inject
    DolarService dolarService;
    
    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    public DolarQuotation getUsdQuotation() {
        return dolarService.getUsdRate();
    }
}
