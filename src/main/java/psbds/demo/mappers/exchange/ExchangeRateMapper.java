package psbds.demo.mappers.exchange;

import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;
import psbds.demo.resources.exchange.dto.getusd.GetUsdExchangeRateResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExchangeRateMapper {

    public GetUsdExchangeRateResponse toGetUsdExchangeRateResponse(DolarAPICotacoesUsdResponse backendResponse) {
        if (backendResponse == null) {
            return null;
        }
        
        GetUsdExchangeRateResponse response = new GetUsdExchangeRateResponse();
        response.setCurrencyCode(backendResponse.getMoeda());
        response.setCurrencyName(backendResponse.getNome());
        response.setBuyRate(backendResponse.getCompra());
        response.setSellRate(backendResponse.getVenda());
        response.setPreviousCloseRate(backendResponse.getFechoAnterior());
        response.setLastUpdate(backendResponse.getDataAtualizacao());
        
        return response;
    }
}
