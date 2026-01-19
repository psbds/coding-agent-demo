package psbds.demo.resources.exchangerate.dto.getexchangerate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Public API response DTO for the exchange rate endpoint.
 * Returns USD exchange rate information with proper data types.
 */
@Getter
@Setter
public class GetExchangeRateResponse {
    
    /**
     * Currency code (e.g., "USD")
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * Currency pair name (e.g., "Dólar Americano/Real Brasileiro")
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Buy price (bid)
     */
    @JsonProperty("bid")
    private BigDecimal bid;
    
    /**
     * Sell price (ask)
     */
    @JsonProperty("ask")
    private BigDecimal ask;
    
    /**
     * Highest exchange rate of the day
     */
    @JsonProperty("high")
    private BigDecimal high;
    
    /**
     * Lowest exchange rate of the day
     */
    @JsonProperty("low")
    private BigDecimal low;
    
    /**
     * Variation in the bid price
     */
    @JsonProperty("variation")
    private BigDecimal variation;
    
    /**
     * Percentage change
     */
    @JsonProperty("percentageChange")
    private BigDecimal percentageChange;
    
    /**
     * Unix timestamp of the quotation
     */
    @JsonProperty("timestamp")
    private String timestamp;
    
    /**
     * Human-readable creation date
     */
    @JsonProperty("createdDate")
    private String createdDate;
}
