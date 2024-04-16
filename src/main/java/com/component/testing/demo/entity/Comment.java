package com.component.testing.demo.entity;

import java.io.Serializable;

public class Comment implements Serializable {

    Integer ticketId;

    Integer userId;
    String commentText;

    public Comment() {}

    public Comment(Integer ticketId, String commentText, int userId) {
        this.ticketId = ticketId;
        this.commentText = commentText;
        this.userId = userId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public int getUserId() {
        return userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Override
    public String toString() {
        return "Comment {" +
                "ticketId=" + ticketId +
                ", comment='" + commentText + '\'' +
                ", userId=" + userId +
                '}';
    }
}
