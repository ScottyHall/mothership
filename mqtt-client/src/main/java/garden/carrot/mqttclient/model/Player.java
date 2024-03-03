package garden.carrot.mqttclient.model;

import garden.carrot.mqttclient.services.Timer;

public class Player {
    private String name;
    private String color;
    private Timer timeLeft;
    private int totalHealth;
    private boolean eliminated;

    public Player(String name, String color, int health, long totalTime) {
        this.name = name;
        this.color = color;
        this.totalHealth = health;
        this.timeLeft = new Timer(totalTime);
        this.eliminated = false;
    }

    // Getters and setters for the attributes

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Timer getPlayerTimer() {
        return this.timeLeft;
    }

    public void setTotalHealth(int totalHealth) {
        this.totalHealth = totalHealth;
    }

    public void subtractHealth(int amountToSubtract) {
        this.totalHealth -= amountToSubtract;
        this.checkIfEliminated();
    }

    public void addHealth(int amountToAdd) {
        this.totalHealth += amountToAdd;
        this.checkIfEliminated();
    }

    public void checkIfEliminated() {
        if (this.totalHealth < 0) {
            this.eliminate();
        }
    }

    public void eliminate() {
        this.eliminated = true;
    }

    public boolean eliminated() {
        return this.eliminated;
    }
}
