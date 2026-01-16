package psbds.demo.backends.dolarapi.model.getcotacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DolarAPIGetCotacaoResponse {

    @JsonProperty("moeda")
    private String moeda;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("compra")
    private BigDecimal compra;

    @JsonProperty("venda")
    private BigDecimal venda;

    @JsonProperty("fechoAnterior")
    private BigDecimal fechoAnterior;

    @JsonProperty("dataAtualizacao")
    private ZonedDateTime dataAtualizacao;
}
