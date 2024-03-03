package garden.carrot.mqttclient.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TurnManager {
    private List<Player> players;
    private int currentPlayerIndex;

    public TurnManager() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = -1;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<PlayerRollResult> randomizePlayerOrder() {
        List<PlayerRollResult> rollResults = new ArrayList<>();
        Random random = new Random();

        for (Player player : players) {
            int roll = random.nextInt(20) + 1; // Roll a d20 (1-20)
            PlayerRollResult result = new PlayerRollResult(player.getName(), roll);
            rollResults.add(result);
        }

        Collections.shuffle(rollResults); // Shuffle the roll results
        return rollResults;
    }

    public void eliminatePlayer(Player player) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player p = iterator.next();
            if (p.equals(player)) {
                iterator.remove();
                if (currentPlayerIndex >= players.size()) {
                    currentPlayerIndex = 0;
                }
                break;
            }
        }
    }

    public void startGame() {
        currentPlayerIndex = 0; // Start with the first player
        System.out.println("Starting game...");
        startTurn();
    }

    public void startTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.eliminated()) {
            System.out.println("Starting turn for Player: " + currentPlayer.getName());
            currentPlayer.getPlayerTimer().start();
        } else {
            System.out.println("Skipping turn for eliminated Player: " + currentPlayer.getName());
            nextTurn();
        }
    }

    public void nextTurn() {
        if (currentPlayerIndex == players.size() - 1) {
            currentPlayerIndex = 0; // Wrap around to the first player
        } else {
            currentPlayerIndex++; // Move to the next player
        }
        startTurn();
    }
}
