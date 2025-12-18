package com.example.TakeTicket.userModel.service;

import com.example.TakeTicket.userModel.Entity.Admin;
import com.example.TakeTicket.userModel.dto.AuthRes;
import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.Entity.Role;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.repository.AdminRepository;
import com.example.TakeTicket.userModel.repository.AgentRepository;
import com.example.TakeTicket.userModel.repository.UserRepository;
import com.example.TakeTicket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AgentRepository   agentRepository;
    private final AdminRepository adminRepository;

    //Inscription agent
    public Agent registerAgent(Agent agent) {
        // Encode password
        agent.setPassword(passwordEncoder.encode(agent.getPassword()));
        agent.setRole(Role.AGENT);
        agentRepository.save(agent);
        return agentRepository.save(agent);
    }

    //  Inscription Client
    public User registerClient(User client) {

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Role.CLIENT);
        userRepository.save(client);
        return userRepository.save(client);
    }

    public AuthRes login(String email, String password) {

        // 1️⃣ Vérifier si c’est un ADMIN
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtUtil.generateToken(email);
            return new AuthRes(admin.getRole().name(), token, null);
        }

        // 2️⃣ Vérifier si c’est un AGENT
        Optional<Agent> agentOpt = agentRepository.findByEmail(email);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtUtil.generateToken(email);
            return new AuthRes(agent.getRole().name(), token, agent.getService_id());
        }

        // 3️⃣ Vérifier si c’est un CLIENT
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtUtil.generateToken(email);
            return new AuthRes(user.getRole().name(), token, null);
        }

        // Aucun utilisateur trouvé
        throw new RuntimeException("User Not Found");
    }



    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
