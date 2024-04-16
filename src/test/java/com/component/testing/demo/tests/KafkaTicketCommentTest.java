package com.component.testing.demo.tests;


import static com.component.testing.demo.util.KafkaRecordsReader.getOffsets;
import static com.component.testing.demo.util.KafkaRecordsReader.readRecords;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

import com.component.testing.demo.config.BaseRestAssuredIntegrationTest;
import com.component.testing.demo.config.KafkaContainerConfig;
import com.component.testing.demo.entity.Comment;
import com.component.testing.demo.config.PgContainerConfig;
import com.component.testing.demo.util.KafkaRecordsReader;
import io.restassured.response.ValidatableResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.KafkaContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {},
    classes = {KafkaContainerConfig.class, PgContainerConfig.class}
)
public class KafkaTicketCommentTest extends BaseRestAssuredIntegrationTest {

    @Autowired
    private KafkaContainer kafka;

    @BeforeEach
    public void setUp() {
        this.setUpAbstractIntegrationTest();
    }

    @Test
    public void addTicket() {
        given(requestSpecification)
            .body("""
                {
                    "title": "Ticket to be added",
                    "description" : "Ticket description"
                }
                """)
            .when()
            .post("/api/ticket")
            .then()
            .statusCode(201);
    }

    @Test
    public void getAllTickets() {
        given(requestSpecification)
            .body("""
                {
                    "title": "Ticket all tickets",
                    "description" : "Ticket description"
                }
                """)
            .when()
            .post("/api/ticket")
            .then()
            .statusCode(201);

        given(requestSpecification)
            .when()
            .get("/api/tickets")
            .then()
            .statusCode(200)
            .body("size()", not(0))
            .body("title", hasItem("Ticket all tickets"));
    }

    @Test
    public void resolveTicket() {
        // add ticket
        String location = given(requestSpecification)
            .body("""
                {
                    "title": "Ticket to be resolved",
                    "description" : "Ticket description"
                }
                """)
            .when()
            .post("/api/ticket")
            .then()
            .extract().response().getHeader("Location");
        int ticketId = Integer.parseInt(location.substring(location.lastIndexOf("/") + 1));

        given(requestSpecification)
            .when()
            .put("/api/ticket/resolve/{ticketId}", ticketId)
            .then()
            .statusCode(200);

        given(requestSpecification)
            .when()
            .get("/api/ticket/{ticketId}", ticketId)
            .then()
            .body("status", is("RESOLVED"));
    }

    @Test
    public void addTicketComment() {
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

        await()
            .untilAsserted(() -> {
                List<ConsumerRecord<Comment, Comment>> records = getLastRecordsFromTopic(1);
                Assertions.assertThat(records.size()).isNotZero();
                Comment kafkaComment = records.get(0).value();

                Assertions.assertThat(kafkaComment.getTicketId()).isEqualTo(ticketId);
                Assertions.assertThat(kafkaComment.getCommentText()).isEqualTo("Comment text");
            });
    }

    @Test
    public void getTicketComments() {
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
    }

    @Test
    public void addCommentWithoutTicket() {
        given(requestSpecification)
            .body(""" 
                {"
                    "ticketId": 6,
                    "commentText" : " Comment text ",
                    "userId" : 1"
                "}
                """)
            .when()
            .post("api/comment/add")
            .then()
            .statusCode(404);
    }

    /*
     * Reads the records from the topic "comments" and returns them as a list of ConsumerRecord
     * @return a list of ConsumerRecord
     */
    @NotNull
    private List<ConsumerRecord<Comment, Comment>> getLastRecordsFromTopic(int lastNoOfRecords) {
        KafkaRecordsReader.setBootstrapServers(kafka.getBootstrapServers());
        final Map<TopicPartition, KafkaRecordsReader.OffsetInfo> partitionOffsetInfos = getOffsets(List.of("comments"));
        List<ConsumerRecord<Comment, Comment>> records = readRecords(partitionOffsetInfos);
        int lastIndex = records.size() - lastNoOfRecords;
        if (lastIndex > -1) {
            return records.subList(lastIndex, records.size());
        }
        return records;
    }
}
