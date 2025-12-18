package com.example.TakeTicket.ticketModel.Service;

import com.example.TakeTicket.ticketModel.entity.ServiceEtab;
import com.example.TakeTicket.ticketModel.entity.ServiceStatus;
import com.example.TakeTicket.ticketModel.repo.ServiceEtabRepository;
import com.example.TakeTicket.userModel.Entity.User;
import com.example.TakeTicket.userModel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceEtabService {
@Autowired
    private ServiceEtabRepository serviceRepository;
@Autowired
    private UserRepository userRepository;


    // Toggle status avec vérification agent
    public ServiceEtab toggleStatus(Long serviceId, String agentEmail) {
        // 1️⃣ Vérifier agent
        User agent = userRepository.findByEmail(agentEmail)
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        // 2️⃣ Vérifier service
        ServiceEtab service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service introuvable"));

        if (!agent.getService_id().equals(serviceId)) {
            throw new RuntimeException("Non autorisé à modifier ce service");
        }



        // 4️⃣ Toggle status
        if (service.getStatus() == ServiceStatus.OPEN) {
            service.setStatus(ServiceStatus.CLOSED);
        } else {
            service.setStatus(ServiceStatus.OPEN);
        }

        return serviceRepository.save(service);
    }

}
