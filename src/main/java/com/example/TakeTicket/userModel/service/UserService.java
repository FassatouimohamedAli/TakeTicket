package com.example.TakeTicket.userModel.service;

import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.TicketStatus;
import com.example.TakeTicket.ticketModel.repo.ServiceEtabRepository;
import com.example.TakeTicket.ticketModel.repo.TicketRepository;
import com.example.TakeTicket.userModel.Entity.Agent;
import com.example.TakeTicket.userModel.Entity.Role;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.repository.AdminRepository;
import com.example.TakeTicket.userModel.repository.AgentRepository;
import com.example.TakeTicket.userModel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AgentRepository agentRepository;

@Autowired
private ServiceEtabRepository serviceEtabRepository;
@Autowired
private TicketRepository ticketRepository;

    // Récupérer tous les agents
    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }



    // Assigner un service à un agent
    public Agent assignServiceToAgent(Long agentId, Long serviceId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new NoSuchElementException("Agent non trouvé avec id : " + agentId));

        ServiceEtab service = serviceEtabRepository.findById(serviceId)
                .orElseThrow(() -> new NoSuchElementException("Service non trouvé avec id : " + serviceId));

        agent.setService_id(service.getId());
        return agentRepository.save(agent);
    }


    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 🥇 Meilleur service
        List<Object[]> bestServices =
                ticketRepository.countTicketsByServiceAndStatus(TicketStatus.COMPLETED);

        if (!bestServices.isEmpty()) {
            Object[] best = bestServices.get(0);
            stats.put("bestService", Map.of(
                    "name", best[0],
                    "completedTickets", best[1]
            ));
        }

        // 📊 Tickets par service
        List<Object[]> totalTickets = ticketRepository.countTotalTicketsByService();
        List<Map<String, Object>> servicesStats = new ArrayList<>();

        for (Object[] row : totalTickets) {
            servicesStats.add(Map.of(
                    "service", row[0],
                    "totalTickets", row[1]
            ));
        }

        stats.put("servicesStats", servicesStats);

        // 👨‍💼 Agent le plus actif
        List<Object[]> agents = ticketRepository.mostActiveAgents();
        if (!agents.isEmpty()) {
            Object[] a = agents.get(0);
            stats.put("topAgent", Map.of(
                    "name", a[0] + " " + a[1],
                    "completedTickets", a[2]
            ));
        }

        return stats;
    }

}
