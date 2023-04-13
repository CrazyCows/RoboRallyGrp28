package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

public class AppController {

    RoboRally roboRally;

    public AppController(RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    // TODO most methods missing here!

    public void exit() {
        // TODO needs to be implemented
    }


    public void newGame() {
    }

    public void stopGame() {
    }

    public void saveGame() {
    }

    public void loadGame() {
    }

    public boolean isGameRunning() {
        // TEMP - GAME IS RUNNING - ALWAYS <3
        return true;
    }
}
