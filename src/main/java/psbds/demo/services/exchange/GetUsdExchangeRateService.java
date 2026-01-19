package psbds.demo.services.exchange;

import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;
import psbds.demo.mappers.exchange.ExchangeRateMapper;
import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class GetUsdExchangeRateService {

    private final DolarAPIClientWrapper dolarAPIClientWrapper;
    private final ExchangeRateMapper exchangeRateMapper;

    @Inject
    public GetUsdExchangeRateService(
            DolarAPIClientWrapper dolarAPIClientWrapper,
            ExchangeRateMapper exchangeRateMapper) {
        this.dolarAPIClientWrapper = dolarAPIClientWrapper;
        this.exchangeRateMapper = exchangeRateMapper;
    }

    public GetUsdExchangeRateResponse getUsdExchangeRate() {
        try {
            DolarAPICotacoesUsdResponse backendResponse = dolarAPIClientWrapper.getCotacoesUsd();
            
            if (backendResponse == null) {
                throw new WebApplicationException(
                    "Exchange rate service unavailable",
                    Response.Status.SERVICE_UNAVAILABLE
                );
            }
            
            return exchangeRateMapper.toGetUsdExchangeRateResponse(backendResponse);
        } catch (WebApplicationException ex) {
            // Re-throw WebApplicationException as-is
            if (ex.getResponse().getStatus() >= 500) {
                throw new WebApplicationException(
                    "Exchange rate service unavailable",
                    Response.Status.SERVICE_UNAVAILABLE
                );
            }
            throw ex;
        } catch (Exception ex) {
            throw new WebApplicationException(
                "Exchange rate service unavailable",
                Response.Status.SERVICE_UNAVAILABLE
            );
        }
    }
}
