package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;

public class Laser extends FieldAction {
    private Heading heading;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {

        ArrayList<Space> spaces = space.board.getLaserSpaces();
        //Space target = gameController.getLineOfSight(space,heading);

/*
        currentPlayer.addSpamCardToDiscardPile(); //Draws two spam damage cards
        currentPlayer.addSpamCardToDiscardPile();

        currentPlayer.setHeading(Heading.NORTH);

 */
        return false;
    }
}
