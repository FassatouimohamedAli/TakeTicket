package com.example.TakeTicket.ticketModel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "services")
public class ServiceEtab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // Ex: "Acte de naissance"
    private String description;  // Ex: "Délivrance d’un acte de naissance certifié"
    private Integer nbrPeople;// nb personnes
    private String prefix;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'OPEN'")
    private ServiceStatus status;


//    @PrePersist
//    public void prePersist() {
//        if (this.status == null)
//            this.status = ServiceStatus.OPEN;
//    }


}


