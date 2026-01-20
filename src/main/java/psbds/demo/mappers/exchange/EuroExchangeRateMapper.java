package psbds.demo.mappers.exchange;

import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;
import psbds.demo.resources.exchange.dto.geteur.GetEurExchangeRateResponse;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper for transforming EUR exchange rate data between external API and public API formats
 * Maps Portuguese field names to English field names
 */
@ApplicationScoped
public class EuroExchangeRateMapper {

    /**
     * Map DolarAPI response to public API response
     * 
     * @param apiResponse DolarAPI response with Portuguese field names
     * @return Public API response with English field names
     */
    public GetEurExchangeRateResponse toPublicResponse(DolarApiAPIGetEurResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        return new GetEurExchangeRateResponse(
            apiResponse.getMoeda(),           // moeda -> currencyCode
            apiResponse.getNome(),            // nome -> currencyName
            apiResponse.getCompra(),          // compra -> buyRate
            apiResponse.getVenda(),           // venda -> sellRate
            apiResponse.getFechoAnterior(),   // fechoAnterior -> previousCloseRate
            apiResponse.getDataAtualizacao()  // dataAtualizacao -> lastUpdate
        );
    }
}
