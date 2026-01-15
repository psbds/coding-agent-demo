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
    void testDolarServiceIsInjectable() {
        // Verify that the DolarService can be injected and is not null
        assertNotNull(dolarService);
    }
}
