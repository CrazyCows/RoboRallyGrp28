package dk.dtu.compute.se.pisd.roborally.controller.item;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * The Priority Antenna is a game element in RoboRally,
 * that decides who gets to make the first move for each turn.
 */

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
        System.out.println("THE PRIORITY ANTENNA"); //I don't think this needs to do anything.
        //In theory this doesn't need to be an object or anything, but it also doesn't need to not be an object
        return false;
    }

}
