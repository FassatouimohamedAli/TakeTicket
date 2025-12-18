package com.example.TakeTicket.userModel.controllers;

import com.example.TakeTicket.userModel.dto.AgentRegisterRequest;
import com.example.TakeTicket.userModel.dto.AuthRes;
import com.example.TakeTicket.userModel.dto.ClientRegisterRequest;
import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class userController {
    @Autowired
  private AuthService authService;



    // ✅ Inscription Agent ou Client (optionnel)
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        String token = authService.register(user);
//        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole()));
//
//
//    }

    @GetMapping("/client")
    public ResponseEntity<?> helloClient() {

        return ResponseEntity.ok("hello  Client ");
    }











    // ✅ Connexion (Agent obligatoire / Client optionnel)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        AuthRes authRes = authService.login(data.get("email"), data.get("password"));
        return ResponseEntity.ok(authRes);
    }

    @PostMapping("/register-agent")
    public ResponseEntity<?> registerAgent(@RequestBody AgentRegisterRequest dto) {
        Agent agent = Agent.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(dto.password())
                .matricule(dto.matricule())
                .build();


        return ResponseEntity.ok(authService.registerAgent(agent));
    }

    @PostMapping("/register-client")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegisterRequest dto) {
        User client = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(dto.password())
                .build();

        return ResponseEntity.ok(authService.registerClient(client));
    }



//    // ✅ Endpoint public : un client prend un ticket sans compte
//    @GetMapping("/public/take-ticket")
//    public ResponseEntity<?> takeTicket() {
//        // Ici tu mettras la logique pour créer un ticket
//        // ex: générer un numéro et le stocker dans la file
//        int ticketNumber = (int) (Math.random() * 1000);
//        return ResponseEntity.ok(Map.of("message", "Ticket pris avec succès", "ticketNumber", ticketNumber));
//    }



}
