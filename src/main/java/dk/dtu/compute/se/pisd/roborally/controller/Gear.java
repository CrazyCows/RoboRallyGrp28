package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Gear extends FieldAction {

    private Heading heading;
    private String direction;

    @Override
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        System.out.println("GEAR");
        if (direction.equals("left")) {
            gameController.turnPlayer(space.getPlayer(), Command.LEFT);
        }
        else if (direction.equals("right")) {
            gameController.turnPlayer(space.getPlayer(), Command.RIGHT);
        }
        else {
            System.out.println("We done f*cked up");
        }
        System.out.println(space.getPlayer().getHeading());
        // TODO needs to be implemented
        return false;
    }

}
