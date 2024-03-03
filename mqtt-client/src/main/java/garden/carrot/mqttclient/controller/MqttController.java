package garden.carrot.mqttclient.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttController {

    @PostMapping("/configChange")
    public String handleConfigChange(@RequestBody Map<String, Object> payload) {
        // Handle the configuration change
        System.out.println("Received configuration change: " + payload);
        return "test";
    }

    @GetMapping("/staticJson")
    public String getStaticJson() {
        // Define your static JSON data
        String staticJson = "{\"message\": \"Hello, world!\"}";

        // Return the static JSON data
        return staticJson;
    }
    //

    // Other controller methods can be added here for handling different types of
    // requests
}
