package garden.carrot.mqttclient.config;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import garden.carrot.mqttclient.handler.MqttMessageHandler;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String broker = "tcp://localhost:1883"; // MQTT broker URL
        UUID clientIdAppend = UUID.randomUUID();
        // String broker = "ws://localhost:9001";
        String clientId = "mothership-" + clientIdAppend;

        MqttClient mqttClient = new MqttClient(broker, clientId);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        int keepAliveInterval = 10; // Keep-alive interval in seconds

        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(keepAliveInterval);

        mqttClient.connect(connOpts);
        System.out.println("Connected to MQTT brokerzzzz");

        return mqttClient;
    }
}
