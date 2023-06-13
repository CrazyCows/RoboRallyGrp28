package dk.dtu.compute.se.pisd.roborally.controller;

public class GameThread extends Thread {

    private static GameThread instance;

    private GameThread(Runnable runnable) {
        super(runnable);
    }

    public static synchronized GameThread getInstance(Runnable runnable) {
        if (instance == null || !instance.isAlive()) {
            instance = new GameThread(runnable);
        }
        return instance;
    }
}