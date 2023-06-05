package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.item.Laserbeam;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LaserGun extends FieldAction {
    private Heading heading;
    private String name;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }


    @Override //Maybe this code should be moved to the gamecontroller? Doesnt really make sense that a lasergun controls this
    public boolean doAction(GameController gameController, Space space) {
        CardController cardController = CardController.getInstance();
        System.out.println("FIRING LASER");
        backgroundAnimationThread(space, space.getBackground());
        cardController.addSpamCardToDiscardPile(space.getPlayer());

        return true;
    }

    public void setup(Space space) {
        space.getWalls().add(heading.prev().prev());
        Space nextSpace = space.board.getNeighbour(space, heading,true);
        if (nextSpace != null) {
            if (!nextSpace.hasItemName("Laser Beam")) {
                while (nextSpace.getActions().stream().noneMatch(action -> action.getClass().equals(LaserGun.class))) {
                    nextSpace.addItem(new Item("Laser Beam", "laserBeam.png", heading, new Laserbeam()));
                    nextSpace = nextSpace.board.getNeighbour(nextSpace, heading,true);
                    if (nextSpace == null) {
                        break;
                    }
                    else if (!nextSpace.hasItemName("Laser Beam")) {
                        break;
                    }
                }
            }
        }
    }
}
