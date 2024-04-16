package com.component.testing.demo.dao;

import java.util.List;

import com.component.testing.demo.entity.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentsDAO implements ICommentsDAO {

    public CommentsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> findAllByTicketId(Integer ticketId) {
        return jdbcTemplate.query(
            "SELECT * FROM comments WHERE ticketId = ?",
            (row, i) -> new Comment(row.getInt("ticketId"), row.getString("commentText"), row.getInt("userId")),
            ticketId
        );
    }

    @Override
    public void add(Integer ticketId, String commentText, Integer userId) {
        jdbcTemplate.update("INSERT INTO comments (ticketId, commentText, userId) VALUES (?, ?, ?)", ticketId, commentText, userId);
    }

    public Boolean exists(Integer ticketId) {
        List<Boolean> results = jdbcTemplate.query(
            "SELECT 1 FROM comments WHERE ticketId = ?",
            (row, i) -> true,
            ticketId
        );
        return !results.isEmpty();
    }
}
