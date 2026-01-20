package psbds.demo.backends.dolarapi.model.geteur;

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
public class DolarApiAPIGetEurResponse {
    
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
    private String dataAtualizacao;
}
