package garden.carrot.mqttclient.repository.mtg;

import garden.carrot.mqttclient.model.MothershipUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MothershipUserRepository extends MongoRepository<MothershipUser, UUID> {

    void deleteByName(String name);

    List<MothershipUser> findByName(String name);
}
