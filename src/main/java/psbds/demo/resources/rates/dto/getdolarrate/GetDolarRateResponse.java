package psbds.demo.resources.rates.dto.getdolarrate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class GetDolarRateResponse {

    @JsonProperty("sell_rate")
    private BigDecimal sellRate;

    @JsonProperty("buy_rate")
    private BigDecimal buyRate;

    @JsonProperty("date")
    private ZonedDateTime date;
}
