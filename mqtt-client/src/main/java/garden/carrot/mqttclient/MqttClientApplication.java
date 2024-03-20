package garden.carrot.mqttclient;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import garden.carrot.mqttclient.model.MothershipUser;
import garden.carrot.mqttclient.model.mtg.TurnManager;
import garden.carrot.mqttclient.service.MessageMapper;
import garden.carrot.mqttclient.service.MothershipUserService;
import garden.carrot.mqttclient.service.MqttMessageHandler;
import garden.carrot.mqttclient.service.TurnManagerService;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MqttClientApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MqttClientApplication.class, args);

        MqttClient mqttClient = context.getBean(MqttClient.class);
        MqttMessageHandler mqttMessageHandler = context.getBean(MqttMessageHandler.class);

        try {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection to MQTT broker lost!");
                    // Attempt to reconnect
                    try {
                        mqttClient.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String[] topicParts = topic.split("/");
                    if (topicParts[1].equals("game")) {
                        // Magic the Gathering
                        if (topicParts[2].equals("mtg")) {
                            try {
                                TurnManager tm = mqttMessageHandler.handleMtgRequestMessage(topicParts[4], message);
                                if (tm != null) {
                                    // Publish the JSON string to a topic
                                    mqttClient.publish("api/game/mtg/p/update",
                                            new MqttMessage(tm.toJSON().getBytes()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            mqttMessageHandler.handleMessage(topic, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used in this example
                }
            });

            mqttClient.subscribe("api/game/mtg/r/#");
            mqttClient.subscribe("api/mothership/r/#");
            mqttClient.subscribe("api/mothership/v/#");
            mqttClient.subscribe("api/configChange/#");
            mqttClient.subscribe("api/test/topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    // ApplicationContext context =
    // SpringApplication.run(MqttClientApplication.class, args);

    // // Get an instance of MothershipUserService from the application context
    // MothershipUserService userService =
    // context.getBean(MothershipUserService.class);
    // TurnManagerService mtgTurnService =
    // context.getBean(TurnManagerService.class);

    // // Connect to MQTT broker
    // String broker = "tcp://localhost:1883"; // MQTT broker URL
    // String clientId = "mothership";
    // try {
    // MqttClient mqttClient = new MqttClient(broker, clientId);
    // MqttConnectOptions connOpts = new MqttConnectOptions();
    // connOpts.setCleanSession(true);

    // TurnManager turnManager = new TurnManager(null, null);

    // // Set keep-alive interval (in seconds)
    // connOpts.setKeepAliveInterval(30); // 60 seconds

    // // Set up message callback
    // mqttClient.setCallback(new MqttCallback() {
    // @Override
    // public void connectionLost(Throwable cause) {
    // System.out.println("Connection to MQTT broker lost!");
    // }

    // @Override
    // public void messageArrived(String topic, MqttMessage message) throws
    // Exception {
    // String payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic,
    // message);
    // System.out.println(payloadResponse);
    // }

    // @Override
    // public void deliveryComplete(IMqttDeliveryToken token) {
    // // Not used in this example
    // }
    // });

    // mqttClient.connect(connOpts);
    // System.out.println("Connected to MQTT broker");

    // // Subscribe to player action topic
    // mqttClient.subscribe("mtg/playerAction/#", (topic, message) -> {
    // // Handle player action message
    // String payload = new String(message.getPayload());
    // System.out.println("Received player action message on topic: " + topic);
    // System.out.println("Payload: " + payload);
    // ObjectMapper objectMapper = new ObjectMapper();
    // JsonNode jsonNode = objectMapper.readTree(payload);

    // String[] parts = topic.split("/");

    // if (parts.length > 2) {
    // if (parts[2].equals("join")) {
    // UUID user = UUID.fromString(jsonNode.get("uid").asText());
    // turnManager.joinLobby(user);
    // mqttClient.publish("mtg/tableUpdate", new
    // MqttMessage(turnManager.toJSON().getBytes()));
    // }
    // }
    // });

    // /**
    // * general mothership with response
    // */
    // mqttClient.subscribeWithResponse("mothership/r/#", (topic, message) -> {
    // // Handle incoming message and send a response
    // String payload = new String(message.getPayload());
    // System.out.println("Received message on topic: " + topic);
    // System.out.println("Payload: " + payload);

    // String[] parts = topic.split("/");

    // if (parts.length > 2) {
    // if (parts[2].equals("getUsers")) {
    // try {
    // List<MothershipUser> userList = userService.getAllUsers();
    // // Iterate over the list and print each user's properties
    // for (MothershipUser user : userList) {
    // System.out.println("User ID: " + user.getUid());
    // System.out.println("Name: " + user.getName());
    // System.out.println("Color: " + user.getColor());
    // System.out.println("Icon: " + user.getIcon());
    // System.out.println(); // Add a newline for better readability
    // }

    // // Process the message and prepare a response
    // String responsePayload = "Response to: " + userList.get(0).getName();

    // // Publish the response to a specific topic
    // String responseTopic = "response/topic";
    // MqttMessage responseMessage = new
    // MqttMessage(userList.get(0).getName().getBytes());
    // mqttClient.publish(responseTopic, responseMessage);

    // // Log the response
    // System.out.println("Sent response: " + responsePayload);
    // } catch (IllegalArgumentException e) {
    // System.err
    // .println("Error parsing user ID from payload or deleting user: " +
    // e.getMessage());
    // }
    // } else if (parts[2].equals("getUserByName")) {
    // try {
    // // Parse the JSON string
    // ObjectMapper objectMapper = new ObjectMapper();
    // JsonNode jsonNode = objectMapper.readTree(payload);

    // // Get the value associated with the "name" key
    // String name = jsonNode.get("name").asText();

    // List<MothershipUser> user = userService.getUserByName(name);

    // // Convert the object to a JSON string
    // String jsonString = objectMapper.writeValueAsString(user);

    // // Publish the response to a specific topic
    // String responseTopic = "response/topic";
    // MqttMessage responseMessage = new MqttMessage(jsonString.getBytes());
    // mqttClient.publish(responseTopic, responseMessage);

    // // Log the response
    // System.out.println("Sent response: " + jsonString);
    // } catch (IllegalArgumentException e) {
    // System.err
    // .println("Error parsing user ID from payload or deleting user: " +
    // e.getMessage());
    // }
    // }
    // }

    // });

    // // Subscribe to config change topic
    // mqttClient.subscribe("mothership/v/#", (topic, message) -> {
    // // Handle config change message
    // String payload = new String(message.getPayload());
    // System.out.println("Received config change message on topic: " + topic);
    // System.out.println("Payload: " + payload);
    // String payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic,
    // message);
    // System.out.println(payloadResponse);

    // String[] parts = topic.split("/");

    // if (parts.length > 2) {
    // if (parts[2].equals("addUser")) {
    // try {
    // // Parse payload and extract user information
    // ObjectMapper objectMapper = new ObjectMapper();
    // MothershipUser newUser = objectMapper.readValue(payload,
    // MothershipUser.class);
    // newUser.setUid(UUID.randomUUID()); // Assign a new UUID as the uid

    // // Save the new user
    // userService.createUser(newUser);

    // System.out.println("User added: " + newUser.getName());
    // } catch (IOException e) {
    // System.err.println("Error parsing user information from payload: " +
    // e.getMessage());
    // }
    // } else if (parts[2].equals("deleteUser")) {
    // try {
    // // Parse the JSON string
    // ObjectMapper objectMapper = new ObjectMapper();
    // JsonNode jsonNode = objectMapper.readTree(payload);

    // // Get the value associated with the "name" key
    // String name = jsonNode.get("name").asText();

    // // Delete the user from the database
    // userService.deleteUser(name);

    // } catch (IOException | IllegalArgumentException e) {
    // System.err
    // .println("Error parsing user ID from payload or deleting user: " +
    // e.getMessage());
    // }
    // } else if (parts[2].equals("getUsers")) {
    // try {
    // List<MothershipUser> userList = userService.getAllUsers();
    // // Iterate over the list and print each user's properties
    // for (MothershipUser user : userList) {
    // System.out.println("User ID: " + user.getUid());
    // System.out.println("Name: " + user.getName());
    // System.out.println("Color: " + user.getColor());
    // System.out.println("Icon: " + user.getIcon());
    // System.out.println(); // Add a newline for better readability
    // }
    // } catch (IllegalArgumentException e) {
    // System.err
    // .println("Error parsing user ID from payload or deleting user: " +
    // e.getMessage());
    // }
    // }
    // }
    // });

    // // Subscribe to config change topic
    // mqttClient.subscribe("configChange/#", (topic, message) -> {
    // // Handle config change message
    // String payload = new String(message.getPayload());
    // System.out.println("Received config change message on topic: " + topic);
    // System.out.println("Payload: " + payload);
    // String payloadResponse = MqttMessageHandler.getMqttPayloadObject(topic,
    // message);
    // System.out.println(payloadResponse);

    // String[] parts = topic.split("/");

    // if (parts.length > 2) {
    // if (parts[2].equals("addUser")) {

    // }
    // }
    // });

    // // Subscribe to a topic
    // String topic = "test/topic";
    // mqttClient.subscribe(topic);
    // System.out.println("Subscribed to topic: " + topic);

    // // Keep the application running
    // while (true) {
    // // Simulate the application running indefinitely
    // Thread.sleep(1000);
    // }

    // } catch (MqttException | InterruptedException e) {
    // e.printStackTrace();
    // }

    // // Close the application context when done
    // SpringApplication.exit(context);
    // }
}
