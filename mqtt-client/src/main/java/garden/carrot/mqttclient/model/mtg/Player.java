package garden.carrot.mqttclient.model.mtg;

import garden.carrot.mqttclient.model.MothershipUser;
import garden.carrot.mqttclient.service.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Player {
  private MothershipUser user;

  private int roll;
  private Health health;
  private Timer timeLeft;
  private int takebacksLeft;
  private List<CommanderDmg> commanderDmg;

  public Player(MothershipUser user, int health, long totalTime, int takebacks) {
    this.user = user;
    this.roll = this.rollForInitiative();
    this.health = new Health(health);
    this.timeLeft = new Timer(totalTime);
    this.takebacksLeft = takebacks;
    this.commanderDmg = new ArrayList<>();
  }

  // Used for sorting initial player order
  public int getInitialRoll() {
    return this.roll;
  }

  public String getPlayerName() {
    return this.user.getName();
  }

  public UUID getPlayerUid() {
    return this.user.getUid();
  }

  public Health getHealth() {
    return this.health;
  }

  public List<CommanderDmg> getCommanderDmg() {
    return this.commanderDmg;
  }

  public int getPlayerHealth() {
    return this.health.getCurrentHealth();
  }

  private int rollForInitiative() {
    Random random = new Random();
    this.roll = random.nextInt(20) + 1; // Roll a d20 (1-20)
    return this.roll;
  }

  // Getters and setters for the attributes
  public Timer getPlayerTimer() {
    return this.timeLeft;
  }

  public void takebackVoteAccepted() {
    if (this.takebacksLeft > 0) {
      this.takebacksLeft -= 1;
    }
  }

  /**
   * Track commander dmg from each player
   * 
   * @param dmgFrom user that dealt commander damage
   * @param dmg     amount of damage
   */
  public void commanderDmgFrom(UUID dmgFrom, int dmg) {
    if (this.commanderDmg.isEmpty()) {
      this.commanderDmg.add(new CommanderDmg(dmgFrom, dmg));
    } else {
      for (int i = 0; i < this.commanderDmg.size(); i++) {
        if (this.commanderDmg.get(i).uid.equals(dmgFrom)) {
          this.commanderDmg.get(i).modifyCommanderDmg(dmg);
        } else {
          this.commanderDmg.add(new CommanderDmg(dmgFrom, dmg));
        }
      }
    }
  }

  /**
   * Check if the player is eliminated from the game entirely
   * 
   * @return player is eliminated if true
   */
  public boolean eliminated() {
    if (this.health.getCurrentHealth() <= 0 || this.overMaxCommanderDmg() || this.timeLeft.getTimeLeft() <= 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Check if any players have done enough commander damage to eliminate player
   * Part of eliminated calculation
   * 
   * @return true if eliminated
   */
  private boolean overMaxCommanderDmg() {
    if (this.commanderDmg.size() > 0) {
      for (int i = 0; i < this.commanderDmg.size(); i++) {
        if (this.commanderDmg.get(i).getCommanderDmg() >= 21) {
          System.out.println("CMD dmg: True");
          return true;
        }
      }
    }
    System.out.println("CMD dmg: False");
    return false;
  }

  public class CommanderDmg {
    private UUID uid;
    private Health health;

    public CommanderDmg(UUID damageFrom, int dmg) {
      this.uid = damageFrom;
      this.health = new Health(dmg);
    }

    public UUID getUuid() {
      return this.uid;
    }

    public int getCommanderDmg() {
      return this.health.getCurrentHealth();
    }

    public void modifyCommanderDmg(int modifier) {
      this.health.modifyHealth(modifier);
    }
  }

  public class Health {
    private ArrayList<Integer> health;

    public Health(int initialHealth) {
      this.health = new ArrayList<Integer>();
      this.health.add(initialHealth);
    }

    public Health() {
      this.health = new ArrayList<Integer>();
      this.health.add(40);
    }

    public int getCurrentHealth() {
      System.out.println("Current Health: " + this.health.get(this.health.size() - 1));
      return this.health.get(this.health.size() - 1);
    }

    public ArrayList<Integer> getHealthHist() {
      return this.health;
    }

    public void modifyHealth(int modifier) {
      if (modifier > -300 && modifier < 300) {
        this.health.add(this.getCurrentHealth() + modifier);
      }
    }

    public void undoLastHealthMod() {
      if (this.health.size() > 1) {
        this.health.remove(this.health.size() - 1);
      }
    }

  }
}
