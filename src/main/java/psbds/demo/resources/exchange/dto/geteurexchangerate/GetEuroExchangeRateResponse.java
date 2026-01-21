package psbds.demo.resources.exchange.dto.geteurexchangerate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for the GET /exchange/eur endpoint.
 * Provides EUR/BRL exchange rate information with English field names.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEuroExchangeRateResponse {

    /**
     * Currency code (always "EUR")
     */
    @JsonProperty("currencyCode")
    private String currencyCode;

    /**
     * Currency name (always "Euro")
     */
    @JsonProperty("currencyName")
    private String currencyName;

    /**
     * Buy rate (rate at which you can buy EUR)
     */
    @JsonProperty("buyRate")
    private BigDecimal buyRate;

    /**
     * Sell rate (rate at which you can sell EUR)
     */
    @JsonProperty("sellRate")
    private BigDecimal sellRate;

    /**
     * Previous closing rate
     */
    @JsonProperty("previousCloseRate")
    private BigDecimal previousCloseRate;

    /**
     * Timestamp of last update (ISO 8601 format)
     */
    @JsonProperty("lastUpdate")
    private String lastUpdate;
}
