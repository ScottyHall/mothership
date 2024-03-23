package garden.carrot.mqttclient.model.mtg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import garden.carrot.mqttclient.model.MothershipUser;
import garden.carrot.mqttclient.repository.mtg.MothershipUserRepository;
import garden.carrot.mqttclient.repository.mtg.TurnManagerRepository;

@Component
public class TurnManager {
  private UUID gameId;
  private List<Player> players;
  private List<Player> lobby;
  private List<List<Player>> turnHist;
  private int currentPlayerIndex;
  private int defaultPlayerHealth;
  private long defaultPlayerTime;
  private int defaultPlayerTakebacks;
  private boolean gameOver;
  private Player winner;

  private final MothershipUserRepository userRepository;

  public TurnManager(MothershipUserRepository userRepository, TurnManagerRepository turnManagerRepository) {
    this.gameId = UUID.randomUUID();
    this.userRepository = userRepository;
    this.turnHist = new ArrayList<>();
    this.players = new ArrayList<>();
    this.lobby = new ArrayList<>();
    this.currentPlayerIndex = -1;
    this.defaultPlayerHealth = 40;
    this.defaultPlayerTime = 1200000;
    this.defaultPlayerTakebacks = 1;
    this.gameOver = false;
    this.winner = null;
  }

  public List<Player> getPlayers() {
    return this.players;
  }

  public List<Player> getLobby() {
    return this.lobby;
  }

  public boolean isGameOver() {
    return this.gameOver;
  }

  public Player getWinner() {
    return this.winner;
  }

  /**
   * Get the player who is currently taking their turn in the game
   * 
   * @return Player currently taking their turn
   */
  public Player getCurrentPlayer() {
    if (this.currentPlayerIndex >= 0 && this.currentPlayerIndex < this.players.size()) {
      return this.players.get(currentPlayerIndex);
    } else {
      return null;
    }
  }

  /**
   * Join the lobby for a game
   * 
   * There must be an existing mothership user account to join, pass in the UUID
   * from that user
   * 
   * @param uid MothershipUser UUID
   * @return int initial random roll generated for turn order
   */
  public int joinLobby(UUID uid) {
    if (this.gameOver) {
      this.clearGame();
    }
    Optional<MothershipUser> userOptional = userRepository.findById(uid);
    if (userOptional.isPresent()) {
      MothershipUser user = userOptional.get();
      System.out.println("Joined lobby: " + user.getName());
      Player me = new Player(user, this.defaultPlayerHealth, this.defaultPlayerTime, this.defaultPlayerTakebacks);
      this.lobby.add(me);
      return me.getInitialRoll();
    } else {
      System.out.println("User with UID " + uid + " not found in the database.");
      return -1;
    }
  }

  /**
   * Join the game (players list) from the lobby (lobby list)
   * 
   * The players list is a list of the active players in the game
   * 
   * The game starts and is considered active when all players are moved from the
   * lobby into the players list
   * 
   * @param uid UUID of the player to move from the lobby to the players list
   */
  public void joinGameFromLobby(UUID uid) {
    Optional<Player> playerOptional = lobby.stream().filter(player -> player.getPlayerUid().equals(uid))
        .findFirst();
    if (playerOptional.isPresent()) {
      Player player = playerOptional.get();
      this.players.add(player);
      this.lobby.remove(player);
    }
  }

  /**
   * Start the game (kind of)
   * 
   * The next step is for all players to call 'meNext' to determine turn order
   * 
   * The turn order is determined by seating arrangement, so the device has zero
   * awareness past the 1st player (highest initial roll)
   */
  public void startGame() {
    System.out.println("Starting game...");
    // only kickoff if there are no active player and lobby is not empty
    if (!lobby.isEmpty() && players.isEmpty()) {
      this.arrangePlayersByRoll();
    }
  }

  /**
   * Clear all values from instance
   */
  public void clearGame() {
    // Reset all fields to their initial values
    this.gameId = UUID.randomUUID();
    this.players.clear();
    this.lobby.clear();
    this.turnHist.clear();
    this.currentPlayerIndex = -1;
    this.gameOver = false;
    this.winner = null;
  }

  /**
   * Check if the game is over
   * 
   * Players will be removed from the players list when they are eliminated, check
   * if there is only a single player in the list
   */
  private void endGame() {
    List<Player> alivePlayers = new ArrayList<>();
    for (Player player : players) {
      if (player.getPlayerHealth() > 0) {
        alivePlayers.add(player);
      }
    }

    if (alivePlayers.size() == 1) {
      this.gameOver = true;
      this.winner = alivePlayers.get(0);
      System.out.println("Winner of the Game: ");
      System.out.println(this.winner.getPlayerName());
    }
  }

  /**
   * Check for the highest roll from all players sorting the lobby
   * 
   * Add the highest roll player to the players list (order placed in the players
   * list drives turn order)
   */
  public void arrangePlayersByRoll() {
    if (!lobby.isEmpty()) {
      Collections.sort(lobby, Comparator.comparingInt(Player::getInitialRoll).reversed());
      Player highestRollPlayer = lobby.remove(0);
      players.add(highestRollPlayer);
    }
    this.currentPlayerIndex = 0;
    startTurn();
  }

  /**
   * Start the currentPlayer's turn
   * 
   * Check if they're eliminated and exist
   * 
   * Player actions that take place at the starting portion of their turn such as
   * starting their timer
   */
  public void startTurn() {
    Player currentPlayer = this.getCurrentPlayer();
    if (currentPlayer != null && !currentPlayer.eliminated()) {
      System.out.println("Starting turn for Player: " + currentPlayer.getPlayerName());
      this.startCurrentPlayersTime();
    } else if (currentPlayer != null) {
      System.out.println("Skipping turn for eliminated Player: " + currentPlayer.getPlayerName());
      nextTurn();
    }
  }

  private void startCurrentPlayersTime() {
    Player currentPlayer = this.getCurrentPlayer();
    if (currentPlayer != null && this.lobby.size() == 0) {
      currentPlayer.getPlayerTimer().start();
    }
  }

  public void pausePlayCurrentPlayerTimer() {
    Player currentPlayer = this.getCurrentPlayer();
    if (currentPlayer != null && this.lobby.size() == 0) {
      if (currentPlayer.getPlayerTimer().isPaused()) {
        currentPlayer.getPlayerTimer().resume();
      } else {
        currentPlayer.getPlayerTimer().pause();
      }
    }
  }

  public void nextTurn() {
    Player currentPlayer = this.getCurrentPlayer();
    if (currentPlayer != null) {
      currentPlayer.getPlayerTimer().stop();
    }

    List<Player> eliminatedPlayers = new ArrayList<>();
    for (Player player : players) {
      if (player.eliminated()) {
        eliminatedPlayers.add(player);
      }
    }
    players.removeAll(eliminatedPlayers);

    if (players.size() == 1) {
      this.endGame();
    } else {
      if (this.currentPlayerIndex == players.size() - 1) {
        this.currentPlayerIndex = 0;
      } else {
        this.currentPlayerIndex++;
      }
      startTurn();
    }
    this.turnHist.add(players);
  }

  /**
   * Place the given player from their UUID to move them from the lobby to the
   * players list (making them next in the turn order)
   * 
   * Intended to be called after starting the game to let players determine turn
   * order,
   * they will take turns calling this to claim the next spot
   * 
   * Start game, then players take turns calling this traditionally going
   * clockwise around the table
   * 
   * @param playerUid UUID of player that is next currently located in the lobby
   */
  public void meNext(UUID playerUid) {
    Optional<Player> playerOptional = lobby.stream().filter(player -> player.getPlayerUid().equals(playerUid))
        .findFirst();
    if (playerOptional.isPresent()) {
      Player player = playerOptional.get();
      if (!players.contains(player)) { // Check if the player is not already in the players list
        lobby.remove(player);
        players.add(player);
        this.startCurrentPlayersTime();
      }
    }
  }

  /**
   * Modify player health
   * Call for adding or subtracting health, pass negative number for subtraction,
   * positive for addition
   * 
   * @param playerUid UUID of player to modify health
   * @param amount    negative for subtracting health, positive for adding health
   */
  public void modifyHealth(UUID playerUid, int amount) {
    Player player = players.stream()
        .filter(p -> p.getPlayerUid().equals(playerUid))
        .findFirst()
        .orElse(null);

    if (player != null) {
      player.getHealth().modifyHealth(amount);
    }
  }

  /**
   * Modify commander damage for a single combat damage dealt from a commander to
   * a player
   * Call for each instance of a commander damaging another player
   * 
   * @param playerUid player that received the damage
   * @param dmgFrom   player that dealt the damage
   * @param dmgAmt    amount of damage dealt from that single commander
   */
  public void modifyCommanderDmg(UUID playerUid, UUID dmgFrom, int dmgAmt) {
    Player player = players.stream()
        .filter(p -> p.getPlayerUid().equals(playerUid))
        .findFirst()
        .orElse(null);
    Player dmgFromPlayer = players.stream()
        .filter(p -> p.getPlayerUid().equals(dmgFrom))
        .findFirst()
        .orElse(null);
    if (player != null && dmgFromPlayer != null) {
      player.commanderDmgFrom(dmgFrom, dmgAmt);
    }
  }

  public String toJSON() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      return mapper.writeValueAsString(this);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
