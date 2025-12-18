package com.example.TakeTicket.userModel.repository;

import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByEmail(String email);
}
