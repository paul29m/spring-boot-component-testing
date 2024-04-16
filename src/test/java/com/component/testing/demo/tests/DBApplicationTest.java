package com.component.testing.demo.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.component.testing.demo.config.BaseRestAssuredIntegrationTest;
import com.component.testing.demo.config.PgContainerConfig;
import com.component.testing.demo.entity.Application;
import com.component.testing.demo.helper.IKafkaTemplate;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = {PgContainerConfig.class}
)
public class DBApplicationTest extends BaseRestAssuredIntegrationTest {

    @MockBean
    private IKafkaTemplate kafkaTemplateProducer;

    @BeforeEach
    public void setUp() {
        this.setUpAbstractIntegrationTest();
    }

    /**
     * Test case to add an application.
     * Sends a POST request with an application body and expects a status code of 201.
     */
    @Test
    public void addApplication() {
        given(requestSpecification)
            .body("""
                {
                    "name": "Test Application add",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201))
            .body("id", notNullValue())
            .body("name", is("Test Application add"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to add an application that already exists
     * Sends a POST request with an application body and expects a status code of 409.
     */
    @Test
    public void addApplicationAlreadyExists() {
        given(requestSpecification)
            .body("""
                {
                    "name": "Test Application existing",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201));

        given(requestSpecification)
            .body("""
                {
                    "name": "Test Application existing",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(409));
    }

    /**
     * Test case to verify the functionality of finding an application.
     * Adds an application and then sends a GET request to find it.
     * Expects the body of the response to match the added application.
     */
    @Test
    public void findApplication() {
        Response responseContent = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application find",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application response = responseContent.body().as(Application.class);

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .body("id", is(response.getId()))
            .body("name", is("Test Application find"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to update an application.
     * Adds an application and then sends a PUT request with an updated application body.
     * Expects a status code of 200.
     */
    @Test
    public void updateApplication() {
        Response responseContent  = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application update",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application response = responseContent.body().as(Application.class);

        given(requestSpecification)
            .body("{" +
                "\"id\": \" "+ response.getId() + "\"," +
                "\"name\": \"Updated Application\"," +
                "\"description\" : \"An updated application.\"," +
                "\"owner\": \"John Doe\"" +
                "}")
            .when()
            .put("/api/application")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(response.getId()))
            .body("name", is("Updated Application"))
            .body("owner", is("John Doe"))
            .body("description", is("An updated application."));
    }

    /**
     * Test case to verify the deletion of an application.
     * Adds an application and then sends a DELETE request to remove it.
     * Expects a status code of 204.
     */
    @Test
    public void deleteApplication() {
        Response responseContent = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application delete",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application response = responseContent.body().as(Application.class);

        given(requestSpecification)
            .when()
            .delete("/api/application/{id}", response.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .statusCode(404);
    }

    @Test
    public void addApplication2() {
        given(requestSpecification)
            .body("""
                {
                    "name": "Test Application 2 add",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application")
            .then()
            .statusCode(is(201))
            .body("id", notNullValue())
            .body("name", is("Test Application 2 add"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    @Test
    public void findApplication2() {
        Response responseContent = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application find 2",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application response = responseContent.body().as(Application.class);

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .body("id", is(response.getId()))
            .body("name", is("Test Application find 2"))
            .body("description", is("A test application."))
            .body("owner", is("Kate Williams"));
    }

    /**
     * Test case to update an application.
     * Adds an application and then sends a PUT request with an updated application body.
     * Expects a status code of 200.
     */
    @Test
    public void updateApplication2() {
        Response response = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application 2 update",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application appResponse = response.body().as(Application.class);

        given(requestSpecification)
            .body("{" +
                "\"id\": \" "+ appResponse.getId() + "\"," +
                "\"name\": \"Updated Application 2\"," +
                "\"description\" : \"An updated application 2\"," +
                "\"owner\": \"John Doe\"" +
                "}")
            .when()
            .put("/api/application")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(appResponse.getId()))
            .body("name", is("Updated Application 2"))
            .body("owner", is("John Doe"))
            .body("description", is("An updated application 2"));
    }

    /**
     * Test case to verify the deletion of an application.
     * Adds an application and then sends a DELETE request to remove it.
     * Expects a status code of 204.
     */
    @Test
    public void deleteApplication2() {
        Response responseContent = given(requestSpecification)
            .body("""
                {
                    "name": "Test Application 2 delete",
                    "description" : "A test application.",
                    "owner": "Kate Williams"
                }
                """)
            .when()
            .post("/api/application");
        Application response = responseContent.body().as(Application.class);

        given(requestSpecification)
            .when()
            .delete("/api/application/{id}", response.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        given(requestSpecification)
            .when()
            .get("/api/application/{id}", response.getId())
            .then()
            .statusCode(404);
    }
}
