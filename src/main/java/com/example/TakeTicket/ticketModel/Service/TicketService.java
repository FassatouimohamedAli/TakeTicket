package com.example.TakeTicket.ticketModel.Service;

import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.Ticket;
import com.example.TakeTicket.ticketModel.entity.TicketStatus;
import com.example.TakeTicket.ticketModel.repo.ServiceEtabRepository;
import com.example.TakeTicket.ticketModel.repo.TicketRepository;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
@Autowired
    private TicketRepository ticketRepository;
@Autowired
private ServiceEtabRepository serviceRepository;
@Autowired
private UserRepository userRepository;

    @Transactional
    public Ticket createTicket(Long serviceId, String userEmail) {

        ServiceEtab service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));

        User user = null;
        if (userEmail != null && !userEmail.isEmpty()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
        }

        // Récupérer dernier numéro de ticket pour ce service
        Integer lastNumber = ticketRepository.findLastTicketNumberByService(service);
        int nextNumber = (lastNumber == null) ? 1 : lastNumber + 1;

        // Générer ticketNumber avec préfixe
        String ticketNumberStr = service.getPrefix() + nextNumber;

        // ✅ Compter UNIQUEMENT les tickets en attente (WAITING)
        int waitingTicketsCount = ticketRepository.countByServiceAndStatus(service, TicketStatus.WAITING);

        // ✅ Estimation basée sur les tickets en attente (6 min par ticket)
        long estimatedWait = waitingTicketsCount * 6;

        // Créer le ticket
        Ticket ticket = Ticket.builder()
                .service(service)
                .ticketNumber(ticketNumberStr)
                .status(TicketStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .user(user)
                .estimatedWaitMinutes(estimatedWait)
                .build();

        // Incrémenter nbrPeople
        service.setNbrPeople((service.getNbrPeople() == null) ? 1 : service.getNbrPeople() + 1);
        serviceRepository.save(service);

        return ticketRepository.save(ticket);
    }

    // 📊 Liste de tous les tickets (admin ou agent)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<ServiceEtab> getAllServiceEtabs() {
    return serviceRepository.findAll();
    }

    // 👤 Liste des tickets d’un utilisateur
    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByUser(user);
    }

    // ⏭️ Marquer comme “en cours”
    public Ticket startTicket(Long ticketId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));
        t.setStatus(TicketStatus.IN_PROGRESS);
        t.setStartedAt(LocalDateTime.now());
        return ticketRepository.save(t);
    }

    // Terminer un ticket
    public Ticket endTicket(Long ticketId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        // Si déjà COMPLETED → ne rien faire
        if (t.getStatus() == TicketStatus.COMPLETED) {
            return t;
        }

        t.setStatus(TicketStatus.COMPLETED);
        t.setEndedAt(LocalDateTime.now());

        if (t.getStartedAt() != null && t.getEndedAt() != null) {
            long totalSeconds = Duration.between(t.getStartedAt(), t.getEndedAt()).getSeconds();
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;

            // Arrondi correct : si seconds >= 30 → +1 minute
            if (seconds >= 40) {
                minutes += 1;
            }

            t.setProcessingTime(minutes);
        }

        Ticket saved = ticketRepository.save(t);

        // 🔁 Recalculer nbrPeople (le ticket ne doit plus être compté dans WAITING)
        updateNbrPeopleForService(t.getService());

        return saved;
    }


    // Annuler un ticket
    public Ticket cancelTicket(Long ticketId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        // ❌ On ne peut annuler QUE les tickets en WAITING
        if (t.getStatus() != TicketStatus.WAITING) {
            throw new RuntimeException("On ne peut annuler qu'un ticket en attente (WAITING).");
        }

        t.setStatus(TicketStatus.CANCELLED);
        Ticket saved = ticketRepository.save(t);

        // 🔁 Recalcule nbrPeople pour ce service
        updateNbrPeopleForService(t.getService());

        return saved;
    }

    private void updateNbrPeopleForService(ServiceEtab service) {
        int waitingCount = ticketRepository.countByServiceAndStatus(service, TicketStatus.WAITING);
        service.setNbrPeople(waitingCount);
        serviceRepository.save(service);
    }

    public List<Ticket> getTicketsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByUser(user);
    }

    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Ticket non trouvé avec id : " + ticketId));
    }

    public List<Ticket> getAllTicketById(List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        if (tickets.isEmpty()) {
            throw new NoSuchElementException("Aucun ticket trouvé pour les IDs : " + ticketIds);
        }
        return tickets;
    }

    public int getTicketPosition(Ticket ticket) {
        // Récupérer tous les tickets du même service et qui sont en attente, triés par date de création
        List<Ticket> ticketsWaiting = ticketRepository.findByServiceAndStatusOrderByCreatedAt(
                ticket.getService(), TicketStatus.WAITING
        );

        // Parcourir la liste et compter la position
        int position = 1; // position commence à 1
        for (Ticket t : ticketsWaiting) {
            if (t.getId().equals(ticket.getId())) {
                return position;
            }
            position++;
        }

        return position; // au cas où le ticket n'est pas dans la liste, retourne fin de file
    }


    public List<Ticket> getTicketsForService(Long serviceId) {
        return ticketRepository.findByServiceIdOrderByCreatedAt(serviceId);
    }





}
