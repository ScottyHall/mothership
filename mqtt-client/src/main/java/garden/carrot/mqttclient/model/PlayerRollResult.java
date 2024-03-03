package garden.carrot.mqttclient.model;

public class PlayerRollResult {
    private String playerName;
    private int rollResult;

    public PlayerRollResult(String playerName, int rollResult) {
        this.playerName = playerName;
        this.rollResult = rollResult;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getRollResult() {
        return rollResult;
    }

    public void setRollResult(int rollResult) {
        this.rollResult = rollResult;
    }
}