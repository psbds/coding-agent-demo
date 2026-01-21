package psbds.demo.resources.exchange.dto.getusd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsdExchangeRateResponse {
    
    @JsonProperty("currencyCode")
    private String currencyCode;
    
    @JsonProperty("currencyName")
    private String currencyName;
    
    @JsonProperty("buyRate")
    private BigDecimal buyRate;
    
    @JsonProperty("sellRate")
    private BigDecimal sellRate;
    
    @JsonProperty("previousCloseRate")
    private BigDecimal previousCloseRate;
    
    @JsonProperty("lastUpdate")
    private String lastUpdate;
}
