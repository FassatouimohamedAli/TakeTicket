package com.example.TakeTicket.userModel.repository;

import com.example.TakeTicket.userModel.Entity.Admin;
import com.example.TakeTicket.userModel.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByRole(Role role);
    Optional<Admin> findByEmail(String email);
}
