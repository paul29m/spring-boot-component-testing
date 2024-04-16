package com.component.testing.demo.helper;

import com.component.testing.demo.entity.Comment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaTemplateProducer implements IKafkaTemplate {

    private final KafkaTemplate<String, Comment> kafkaTemplate;

    public KafkaTemplateProducer(KafkaTemplate<String, Comment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send comment to comments topic
     * @param comment - The comment to be sent
     */
    @Override
    public void send(Comment comment) throws ExecutionException, InterruptedException {
        kafkaTemplate.send("comments", comment).get();    }
}
