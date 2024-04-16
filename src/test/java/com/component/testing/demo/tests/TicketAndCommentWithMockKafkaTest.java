package com.component.testing.demo.tests;


import com.component.testing.demo.config.BaseRestAssuredIntegrationTest;
import com.component.testing.demo.config.PgContainerConfig;
import com.component.testing.demo.entity.Comment;
import com.component.testing.demo.helper.IKafkaTemplate;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.lazy-initialization=true"
    },
    classes = {PgContainerConfig.class}
)
public class TicketAndCommentWithMockKafkaTest extends BaseRestAssuredIntegrationTest {

    @MockBean
    private IKafkaTemplate kafkaTemplateProducer;

    @BeforeEach
    public void setUp() {
        this.setUpAbstractIntegrationTest();
    }

    @Test
    public void addTicketComment() throws ExecutionException, InterruptedException {
        //add ticket
        String location = given(requestSpecification)
            .body("""
                {
                    "title": "Ticket to be commented",
                    "description" : "Ticket description"
                }
                """)
            .when()
            .post("/api/ticket")
            .then().extract().response().getHeader("Location");
        // http://localhost:8080/ticket/1
        int ticketId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        //add comment to ticket
        given(requestSpecification)
            .body("{" +
                "\"ticketId\": " + ticketId + "," +
                "\"commentText\" : \"Comment text\"," +
                "\"userId\" : 1" +
                "}")
            .when()
            .post("api/comments/add")
            .then()
            .statusCode(201);

        await().untilAsserted( () -> {Mockito.verify(kafkaTemplateProducer, Mockito.times(1)).send(Mockito.isA(Comment.class));} );
    }

    @Test
    public void getTicketComments() throws ExecutionException, InterruptedException {
        //add ticket
        String location = given(requestSpecification)
            .body("""
                {
                    "title": "Ticket to be commented",
                    "description" : "Ticket description"
                }
                """)
            .when()
            .post("/api/ticket")
            .then().extract().response().getHeader("Location");
        // http://localhost:8080/ticket/1
        int ticketId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        // add multiple comments to same ticket
        String comment = "comment text";
        List<String> comments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            comments.add(comment + i);
            given(requestSpecification)
                .body("{" +
                    "\"ticketId\": " + ticketId + "," +
                    "\"commentText\" : \"" + comment + i + "\"," +
                    "\"userId\" : 1" +
                    "}")
                .when()
                .post("api/comments/add");
        }

        //retrieve
        await()
            .untilAsserted(() -> {
                ValidatableResponse validatableResponse = given(requestSpecification)
                    .queryParam("ticketId", ticketId)
                    .when()
                    .get("api/comments")
                    .then();
                validatableResponse.body("ticketId", everyItem(is(ticketId)));
                for (int i = 0; i < 5; i++) {
                    validatableResponse.body("commentText", hasItem(comments.get(i)));
                }
            });

        Mockito.verify(kafkaTemplateProducer, Mockito.times(5)).send(Mockito.isA(Comment.class));
    }

    @Test
    public void addCommentWithoutTicket() throws ExecutionException, InterruptedException {
        given(requestSpecification)
            .body(""" 
                {
                    "ticketId": 6,
                    "commentText" : " Comment text ",
                    "userId" : 1"
                }
                """)
            .when()
            .post("api/comment/add")
            .then()
            .statusCode(404);

        Mockito.verify(kafkaTemplateProducer, Mockito.times(0)).send(Mockito.isA(Comment.class));
    }
}
