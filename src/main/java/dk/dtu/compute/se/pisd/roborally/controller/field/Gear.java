package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Gear extends FieldAction {

    private Heading heading;
    private String direction;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        System.out.println("Gear, turning " + direction);
        if (direction.equals("left")) {
            gameController.turnPlayer(space.getPlayer(), Command.LEFT);
        }
        else if (direction.equals("right")) {
            gameController.turnPlayer(space.getPlayer(), Command.RIGHT);
        }
        else {
            throw new IllegalArgumentException();
        }
        return false;
    }

}
