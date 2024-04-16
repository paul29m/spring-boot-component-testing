package com.component.testing.demo.api;

import com.component.testing.demo.entity.Ticket;
import com.component.testing.demo.service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    private ITicketService ticketService;

    @GetMapping("/ticket/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("id") Integer id) {
        Ticket ticket = ticketService.getTicketById(id);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> list = ticketService.getAllTickets();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/ticket")
    public ResponseEntity<Void> addTicket(@RequestBody Ticket ticket, UriComponentsBuilder builder) {
        ticketService.addTicket(ticket);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/ticket/{id}").buildAndExpand(ticket.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/ticket")
    public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket) {
        ticketService.updateTicket(ticket);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @DeleteMapping("/ticket/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") Integer id) {
        ticketService.deleteTicket(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/ticket/resolve/{id}")
    public ResponseEntity<Ticket> resolveTicket(@PathVariable("id") Integer id) {
        ticketService.resolveTicket(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
