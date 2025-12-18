package com.example.TakeTicket.ticketModel.controller;

import com.example.TakeTicket.ticketModel.Service.ServiceEtabService;
import com.example.TakeTicket.ticketModel.Service.TicketService;
import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.Ticket;
import com.example.TakeTicket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/agent")
@RequiredArgsConstructor
public class AgentController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private ServiceEtabService serviceEtabService;
    @Autowired
    private JwtUtil jwtUtil;

    // ▶️ Commencer un ticket
    @PutMapping("/start/{ticketId}")
    public Ticket startTicket(@PathVariable Long ticketId) {
        return ticketService.startTicket(ticketId);
    }

    // ✅ Terminer un ticket
    @PutMapping("/end/{ticketId}")
    public Ticket endTicket(@PathVariable Long ticketId) {
        return ticketService.endTicket(ticketId);
    }

// all the ticket  for a service for agent
    @GetMapping("/tickets/service/{serviceId}")
    public List<Ticket> getTicketsByService(@PathVariable Long serviceId) {
        return ticketService.getTicketsForService(serviceId);
    }

    // 🔁 Toggle service status
    @PutMapping("/service/toggle/{serviceId}")
    public ResponseEntity<ServiceEtab> toggleServiceStatus(
            @PathVariable Long serviceId,
            @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(jwt) ;// extraire email du token

        ServiceEtab updatedService = serviceEtabService.toggleStatus(serviceId, email);

        return ResponseEntity.ok(updatedService);
    }
}
