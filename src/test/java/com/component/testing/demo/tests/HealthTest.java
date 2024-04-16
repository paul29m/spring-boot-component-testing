package com.component.testing.demo.tests;

import com.component.testing.demo.config.BaseRestAssuredIntegrationTest;
import com.component.testing.demo.config.KafkaContainerConfig;
import com.component.testing.demo.config.PgContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {},
        classes = {KafkaContainerConfig.class, PgContainerConfig.class}
)
public class HealthTest extends BaseRestAssuredIntegrationTest {

    @BeforeEach
    public void setUp() {
        this.setUpAbstractIntegrationTest();
    }

    @Test
    public void healthy() {
        given(requestSpecification)
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200);
    }
}
