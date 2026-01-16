package unit.psbds.demo.backends.dolarapi.model.getcotacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.model.getcotacao.DolarAPIGetCotacaoResponse;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DolarAPIGetCotacaoResponseTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        DolarAPIGetCotacaoResponse response = new DolarAPIGetCotacaoResponse();
        String moeda = "USD";
        String nome = "Dólar";
        BigDecimal compra = new BigDecimal("5.3848");
        BigDecimal venda = new BigDecimal("5.3857");
        BigDecimal fechoAnterior = new BigDecimal("5.3967");
        ZonedDateTime dataAtualizacao = ZonedDateTime.now();

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
        Field moedaField = DolarAPIGetCotacaoResponse.class.getDeclaredField("moeda");

        // Act
        JsonProperty jsonProperty = moedaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "moeda field should have @JsonProperty annotation");
        assertEquals("moeda", jsonProperty.value(), "moeda field should have correct JsonProperty value");
    }

    @Test
    void testNomeFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field nomeField = DolarAPIGetCotacaoResponse.class.getDeclaredField("nome");

        // Act
        JsonProperty jsonProperty = nomeField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "nome field should have @JsonProperty annotation");
        assertEquals("nome", jsonProperty.value(), "nome field should have correct JsonProperty value");
    }

    @Test
    void testCompraFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field compraField = DolarAPIGetCotacaoResponse.class.getDeclaredField("compra");

        // Act
        JsonProperty jsonProperty = compraField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "compra field should have @JsonProperty annotation");
        assertEquals("compra", jsonProperty.value(), "compra field should have correct JsonProperty value");
    }

    @Test
    void testVendaFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field vendaField = DolarAPIGetCotacaoResponse.class.getDeclaredField("venda");

        // Act
        JsonProperty jsonProperty = vendaField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "venda field should have @JsonProperty annotation");
        assertEquals("venda", jsonProperty.value(), "venda field should have correct JsonProperty value");
    }

    @Test
    void testFechoAnteriorFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field fechoAnteriorField = DolarAPIGetCotacaoResponse.class.getDeclaredField("fechoAnterior");

        // Act
        JsonProperty jsonProperty = fechoAnteriorField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "fechoAnterior field should have @JsonProperty annotation");
        assertEquals("fechoAnterior", jsonProperty.value(), "fechoAnterior field should have correct JsonProperty value");
    }

    @Test
    void testDataAtualizacaoFieldAnnotations() throws NoSuchFieldException {
        // Arrange
        Field dataAtualizacaoField = DolarAPIGetCotacaoResponse.class.getDeclaredField("dataAtualizacao");

        // Act
        JsonProperty jsonProperty = dataAtualizacaoField.getAnnotation(JsonProperty.class);

        // Assert
        assertNotNull(jsonProperty, "dataAtualizacao field should have @JsonProperty annotation");
        assertEquals("dataAtualizacao", jsonProperty.value(), "dataAtualizacao field should have correct JsonProperty value");
    }
}
