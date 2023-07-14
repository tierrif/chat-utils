package io.github.hotlava03.chatutils.util;

public class TooltipAlert {
    private static TooltipAlert instance;

    private boolean running;
    private long goalTime;
    private int creationTicks;

    private TooltipAlert() {}

    public void tick() {
        if (this.running && System.currentTimeMillis() >= goalTime) {
            this.running = false;
        }
    }

    public void start(int creationTicks) {
        this.running = true;
        this.goalTime = System.currentTimeMillis() + 2500L;
        this.creationTicks = creationTicks;
    }

    public int getCreationTicks() {
        return this.creationTicks;
    }

    public boolean isRunning() {
        return this.running;
    }

    public static void init() {
        instance = new TooltipAlert();
        instance.goalTime = -1L;
        instance.creationTicks = -1;
    }

    public static TooltipAlert getInstance() {
        return instance;
    }
}
