package psbds.demo;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import psbds.demo.backends.dolar.model.usd.DolarAPIUsdResponse;

@Path("/dolar")
public class DolarResource {
    
    private static final Logger LOG = Logger.getLogger(DolarResource.class);
    
    @Inject
    DolarService dolarService;
    
    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsdQuotation() {
        LOG.info("Received request for USD quotation");
        try {
            DolarAPIUsdResponse quotation = dolarService.getUsdRate();
            if (quotation == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"error\": \"USD quotation not found\"}")
                    .build();
            }
            return Response.ok(quotation).build();
        } catch (Exception e) {
            LOG.error("Error retrieving USD quotation", e);
            return Response.serverError()
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"Failed to retrieve USD quotation\"}")
                .build();
        }
    }
}
