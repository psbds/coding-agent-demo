package psbds.demo.mappers.exchangerate;

import psbds.demo.backends.dollarapi.model.quotation.DollarAPIQuotationResponse;
import psbds.demo.resources.exchangerate.dto.getexchangerate.GetExchangeRateResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

/**
 * Mapper for transforming DollarAPIQuotationResponse to GetExchangeRateResponse.
 * Handles data type conversions from String to BigDecimal.
 */
@ApplicationScoped
public class DollarAPIQuotationResponseMapping {

    /**
     * Converts a DollarAPIQuotationResponse to a GetExchangeRateResponse.
     *
     * @param apiResponse The external API response
     * @return The public API response DTO
     */
    public GetExchangeRateResponse toGetExchangeRateResponse(DollarAPIQuotationResponse apiResponse) {
        if (apiResponse == null) {
            return null;
        }

        GetExchangeRateResponse response = new GetExchangeRateResponse();
        response.setCode(apiResponse.getCode());
        response.setName(apiResponse.getName());
        response.setBid(parseStringToBigDecimal(apiResponse.getBid()));
        response.setAsk(parseStringToBigDecimal(apiResponse.getAsk()));
        response.setHigh(parseStringToBigDecimal(apiResponse.getHigh()));
        response.setLow(parseStringToBigDecimal(apiResponse.getLow()));
        response.setVariation(parseStringToBigDecimal(apiResponse.getVarBid()));
        response.setPercentageChange(parseStringToBigDecimal(apiResponse.getPctChange()));
        response.setTimestamp(apiResponse.getTimestamp());
        response.setCreatedDate(apiResponse.getCreateDate());

        return response;
    }

    /**
     * Safely parses a String to BigDecimal, returning null if the string is null or empty.
     *
     * @param value The string value to parse
     * @return BigDecimal representation or null
     */
    private BigDecimal parseStringToBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
