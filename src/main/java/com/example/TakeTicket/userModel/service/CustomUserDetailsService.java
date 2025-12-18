package com.example.TakeTicket.userModel.service;

import com.example.TakeTicket.userModel.Entity.Admin;
import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.repository.AdminRepository;
import com.example.TakeTicket.userModel.repository.AgentRepository;
import com.example.TakeTicket.userModel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche d'abord un agent
        Agent agent = agentRepository.findByEmail(email).orElse(null);
        if (agent != null) {
            return agent;
        }

        // Recherche un Admin
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return admin;
        }

        // Recherche un client
        User client = userRepository.findByEmail(email).orElse(null);
        if (client != null) {
            return client;
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
