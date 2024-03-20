package garden.carrot.mqttclient.model.mtg;

public class PlayerActions {
  private TurnManager turnManager;

  public PlayerActions(TurnManager turnManager) {
    this.turnManager = turnManager;
  }

  public void performAction(String actionPath) {
    // You can define different actions based on the actionPath
    if (actionPath.equals("/nextTurn")) {
      turnManager.nextTurn();
    } else if (actionPath.startsWith("/setPlayerAttributes")) {
      // Example: /setPlayerAttributes?playerName=John&totalTime=60000&totalHealth=50
      String[] params = actionPath.split("\\?");
      if (params.length == 2) {
        String[] keyValuePairs = params[1].split("&");
        String playerName = null;
        long totalTime = 0;
        int totalHealth = 0;
        for (String pair : keyValuePairs) {
          String[] keyValue = pair.split("=");
          if (keyValue.length == 2) {
            String key = keyValue[0];
            String value = keyValue[1];
            if (key.equals("playerName")) {
              playerName = value;
            } else if (key.equals("totalTime")) {
              totalTime = Long.parseLong(value);
            } else if (key.equals("totalHealth")) {
              totalHealth = Integer.parseInt(value);
            }
          }
        }
        if (playerName != null && totalTime > 0 && totalHealth > 0) {
          // Create or update player with specified attributes
          Player player = turnManager.getCurrentPlayer(); // For demonstration, assuming current player
          if (player != null) {
            // player.setName(playerName);
            player.getPlayerTimer().setTimeLeft(totalTime);
            // player.setTotalHealth(totalHealth);
          }
        }
      }
    } else if (actionPath.startsWith("/createPlayer")) {
      // Example: /createPlayer?playerName=Jane&totalTime=60000&totalHealth=50
      String[] params = actionPath.split("\\?");
      if (params.length == 2) {
        String[] keyValuePairs = params[1].split("&");
        String playerName = "test";
        long totalTime = 1200000;
        int totalHealth = 40;
        for (String pair : keyValuePairs) {
          String[] keyValue = pair.split("=");
          if (keyValue.length == 2) {
            String key = keyValue[0];
            String value = keyValue[1];
            if (key.equals("playerName")) {
              playerName = value;
            } else if (key.equals("totalTime")) {
              totalTime = Long.parseLong(value);
            } else if (key.equals("totalHealth")) {
              totalHealth = Integer.parseInt(value);
            }
          }
        }
        if (playerName != null && totalTime > 0 && totalHealth > 0) {
          // Create new player with specified attributes
          // Player player = new Player(playerName, "", totalHealth, totalTime);
          // turnManager.addPlayer(player);
        }
      }
    }
  }
}
