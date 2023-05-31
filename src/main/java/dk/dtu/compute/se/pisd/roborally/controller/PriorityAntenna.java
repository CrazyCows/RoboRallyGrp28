package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * The Priority Antenna is a game element in RoboRally,
 * that decides who gets to make the first move for each turn.
 */

// TODO: The Priority Antenna should probably be a Space and not an item.

public class PriorityAntenna extends FieldAction {

    private Heading heading;


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        System.out.println("THE PRIORITY ANTENNA");
        // TODO needs to be implemented
        return false;
    }

}
