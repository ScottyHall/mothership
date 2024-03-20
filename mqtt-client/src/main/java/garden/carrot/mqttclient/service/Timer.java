package garden.carrot.mqttclient.service;

public class Timer {
    private long timeLeft; // time in ms the player has left on their timer total
    private long currentTime; // system time in ms for start of timer
    private long pauseTime; // system time in ms when the player paused their timer
    private boolean paused;
    private long initialTime;

    public Timer(long totalTime) {
        this.timeLeft = totalTime > 0 ? totalTime : 1200000; // 20 mins
        this.initialTime = this.timeLeft;
        this.paused = true;
    }

    /**
     * Start the timer (acts like a stop watch)
     * 
     * Gets the current system time in ms for later calculating how long their turn
     * went
     */
    public void start() {
        this.currentTime = System.currentTimeMillis();
        this.paused = false;
    }

    public void pause() {
        if (!paused) {
            this.pauseTime = System.currentTimeMillis();
            this.paused = true;
        }
    }

    public void resume() {
        if (paused) {
            long elapsedTime = System.currentTimeMillis() - this.pauseTime;
            this.currentTime += elapsedTime;
            this.paused = false;
        }
    }

    /**
     * Stop the player's timer
     * 
     * @return Long time the player has left in ms
     */
    public long stop() {
        if (!paused) {
            this.timeLeft = this.timeLeft + this.currentTime - System.currentTimeMillis();
        } else if (this.currentTime != 0) {
            // player passes turn while paused, kind of a not cool move, but it will happen
            this.timeLeft = this.timeLeft + this.currentTime - this.pauseTime;
        }
        this.currentTime = 0;
        this.paused = false;
        return this.timeLeft;
    }

    public boolean isPaused() {
        return this.paused;
    }

    /**
     * Get the total time left on their timer in ms
     * 
     * Calculation takes place but is not saved, only returned
     * 
     * @return Long time the player has left in ms
     */
    public long getTimeLeft() {
        long timeRemaining = 0;
        if (paused && this.currentTime != 0) {
            timeRemaining = this.timeLeft + this.currentTime - this.pauseTime;
        } else if (!paused && this.currentTime != 0) {
            timeRemaining = this.timeLeft + this.currentTime - System.currentTimeMillis();
        } else {
            timeRemaining = this.timeLeft;
        }
        System.out.println(timeRemaining);
        System.out.println(convertMillisToMinutes(timeRemaining));
        return timeRemaining;
    }

    /**
     * Get the total time left on their timer in String format mm:ss
     * 
     * Calculation takes place but is not saved, only returned
     * 
     * @return String time the player has left in format mm:ss
     */
    public String getTimeLeftMin() {
        long timeRemaining = 0;
        if (paused && this.currentTime != 0) {
            timeRemaining = this.timeLeft + this.currentTime - this.pauseTime;
        } else if (!paused && this.currentTime != 0) {
            timeRemaining = this.timeLeft + this.currentTime - System.currentTimeMillis();
        } else {
            timeRemaining = this.timeLeft;
        }
        System.out.println(convertMillisToMinutes(timeRemaining));
        return convertMillisToMinutes(timeRemaining);
    }

    /**
     * Convert ms to String display format mm:ss
     * 
     * @param millis Long ms for time conversion
     * @return String in format mm:ss
     */
    private String convertMillisToMinutes(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setTimeLeft(long newTimeRemaining) {
        this.timeLeft = newTimeRemaining;
    }

    public void reset() {
        this.timeLeft = this.initialTime;
        paused = false;
    }
}
