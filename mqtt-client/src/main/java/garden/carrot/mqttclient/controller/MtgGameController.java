package garden.carrot.mqttclient.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import garden.carrot.mqttclient.model.mtg.Player;
import garden.carrot.mqttclient.model.mtg.TurnManager;
import garden.carrot.mqttclient.service.MtgGameService;

@RestController
@RequestMapping("/api/game/mtg")
public class MtgGameController {

  @Autowired
  private MtgGameService mtgGameService;

  @PostMapping("/join")
  public int joinGame(@RequestBody UUID uid) {
    return mtgGameService.joinGame(uid);
  }

  @PostMapping("/start")
  public void startGame() {
    mtgGameService.startGame();
  }

  @GetMapping("/currentPlayer")
  public Player getCurrentPlayer() {
    return mtgGameService.getCurrentPlayer();
  }

  @PostMapping("pausePlayCurrentPlayer")
  public void pausePlayCurrentPlayer() {
    mtgGameService.pausePlayCurrentPlayer();
  }

  @PostMapping("/modifyPlayerHealth")
  public ResponseEntity<String> modifyPlayerHealth(@RequestParam UUID playerUid, @RequestParam int healthModifier) {
    try {
      mtgGameService.modifyPlayerHealth(playerUid, healthModifier);
      return ResponseEntity.ok("Player health modified successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to modify player health: " + e.getMessage());
    }
  }

  @PostMapping("/modifyCommanderDmg")
  public ResponseEntity<String> modifyCommanderDmg(@RequestParam UUID playerHit, @RequestParam UUID dmgFrom,
      @RequestParam int dmg) {
    try {
      mtgGameService.modifyCommanderDmg(playerHit, dmgFrom, dmg);
      return ResponseEntity.ok("Player health modified successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to modify commander dmg: " + e.getMessage());
    }

  }

  @PostMapping("/meNext")
  public void meNext(@RequestBody UUID playerUid) {
    mtgGameService.meNext(playerUid);
  }

  @PostMapping("/nextTurn")
  public void nextTurn() {
    mtgGameService.nextTurn();
  }

  @GetMapping("/gameStatus")
  public TurnManager getStatus() {
    return mtgGameService.getGameStatus();
  }
}
