package garden.carrot.mqttclient.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MqttMessageHandler {
    public static Map<String, Object> getMqttPayloadObject(String topic, MqttMessage message) {
        // Convert payload to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String payloadString = null;
        Map<String, Object> jsonPayload = null;
        try {
            // Convert byte[] to String
            payloadString = new String(message.getPayload(), StandardCharsets.UTF_8);
            jsonPayload = objectMapper.readValue(payloadString, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            // Handle JSON parsing exception
            e.printStackTrace();
            return null;
        }

        if (jsonPayload != null) {
            // Access the value associated with the "test" key
            // Get values from object as Object testValue = jsonPayload.get("test");
            return jsonPayload;
        } else {
            System.out.println("Error: Unable to parse JSON payload");
            return null;
        }
    }
}
