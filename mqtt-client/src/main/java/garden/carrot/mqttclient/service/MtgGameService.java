package garden.carrot.mqttclient.service;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import garden.carrot.mqttclient.model.mtg.Player;
import garden.carrot.mqttclient.model.mtg.TurnManager;

@Service
public class MtgGameService {

  private final TurnManager turnManager;
  private final MqttClient mqttClient;

  @Autowired
  public MtgGameService(TurnManager turnManager, MqttClient mqttClient) {
    this.turnManager = turnManager;
    this.mqttClient = mqttClient;
  }

  /**
   * publishes update on websocket
   */
  private void publishWebsocketUpdate() {
    try {
      TurnManager tm = turnManager;
      if (tm != null) {
        // Publish the JSON string to a topic
        mqttClient.publish("api/game/mtg/p/update",
            new MqttMessage(tm.toJSON().getBytes()));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int joinGame(UUID uid) {
    int roll = turnManager.joinLobby(uid);
    this.publishWebsocketUpdate();
    return roll;
  }

  public void startGame() {
    turnManager.startGame();
    this.publishWebsocketUpdate();
  }

  public Player getCurrentPlayer() {
    return turnManager.getCurrentPlayer();
  }

  public void pausePlayCurrentPlayer() {
    turnManager.pausePlayCurrentPlayerTimer();
    this.publishWebsocketUpdate();
  }

  public void modifyPlayerHealth(UUID playerUid, int healthModifier) {
    turnManager.modifyHealth(playerUid, healthModifier);
    this.publishWebsocketUpdate();
  }

  public void modifyCommanderDmg(UUID playerHit, UUID dmgFrom, int dmg) {
    turnManager.modifyCommanderDmg(playerHit, dmgFrom, dmg);
    this.publishWebsocketUpdate();
  }

  public void meNext(UUID playerUid) {
    turnManager.meNext(playerUid);
    this.publishWebsocketUpdate();
  }

  public void nextTurn() {
    turnManager.nextTurn();
    this.publishWebsocketUpdate();
  }

  public TurnManager getGameStatus() {
    return turnManager;
  }

  public void clearGame() {
    turnManager.clearGame();
    this.publishWebsocketUpdate();
  }
}
