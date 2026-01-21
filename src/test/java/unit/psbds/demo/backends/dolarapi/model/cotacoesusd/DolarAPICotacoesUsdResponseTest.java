package unit.psbds.demo.backends.dolarapi.model.cotacoesusd;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.model.cotacoesusd.DolarAPICotacoesUsdResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DolarAPICotacoesUsdResponseTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        DolarAPICotacoesUsdResponse response = new DolarAPICotacoesUsdResponse();
        String moeda = "USD";
        String nome = "Dólar";
        BigDecimal compra = new BigDecimal("5.371");
        BigDecimal venda = new BigDecimal("5.374");
        BigDecimal fechoAnterior = new BigDecimal("5.3694");
        String dataAtualizacao = "2026-01-16T19:02:00.000Z";

        // Act
        response.setMoeda(moeda);
        response.setNome(nome);
        response.setCompra(compra);
        response.setVenda(venda);
        response.setFechoAnterior(fechoAnterior);
        response.setDataAtualizacao(dataAtualizacao);

        // Assert
        assertEquals(moeda, response.getMoeda(), "getMoeda should return the value set by setMoeda");
        assertEquals(nome, response.getNome(), "getNome should return the value set by setNome");
        assertEquals(compra, response.getCompra(), "getCompra should return the value set by setCompra");
        assertEquals(venda, response.getVenda(), "getVenda should return the value set by setVenda");
        assertEquals(fechoAnterior, response.getFechoAnterior(), "getFechoAnterior should return the value set by setFechoAnterior");
        assertEquals(dataAtualizacao, response.getDataAtualizacao(), "getDataAtualizacao should return the value set by setDataAtualizacao");
    }

    @Test
    void testMoedaFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field moedaField = DolarAPICotacoesUsdResponse.class.getDeclaredField("moeda");

        // Act
        JsonProperty jsonProperty = moedaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "moeda field should have @JsonProperty annotation");
        assertEquals("moeda", jsonProperty.value(), "moeda field should have correct JsonProperty value");
    }

    @Test
    void testNomeFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field nomeField = DolarAPICotacoesUsdResponse.class.getDeclaredField("nome");

        // Act
        JsonProperty jsonProperty = nomeField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "nome field should have @JsonProperty annotation");
        assertEquals("nome", jsonProperty.value(), "nome field should have correct JsonProperty value");
    }

    @Test
    void testCompraFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field compraField = DolarAPICotacoesUsdResponse.class.getDeclaredField("compra");

        // Act
        JsonProperty jsonProperty = compraField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "compra field should have @JsonProperty annotation");
        assertEquals("compra", jsonProperty.value(), "compra field should have correct JsonProperty value");
    }

    @Test
    void testVendaFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field vendaField = DolarAPICotacoesUsdResponse.class.getDeclaredField("venda");

        // Act
        JsonProperty jsonProperty = vendaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "venda field should have @JsonProperty annotation");
        assertEquals("venda", jsonProperty.value(), "venda field should have correct JsonProperty value");
    }

    @Test
    void testFechoAnteriorFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field fechoAnteriorField = DolarAPICotacoesUsdResponse.class.getDeclaredField("fechoAnterior");

        // Act
        JsonProperty jsonProperty = fechoAnteriorField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "fechoAnterior field should have @JsonProperty annotation");
        assertEquals("fechoAnterior", jsonProperty.value(), "fechoAnterior field should have correct JsonProperty value");
    }

    @Test
    void testDataAtualizacaoFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field dataAtualizacaoField = DolarAPICotacoesUsdResponse.class.getDeclaredField("dataAtualizacao");

        // Act
        JsonProperty jsonProperty = dataAtualizacaoField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "dataAtualizacao field should have @JsonProperty annotation");
        assertEquals("dataAtualizacao", jsonProperty.value(), "dataAtualizacao field should have correct JsonProperty value");
    }
}
