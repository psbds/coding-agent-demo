package psbds.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DolarQuotation(
    String moeda,
    String nome,
    BigDecimal compra,
    BigDecimal venda,
    BigDecimal fechoAnterior,
    LocalDateTime dataAtualizacao
) {
}
