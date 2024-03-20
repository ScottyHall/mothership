package garden.carrot.mqttclient.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import garden.carrot.mqttclient.model.mtg.TurnManager;
import garden.carrot.mqttclient.repository.mtg.TurnManagerRepository;

@Service
public class TurnManagerService {

    private final TurnManagerRepository turnManagerRepository;

    @Autowired
    public TurnManagerService(TurnManagerRepository turnManagerRepository) {
        this.turnManagerRepository = turnManagerRepository;
    }

    public TurnManager saveTurnManager(TurnManager turnManager) {
        return turnManagerRepository.save(turnManager);
    }

    public TurnManager getTurnManagerById(UUID id) {
        return turnManagerRepository.findById(id).orElse(null);
    }

    // Add other methods as needed
}
