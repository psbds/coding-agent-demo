package psbds.demo.resources.exchange;

import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;
import psbds.demo.services.exchange.GetUsdExchangeRateService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/exchange")
public class ExchangeResource {

    private final GetUsdExchangeRateService getUsdExchangeRateService;

    @Inject
    public ExchangeResource(GetUsdExchangeRateService getUsdExchangeRateService) {
        this.getUsdExchangeRateService = getUsdExchangeRateService;
    }

    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    public GetUsdExchangeRateResponse getUsdExchangeRate() {
        return getUsdExchangeRateService.getUsdExchangeRate();
    }
}
