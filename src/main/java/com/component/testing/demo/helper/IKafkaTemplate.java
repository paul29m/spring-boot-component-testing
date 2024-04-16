package com.component.testing.demo.helper;

import com.component.testing.demo.entity.Comment;

import java.util.concurrent.ExecutionException;

public interface IKafkaTemplate {
    void send(Comment comment) throws ExecutionException, InterruptedException;
}
