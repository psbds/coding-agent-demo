package psbds.demo.backends.dolar.model.usd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DolarAPIUsdResponse {
    
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
    private LocalDateTime dataAtualizacao;
}
