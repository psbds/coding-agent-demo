package psbds.demo.backends.dolarapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing the response from DolarApi.com EUR endpoint.
 * Maps Portuguese field names from the external API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EuroExchangeRateApiResponse {

    /**
     * Currency code (always "EUR")
     */
    @JsonProperty("moeda")
    private String moeda;

    /**
     * Currency name in Portuguese (always "Euro")
     */
    @JsonProperty("nome")
    private String nome;

    /**
     * Buy rate (rate at which you can buy EUR)
     */
    @JsonProperty("compra")
    private BigDecimal compra;

    /**
     * Sell rate (rate at which you can sell EUR)
     */
    @JsonProperty("venda")
    private BigDecimal venda;

    /**
     * Previous closing rate
     */
    @JsonProperty("fechoAnterior")
    private BigDecimal fechoAnterior;

    /**
     * Timestamp of last update (ISO 8601 format)
     */
    @JsonProperty("dataAtualizacao")
    private String dataAtualizacao;
}
