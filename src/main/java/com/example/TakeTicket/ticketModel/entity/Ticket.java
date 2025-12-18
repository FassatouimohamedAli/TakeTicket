package com.example.TakeTicket.ticketModel.entity;

import com.example.TakeTicket.userModel.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "service_name_id" , nullable = false)
    private ServiceEtab service;


    private String  ticketNumber;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime createdAt;       // quand le client a pris le ticket
    private LocalDateTime startedAt;       // quand l’agent a commencé à le servir
    private LocalDateTime endedAt;         // quand l’agent a terminé

    private Long estimatedWaitMinutes;     // estimation avant le tour
    private Long processingTime;    //en fns de nbrpersonne dans le service et le temps estimer (calculer entre starttime et end time )



    //relation avec utilisateur (client)
    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = true)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TicketStatus.WAITING;
        }
    }

}
