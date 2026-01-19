package unit.psbds.demo.backends.dolarapi;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.junit.jupiter.api.Test;
import psbds.demo.backends.dolarapi.DolarAPIClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class DolarAPIClientTest {

    @Test
    void testClassAnnotations() {
        // Arrange
        Class<DolarAPIClient> clientClass = DolarAPIClient.class;

        // Act
        RegisterRestClient registerRestClient = clientClass.getAnnotation(RegisterRestClient.class);

        // Assert
        assertNotNull(registerRestClient, "DolarAPIClient should have @RegisterRestClient annotation");
        assertEquals("dolarapi-api", registerRestClient.configKey(), 
            "DolarAPIClient should have correct configKey");
    }

    @Test
    void testGetCotacoesUsdMethodAnnotations() throws NoSuchMethodException {
        // Arrange
        Method method = DolarAPIClient.class.getMethod("getCotacoesUsd");

        // Act
        GET getAnnotation = method.getAnnotation(GET.class);
        Path pathAnnotation = method.getAnnotation(Path.class);
        Produces producesAnnotation = method.getAnnotation(Produces.class);

        // Assert
        assertNotNull(getAnnotation, "getCotacoesUsd method should have @GET annotation");
        assertNotNull(pathAnnotation, "getCotacoesUsd method should have @Path annotation");
        assertEquals("/v1/cotacoes/usd", pathAnnotation.value(), 
            "getCotacoesUsd method should have correct path");
        assertNotNull(producesAnnotation, "getCotacoesUsd method should have @Produces annotation");
        assertArrayEquals(new String[]{"application/json"}, producesAnnotation.value(), 
            "getCotacoesUsd method should produce application/json");
    }
}
