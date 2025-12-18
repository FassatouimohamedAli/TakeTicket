package com.example.TakeTicket.userModel.repository;

import com.example.TakeTicket.userModel.Entity.Admin;
import com.example.TakeTicket.userModel.Entity.Role;
import com.example.TakeTicket.userModel.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(Role role);
}
