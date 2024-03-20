package garden.carrot.mqttclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageMapper {
  public static JsonNode parseMessage(String jsonMessage) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(jsonMessage);
      return jsonNode;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
