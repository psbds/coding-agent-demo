package psbds.demo.backends.dollarapi.model.quotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the response from the Dollar API quotation endpoint.
 * Maps to the JSON structure returned by https://br.dolarapi.com/v1/cotacoes/usd
 */
@Getter
@Setter
public class DollarAPIQuotationResponse {
    
    /**
     * Currency code (e.g., "USD")
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * Target currency code (e.g., "BRL")
     */
    @JsonProperty("codein")
    private String codein;
    
    /**
     * Currency pair name (e.g., "Dólar Americano/Real Brasileiro")
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * Highest exchange rate of the day
     */
    @JsonProperty("high")
    private String high;
    
    /**
     * Lowest exchange rate of the day
     */
    @JsonProperty("low")
    private String low;
    
    /**
     * Variation in the bid price
     */
    @JsonProperty("varBid")
    private String varBid;
    
    /**
     * Percentage change
     */
    @JsonProperty("pctChange")
    private String pctChange;
    
    /**
     * Buy price (bid)
     */
    @JsonProperty("bid")
    private String bid;
    
    /**
     * Sell price (ask)
     */
    @JsonProperty("ask")
    private String ask;
    
    /**
     * Unix timestamp of the quotation
     */
    @JsonProperty("timestamp")
    private String timestamp;
    
    /**
     * Human-readable creation date
     */
    @JsonProperty("create_date")
    private String createDate;
}
