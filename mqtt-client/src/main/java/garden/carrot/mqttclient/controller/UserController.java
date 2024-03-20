package garden.carrot.mqttclient.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import garden.carrot.mqttclient.model.MothershipUser;
import garden.carrot.mqttclient.repository.mtg.MothershipUserRepository;

@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  private MothershipUserRepository userRepository;

  /**
   * Get list of all MothershipUsers
   * 
   * @return
   */
  @GetMapping("/getAllUsers")
  public List<MothershipUser> getAllMothershipUsers() {
    return userRepository.findAll();
  }

  /**
   * Get list of MothershipUsers by name
   * 
   * @param name
   * @return
   */
  @GetMapping("/getUsersByName")
  public List<MothershipUser> getUsersByName(@RequestParam String name) {
    return userRepository.findByName(name);
  }

  @GetMapping("/getUser/{userId}")
  public ResponseEntity<MothershipUser> getUserById(@PathVariable("userId") UUID userId) {
    @SuppressWarnings("null")
    Optional<MothershipUser> userOptional = userRepository.findById(userId);
    return userOptional.map(user -> ResponseEntity.ok().body(user))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Save a MothershipUser
   * 
   * @param user
   * @return
   */
  @PostMapping("/addUser")
  public MothershipUser addUser(@RequestBody MothershipUser user) {
    if (user == null) {
      throw new IllegalArgumentException("User object cannot be null");
    }
    UUID userId = UUID.randomUUID();
    user.setUid(userId);
    // Save the user to the database
    return userRepository.save(user);
  }
}
