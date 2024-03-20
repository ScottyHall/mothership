package garden.carrot.mqttclient.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.databind.JsonNode;

import garden.carrot.mqttclient.model.mtg.Player;
import garden.carrot.mqttclient.model.mtg.TurnManager;
import garden.carrot.mqttclient.service.MtgGameService;

@Component
public class MqttMessageHandler {

    private final MothershipUserService userService;
    private final TurnManagerService turnManagerService;

    @Autowired
    private MtgGameService mtgGameService;

    @Autowired
    public MqttMessageHandler(MothershipUserService userService, TurnManagerService turnManagerService) {
        this.userService = userService;
        this.turnManagerService = turnManagerService;
    }

    public void handleMessage(String topic, MqttMessage message) throws Exception {
        System.out.println(topic);
        System.out.println(message);
        // Handle incoming MQTT messages based on the topic
        // Implement your message handling logic here
    }

    public TurnManager handleMtgRequestMessage(String topic, MqttMessage message) throws Exception {
        System.out.println(message);
        if (topic.equals("join")) {
            int roll = mtgGameService.joinGame(UUID.fromString(message.toString()));
            System.out.println(roll);
            return mtgGameService.getGameStatus();
        } else if (topic.equals("start")) {
            mtgGameService.startGame();
        } else if (topic.equals("currentPlayer")) {
            Player currentPlayer = mtgGameService.getCurrentPlayer();
            System.out.println(currentPlayer.getPlayerName());
        } else if (topic.equals("modifyPlayerHealth")) {
            JsonNode parsedMsg = MessageMapper.parseMessage(message.toString());
            String uidString = parsedMsg.get("uid").asText();
            UUID uid = UUID.fromString(uidString);
            int healthModifier = parsedMsg.get("amount").asInt();
            System.out.println(message);
            try {
                mtgGameService.modifyPlayerHealth(uid, healthModifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mtgGameService.getGameStatus();
        } else if (topic.equals("modifyCommanderDmg")) {
            JsonNode parsedMsg = MessageMapper.parseMessage(message.toString());
            String uidPlayerHitString = parsedMsg.get("playerHit").asText();
            String uidDmgFromString = parsedMsg.get("dmgFrom").asText();
            UUID uidPlayerHit = UUID.fromString(uidPlayerHitString);
            UUID uidDmgFrom = UUID.fromString(uidDmgFromString);
            int dmg = parsedMsg.get("dmg").asInt();
            System.out.println(message);
            try {
                mtgGameService.modifyCommanderDmg(uidPlayerHit, uidDmgFrom, dmg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mtgGameService.getGameStatus();
        } else if (topic.equals("meNext")) {
            mtgGameService.meNext(UUID.fromString(message.toString()));
            return mtgGameService.getGameStatus();
        } else if (topic.equals("nextTurn")) {
            mtgGameService.nextTurn();
            return mtgGameService.getGameStatus();
        } else if (topic.equals("gameStatus")) {
            return mtgGameService.getGameStatus();
        } else if (topic.equals("pausePlayCurrentPlayer")) {
            mtgGameService.pausePlayCurrentPlayer();
            return mtgGameService.getGameStatus();
        }
        return null;
        /*
         * /api/game/mtg/r/...
         * /api/game/mtg/
         * /join
         * /start
         * /currentPlayer
         * /modifyPlayerHealth
         * /meNext
         * /nextTurn
         * /gameStatus
         */

    }
}
