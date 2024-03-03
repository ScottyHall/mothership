package garden.carrot.mqttclient;

import java.util.Map;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import garden.carrot.mqttclient.services.MqttMessageHandler;

@SpringBootApplication
public class MqttClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttClientApplication.class, args);

        // Connect to MQTT broker
        String broker = "tcp://localhost:1883"; // MQTT broker URL
        String clientId = "mqtt-client";
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Set keep-alive interval (in seconds)
            connOpts.setKeepAliveInterval(30); // 60 seconds

            // Set up message callback
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Map<String, Object> payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic, message);
                    System.out.println(payloadResponse.get("test"));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });

            mqttClient.connect(connOpts);
            System.out.println("Connected to MQTT broker");

            // Subscribe to player action topic
            mqttClient.subscribe("playerAction/#", (topic, message) -> {
                // Handle player action message
                String payload = new String(message.getPayload());
                System.out.println("Received player action message on topic: " + topic);
                System.out.println("Payload: " + payload);
                Map<String, Object> payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic, message);
                System.out.println(payloadResponse.get("test"));

                // Implement game logic based on player actions
                // For example, update player timers, switch turns, etc.
                // Your game logic implementation goes here
            });

            // Subscribe to config change topic
            mqttClient.subscribe("configChange/#", (topic, message) -> {
                // Handle config change message
                String payload = new String(message.getPayload());
                System.out.println("Received config change message on topic: " + topic);
                System.out.println("Payload: " + payload);
                Map<String, Object> payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic, message);
                System.out.println(payloadResponse.get("test"));

                // Implement logic to update game configuration
                // For example, update player count, total time, etc.
                // Your configuration update logic goes here
            });

            // Subscribe to a topic
            String topic = "test/topic";
            mqttClient.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

            // Keep the application running
            while (true) {
                // Simulate the application running indefinitely
                Thread.sleep(1000);
            }

        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
