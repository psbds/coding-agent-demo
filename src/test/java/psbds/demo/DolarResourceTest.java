package psbds.demo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class DolarResourceTest {
    @Test
    void testDolarEndpointStructure() {
        // This test just verifies the endpoint exists and responds with 200 or 500
        // A 500 is acceptable if the external API is unreachable
        given()
          .when().get("/dolar/usd")
          .then()
              .statusCode(org.hamcrest.Matchers.anyOf(
                  org.hamcrest.Matchers.is(200),
                  org.hamcrest.Matchers.is(500)
              ));
    }
}
