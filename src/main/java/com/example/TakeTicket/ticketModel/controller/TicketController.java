package com.example.TakeTicket.ticketModel.controller;

import com.example.TakeTicket.ticketModel.Service.TicketService;
import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.Ticket;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/public")
@RequiredArgsConstructor
// pour ton front React/React Native
public class TicketController {
@Autowired
    private TicketService ticketService;

@Autowired
private JwtUtil jwtUtil;

    // ➕ Prendre un ticket
    @PostMapping("/create/{serviceId}")
    public Ticket createTicket(
            @PathVariable Long serviceId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userEmail = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                userEmail = jwtUtil.extractEmail(token);
            }
        }

        return ticketService.createTicket(serviceId, userEmail);
    }

    // 📋 Tous les tickets
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // 👤 Tickets d’un utilisateur
    @PostMapping("/user")
    public List<Ticket> getTicketsByUser(@RequestBody User user) {
        return ticketService.getTicketsByUser(user);
    }

    // 👤 Tickets de l'utilisateur connecté (via JWT)
    @GetMapping("/my-tickets")
    public List<Ticket> getMyTickets(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String userEmail = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                userEmail = jwtUtil.extractEmail(token);
            }
        }

        if (userEmail == null) {
            // pas connecté → renvoyer liste vide
            return List.of();
        }

        return ticketService.getTicketsByUserEmail(userEmail);
    }




     // Annuler un ticket
    @PutMapping("/cancel/{ticketId}")
    public ResponseEntity<Ticket> cancelTicket(
            @PathVariable Long ticketId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // ✅ Client connecté → annuler ticket côté backend
            Ticket t = ticketService.cancelTicket(ticketId);
            return ResponseEntity.ok(t);
        } else {
            // ❌ Non connecté → ne fait rien côté backend, le front gère AsyncStorage
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @GetMapping("/services")
    public List<ServiceEtab> getAllServiceEtabs() {
        return ticketService.getAllServiceEtabs();
    }
    @GetMapping("/tickets/{ticketId}")
    public Ticket getTicketById(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId);
    }

    @GetMapping("/tickets/multiple")
    public ResponseEntity<List<Ticket>> getTicketsByIds(@RequestBody  List<Long> ids) {
        List<Ticket> tickets = ticketService.getAllTicketById(ids);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/{ticketId}/position")
    public int getTicketPosition(@PathVariable Long ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        return ticketService.getTicketPosition(ticket);
    }



}
