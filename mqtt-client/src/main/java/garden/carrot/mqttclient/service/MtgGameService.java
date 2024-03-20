package garden.carrot.mqttclient.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import garden.carrot.mqttclient.model.mtg.Player;
import garden.carrot.mqttclient.model.mtg.TurnManager;

@Service
public class MtgGameService {

    private final TurnManager turnManager;

    @Autowired
    public MtgGameService(TurnManager turnManager) {
        this.turnManager = turnManager;
    }

    public int joinGame(UUID uid) {
        return turnManager.joinLobby(uid);
    }

    public void startGame() {
        turnManager.startGame();
    }

    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    public void pausePlayCurrentPlayer() {
        turnManager.pausePlayCurrentPlayerTimer();
    }

    public void modifyPlayerHealth(UUID playerUid, int healthModifier) {
        turnManager.modifyHealth(playerUid, healthModifier);
    }

    public void modifyCommanderDmg(UUID playerHit, UUID dmgFrom, int dmg) {
        turnManager.modifyCommanderDmg(playerHit, dmgFrom, dmg);
    }

    public void meNext(UUID playerUid) {
        turnManager.meNext(playerUid);
    }

    public void nextTurn() {
        turnManager.nextTurn();
    }

    public TurnManager getGameStatus() {
        return turnManager;
    }
}
