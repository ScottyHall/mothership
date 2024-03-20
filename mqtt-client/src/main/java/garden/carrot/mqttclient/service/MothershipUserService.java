package garden.carrot.mqttclient.service;

import garden.carrot.mqttclient.model.MothershipUser;
import garden.carrot.mqttclient.repository.mtg.MothershipUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MothershipUserService {

    private final MothershipUserRepository userRepository;

    @Autowired
    public MothershipUserService(MothershipUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<MothershipUser> getAllUsers() {
        return userRepository.findAll();
    }

    public MothershipUser getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<MothershipUser> getUserByName(String name) {
        return userRepository.findByName(name);
    }

    public MothershipUser createUser(MothershipUser user) {
        System.out.println("USER CREATED TRIGGERED");
        return userRepository.save(user);
    }

    public MothershipUser updateUser(UUID userId, MothershipUser updatedUser) {
        updatedUser.setUid(userId);
        return userRepository.save(updatedUser);
    }

    public void deleteUser(UUID uid) {
        userRepository.deleteById(uid);
    }

    public void deleteUser(String name) {
        userRepository.deleteByName(name);
    }
}
