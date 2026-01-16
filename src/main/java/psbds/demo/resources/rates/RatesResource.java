package psbds.demo.resources.rates;

import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import psbds.demo.services.rates.GetDolarRateService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1/rates")
public class RatesResource {

    private final GetDolarRateService getDolarRateService;

    @Inject
    public RatesResource(GetDolarRateService getDolarRateService) {
        this.getDolarRateService = getDolarRateService;
    }

    @GET
    @Path("/dolar")
    @Produces(MediaType.APPLICATION_JSON)
    public GetDolarRateResponse getDolarRate() {
        return getDolarRateService.execute();
    }
}
