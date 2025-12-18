package com.example.TakeTicket.ticketModel.repo;

import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.Ticket;
import com.example.TakeTicket.ticketModel.entity.TicketStatus;
import com.example.TakeTicket.userModel.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Tickets d’un utilisateur
    List<Ticket> findByUser(User user);

    List<Ticket> findByService_Id(Long serviceId);
    // Tickets en attente pour un service

    // 🔹 Tickets en attente (ou autre statut) pour un service
    List<Ticket> findByServiceNameAndStatus(ServiceEtab service, TicketStatus status);

    // Récupérer le dernier ticket créé pour un service
    @Query("SELECT MAX(CAST(SUBSTRING(t.ticketNumber, LENGTH(t.service.prefix)+1) AS int)) " +
            "FROM Ticket t WHERE t.service = :service")
    Integer findLastTicketNumberByService(@Param("service") ServiceEtab service);

    // TicketRepository.java
    List<Ticket> findByServiceAndStatusOrderByCreatedAt(ServiceEtab service, TicketStatus status);
    int countByServiceAndStatus(ServiceEtab service, TicketStatus status);
    List<Ticket> findByServiceIdOrderByCreatedAt(Long serviceId);


    // OU avec @Query si vous préférez
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.service = :service AND t.status = :status")
    long countWaitingTickets(@Param("service") ServiceEtab service, @Param("status") TicketStatus status);

    // Nombre de tickets par service et statut
    @Query("""
SELECT t.service.name, COUNT(t)
FROM Ticket t
WHERE t.status = :status
GROUP BY t.service.name
ORDER BY COUNT(t) DESC
""")
    List<Object[]> countTicketsByServiceAndStatus(
            @Param("status") TicketStatus status
    );

    // Total tickets par service
    @Query("""
SELECT t.service.name, COUNT(t)
FROM Ticket t
GROUP BY t.service.name
""")
    List<Object[]> countTotalTicketsByService();


    @Query("""
SELECT a.firstName, a.lastName, COUNT(t)
FROM Ticket t
JOIN Agent a ON a.Service_id = t.service.id
WHERE t.status = 'COMPLETED'
GROUP BY a.id, a.firstName, a.lastName
ORDER BY COUNT(t) DESC
""")
    List<Object[]> mostActiveAgents();
}
