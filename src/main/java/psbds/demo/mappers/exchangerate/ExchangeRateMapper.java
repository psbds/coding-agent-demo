package psbds.demo.mappers.exchangerate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Aggregator mapper for exchange rate related mappings.
 * Coordinates all mapping operations for the exchange rate feature.
 */
@ApplicationScoped
public class ExchangeRateMapper {

    private final DollarAPIQuotationResponseMapping dollarAPIQuotationResponseMapping;

    @Inject
    public ExchangeRateMapper(DollarAPIQuotationResponseMapping dollarAPIQuotationResponseMapping) {
        this.dollarAPIQuotationResponseMapping = dollarAPIQuotationResponseMapping;
    }

    /**
     * Returns the mapping instance for DollarAPI quotation responses.
     *
     * @return DollarAPIQuotationResponseMapping instance
     */
    public DollarAPIQuotationResponseMapping getDollarAPIQuotationResponseMapping() {
        return dollarAPIQuotationResponseMapping;
    }
}
