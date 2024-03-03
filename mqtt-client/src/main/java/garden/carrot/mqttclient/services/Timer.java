package garden.carrot.mqttclient.services;

public class Timer {
    private long totalTime;
    private long currentTime;
    private long pauseTime;
    private boolean paused;

    public Timer(long totalTime) {
        this.totalTime = totalTime > 0 ? totalTime : 1200000; // 20 mins
        this.paused = false;
    }

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

    public long stop() {
        if (!paused) {
            this.totalTime = this.totalTime + this.currentTime - System.currentTimeMillis();
            this.currentTime = 0;
        }
        return this.totalTime;
    }

    public long getTimeLeft() {
        long timeRemaining = 0;
        if (!paused) {
            timeRemaining = this.totalTime + this.currentTime - System.currentTimeMillis();
        } else {
            timeRemaining = this.totalTime;
        }
        return timeRemaining;
    }

    public void setTimeLeft(long newTimeRemaining) {
        this.totalTime = newTimeRemaining;
    }

    public void reset() {
        totalTime = 1200000;
        paused = false;
    }
}
