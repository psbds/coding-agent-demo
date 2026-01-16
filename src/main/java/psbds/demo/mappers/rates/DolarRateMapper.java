package psbds.demo.mappers.rates;

import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DolarRateMapper {

    private final GetDolarRateResponseMapping getDolarRateResponseMapping;

    @Inject
    public DolarRateMapper(GetDolarRateResponseMapping getDolarRateResponseMapping) {
        this.getDolarRateResponseMapping = getDolarRateResponseMapping;
    }

    public GetDolarRateResponse toGetDolarRateResponse(DolarAPIGetCotacaoResponse apiResponse) {
        return getDolarRateResponseMapping.map(apiResponse);
    }
}
