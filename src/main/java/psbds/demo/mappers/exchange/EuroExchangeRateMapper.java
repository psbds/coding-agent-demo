package psbds.demo.mappers.exchange;

import jakarta.enterprise.context.ApplicationScoped;
import psbds.demo.backends.dolarapi.dto.EuroExchangeRateApiResponse;
import psbds.demo.resources.exchange.dto.geteurexchangerate.GetEuroExchangeRateResponse;

/**
 * Mapper for transforming EuroExchangeRateApiResponse to GetEuroExchangeRateResponse.
 * Maps Portuguese field names from external API to English field names for public API.
 */
@ApplicationScoped
public class EuroExchangeRateMapper {

    /**
     * Maps external API response to public API response.
     * Transforms Portuguese field names to English equivalents.
     *
     * @param apiResponse the response from DolarApi.com
     * @return GetEuroExchangeRateResponse for public endpoint
     */
    public GetEuroExchangeRateResponse toPublicResponse(EuroExchangeRateApiResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        return GetEuroExchangeRateResponse.builder()
                .currencyCode(apiResponse.getMoeda())
                .currencyName(apiResponse.getNome())
                .buyRate(apiResponse.getCompra())
                .sellRate(apiResponse.getVenda())
                .previousCloseRate(apiResponse.getFechoAnterior())
                .lastUpdate(apiResponse.getDataAtualizacao())
                .build();
    }
}
