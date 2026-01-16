package psbds.demo.mappers.rates;

import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.ZonedDateTime;

@ApplicationScoped
public class GetDolarRateResponseMapping {

    public GetDolarRateResponse map(DolarAPIGetCotacaoResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        GetDolarRateResponse response = new GetDolarRateResponse();
        response.setSellRate(apiResponse.getVenda());
        response.setBuyRate(apiResponse.getCompra());
        response.setDate(apiResponse.getDataAtualizacao());

        return response;
    }
}
