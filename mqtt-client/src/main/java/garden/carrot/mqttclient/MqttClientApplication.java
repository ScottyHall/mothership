package garden.carrot.mqttclient;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
			mqttClient.connect(connOpts);

			System.out.println("Connected to MQTT broker");

			// Subscribe to a topic
			String topic = "test/topic";
			mqttClient.subscribe(topic);
			System.out.println("Subscribed to topic: " + topic);

			// Disconnect from the broker
			// mqttClient.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}
