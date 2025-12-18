package com.example.TakeTicket.userModel.controllers;

import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.repository.AdminRepository;
import com.example.TakeTicket.userModel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {


    @Autowired
    private UserService userService;

    // Récupérer tous les agents
    @GetMapping("/agents")
    public ResponseEntity<List<Agent>> getAllAgents() {
        return ResponseEntity.ok(userService.getAllAgents());
    }



    // Assigner un service à un agent
    @PutMapping("/agents/{id}/assign-service/{serviceId}")
    public ResponseEntity<Agent> assignService(@PathVariable Long id, @PathVariable Long serviceId) {
        return ResponseEntity.ok(userService.assignServiceToAgent(id, serviceId));
    }

    @GetMapping("/Statistique")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(userService.getDashboardStats());
    }

}
