package garden.carrot.mqttclient.repository.mtg;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import garden.carrot.mqttclient.model.mtg.TurnManager;

import java.util.UUID;

@Repository
public interface TurnManagerRepository extends MongoRepository<TurnManager, UUID> {
  // You can add custom methods here if needed
}
