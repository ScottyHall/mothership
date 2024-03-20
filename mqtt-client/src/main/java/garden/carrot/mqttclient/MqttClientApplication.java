package garden.carrot.mqttclient;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import garden.carrot.mqttclient.model.mtg.TurnManager;
import garden.carrot.mqttclient.service.MqttMessageHandler;

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
}
