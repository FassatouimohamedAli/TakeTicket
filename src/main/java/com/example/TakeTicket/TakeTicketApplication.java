package com.example.TakeTicket;

import com.example.TakeTicket.userModel.Entity.Admin;
import com.example.TakeTicket.userModel.Entity.Role;
import com.example.TakeTicket.userModel.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class TakeTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeTicketApplication.class, args);
	}

	@Bean
	CommandLineRunner run(AdminRepository adminRepo,
						  PasswordEncoder passwordEncoder) {
		return args -> {
			if (adminRepo.findByRole(Role.ADMIN).isPresent())
				return;
			Admin admin = new Admin("admin@example.com", passwordEncoder.encode("password"));
			adminRepo.save(admin);
		};
	}

}
