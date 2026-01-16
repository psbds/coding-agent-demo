package psbds.demo.services.rates;

import psbds.demo.backends.dolarapi.DolarAPIClientWrapper;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;
import psbds.demo.mappers.rates.DolarRateMapper;
import psbds.demo.resources.rates.dto.getdolarrate.GetDolarRateResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetDolarRateService {

    private final DolarAPIClientWrapper dolarAPIClientWrapper;
    private final DolarRateMapper dolarRateMapper;

    @Inject
    public GetDolarRateService(DolarAPIClientWrapper dolarAPIClientWrapper, DolarRateMapper dolarRateMapper) {
        this.dolarAPIClientWrapper = dolarAPIClientWrapper;
        this.dolarRateMapper = dolarRateMapper;
    }

    public GetDolarRateResponse execute() {
        DolarAPIGetCotacaoResponse apiResponse = dolarAPIClientWrapper.getCotacaoUSD();
        return dolarRateMapper.toGetDolarRateResponse(apiResponse);
    }
}
