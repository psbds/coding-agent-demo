package unit.psbds.demo.backends.dolarapi.model.geteur;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import psbds.demo.backends.dolarapi.model.geteur.DolarApiAPIGetEurResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;

class DolarApiAPIGetEurResponseTest {

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String moeda = "EUR";
        String nome = "Euro";
        BigDecimal compra = new BigDecimal("6.125");
        BigDecimal venda = new BigDecimal("6.129");
        BigDecimal fechoAnterior = new BigDecimal("6.118");
        String dataAtualizacao = "2026-01-20T14:30:00.000Z";

        // Act
        DolarApiAPIGetEurResponse response = new DolarApiAPIGetEurResponse(
            moeda, nome, compra, venda, fechoAnterior, dataAtualizacao);

        // Assert
        assertEquals(moeda, response.getMoeda(), 
            "moeda should be set correctly by constructor");
        assertEquals(nome, response.getNome(), 
            "nome should be set correctly by constructor");
        assertEquals(compra, response.getCompra(), 
            "compra should be set correctly by constructor");
        assertEquals(venda, response.getVenda(), 
            "venda should be set correctly by constructor");
        assertEquals(fechoAnterior, response.getFechoAnterior(), 
            "fechoAnterior should be set correctly by constructor");
        assertEquals(dataAtualizacao, response.getDataAtualizacao(), 
            "dataAtualizacao should be set correctly by constructor");
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        DolarApiAPIGetEurResponse response = new DolarApiAPIGetEurResponse();
        String moeda = "EUR";
        String nome = "Euro";
        BigDecimal compra = new BigDecimal("6.125");
        BigDecimal venda = new BigDecimal("6.129");
        BigDecimal fechoAnterior = new BigDecimal("6.118");
        String dataAtualizacao = "2026-01-20T14:30:00.000Z";

        // Act
        response.setMoeda(moeda);
        response.setNome(nome);
        response.setCompra(compra);
        response.setVenda(venda);
        response.setFechoAnterior(fechoAnterior);
        response.setDataAtualizacao(dataAtualizacao);

        // Assert
        assertEquals(moeda, response.getMoeda(), 
            "getMoeda should return the value set by setMoeda");
        assertEquals(nome, response.getNome(), 
            "getNome should return the value set by setNome");
        assertEquals(compra, response.getCompra(), 
            "getCompra should return the value set by setCompra");
        assertEquals(venda, response.getVenda(), 
            "getVenda should return the value set by setVenda");
        assertEquals(fechoAnterior, response.getFechoAnterior(), 
            "getFechoAnterior should return the value set by setFechoAnterior");
        assertEquals(dataAtualizacao, response.getDataAtualizacao(), 
            "getDataAtualizacao should return the value set by setDataAtualizacao");
    }

    @Test
    void testMoedaFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field moedaField = DolarApiAPIGetEurResponse.class.getDeclaredField("moeda");

        // Act
        JsonProperty jsonProperty = moedaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "moeda field should have @JsonProperty annotation");
        assertEquals("moeda", jsonProperty.value(), 
            "moeda field should have correct JsonProperty value");
    }

    @Test
    void testNomeFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field nomeField = DolarApiAPIGetEurResponse.class.getDeclaredField("nome");

        // Act
        JsonProperty jsonProperty = nomeField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "nome field should have @JsonProperty annotation");
        assertEquals("nome", jsonProperty.value(), 
            "nome field should have correct JsonProperty value");
    }

    @Test
    void testCompraFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field compraField = DolarApiAPIGetEurResponse.class.getDeclaredField("compra");

        // Act
        JsonProperty jsonProperty = compraField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "compra field should have @JsonProperty annotation");
        assertEquals("compra", jsonProperty.value(), 
            "compra field should have correct JsonProperty value");
    }

    @Test
    void testVendaFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field vendaField = DolarApiAPIGetEurResponse.class.getDeclaredField("venda");

        // Act
        JsonProperty jsonProperty = vendaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "venda field should have @JsonProperty annotation");
        assertEquals("venda", jsonProperty.value(), 
            "venda field should have correct JsonProperty value");
    }

    @Test
    void testFechoAnteriorFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field fechoAnteriorField = DolarApiAPIGetEurResponse.class.getDeclaredField("fechoAnterior");

        // Act
        JsonProperty jsonProperty = fechoAnteriorField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "fechoAnterior field should have @JsonProperty annotation");
        assertEquals("fechoAnterior", jsonProperty.value(), 
            "fechoAnterior field should have correct JsonProperty value");
    }

    @Test
    void testDataAtualizacaoFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field dataAtualizacaoField = DolarApiAPIGetEurResponse.class.getDeclaredField("dataAtualizacao");

        // Act
        JsonProperty jsonProperty = dataAtualizacaoField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "dataAtualizacao field should have @JsonProperty annotation");
        assertEquals("dataAtualizacao", jsonProperty.value(), 
            "dataAtualizacao field should have correct JsonProperty value");
    }
}
