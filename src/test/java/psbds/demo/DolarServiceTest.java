package psbds.demo;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DolarServiceTest {
    @Inject
    DolarService dolarService;
    
    @Test
    void testGetUsdRate() {
        DolarQuotation quotation = dolarService.getUsdRate();
        
        assertNotNull(quotation);
        assertEquals("USD", quotation.moeda);
        assertNotNull(quotation.nome);
        assertNotNull(quotation.compra);
        assertNotNull(quotation.venda);
        assertNotNull(quotation.dataAtualizacao);
    }
}
