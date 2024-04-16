package com.component.testing.demo.dao;

import com.component.testing.demo.entity.Ticket;

import java.util.List;

public interface ITicketDAO {
    List<Ticket> getAllTickets();
    void addTicket(Ticket ticket);
    Ticket getTicketById(int ticketId);
    void updateTicket(Ticket ticket);
    void deleteTicket(int ticketId);
    void resolve(int ticketId);
}
