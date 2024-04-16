package com.component.testing.demo.dao;

import com.component.testing.demo.entity.Comment;

import java.util.List;

public interface ICommentsDAO {
    List<Comment> findAllByTicketId(Integer ticketId);

    void add(Integer ticketId, String commentText, Integer userId);
}
