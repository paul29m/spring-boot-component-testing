package com.component.testing.demo.service;

import com.component.testing.demo.entity.Ticket;
import java.util.List;

public interface ITicketService {
    List<Ticket> getAllTickets();
    Ticket getTicketById(Integer ticketId);
    void addTicket(Ticket ticket);
    void updateTicket(Ticket ticket);
    void deleteTicket(Integer ticketId);
    void resolveTicket(Integer ticketId);
    boolean canTicketBeCommented(Integer ticketId);
}
