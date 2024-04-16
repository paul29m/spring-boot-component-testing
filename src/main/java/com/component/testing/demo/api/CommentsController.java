package com.component.testing.demo.api;

import com.component.testing.demo.helper.IKafkaTemplate;
import com.component.testing.demo.service.TicketService;
import com.component.testing.demo.entity.Comment;
import com.component.testing.demo.dao.CommentsDAO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing comments.
 */
@RestController
@RequestMapping("api/comments")
public class CommentsController {

    private final IKafkaTemplate kafkaProducer;

    private final CommentsDAO commentsRepository;

    private final TicketService ticketService;

    public CommentsController(
        IKafkaTemplate kafkaTemplate,
        CommentsDAO commentsRepository,
        TicketService ticketService
    ) {
        this.kafkaProducer = kafkaTemplate;
        this.commentsRepository = commentsRepository;
        this.ticketService = ticketService;
    }

    /**
     * Records a comment and sending it to the Kafka topic "comments".
     *
     * @param comment the Comment object to be recorded
     * @return a ResponseEntity indicating that the comment has been accepted
     * @throws Exception if an error occurs while sending the comment to the topic
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addComment(@RequestBody Comment comment) throws Exception {
        if (!ticketService.canTicketBeCommented(comment.getTicketId())) {
            return ResponseEntity.notFound().build();
        }

        commentsRepository.add(comment.getTicketId(), comment.getCommentText(), comment.getUserId());
        kafkaProducer.send(comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public List<Comment> getComments(@RequestParam Integer ticketId) {
        return commentsRepository.findAllByTicketId(ticketId);
    }
}
