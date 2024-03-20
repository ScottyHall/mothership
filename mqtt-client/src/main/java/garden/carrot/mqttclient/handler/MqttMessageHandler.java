package garden.carrot.mqttclient.handler;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttMessageHandler {

    public static String getMqttPayloadObject(String topic, MqttMessage message) {
        // Your implementation to handle MQTT messages and return a payload response
        return "Payload response for topic: " + topic;
    }
}
