package com.example.TakeTicket.ticketModel.repo;

import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceEtabRepository extends JpaRepository<ServiceEtab, Long> {

    List<ServiceEtab> getServiceEtabById(Long id);
}
