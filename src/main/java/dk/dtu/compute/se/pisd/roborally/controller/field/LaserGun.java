package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.item.Laserbeam;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class LaserGun extends FieldAction {
    private Heading heading;
    private String name;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {
        System.out.println("FIRING LASER");
        backgroundAnimationThread(space, space.getBackground());
        gameController.getCardController().drawSpamCardToDiscardPile(space.getPlayer());
        return true;
    }

    public void setup(Space space) {
        space.getWalls().add(heading.prev().prev());
        Space nextSpace = space.board.getNeighbour(space, heading, true);
        if (nextSpace != null) {
            if (!nextSpace.hasItemName("Laser Beam")) {
                while (nextSpace.getActions().stream().noneMatch(action -> action.getClass().equals(LaserGun.class))) {
                    nextSpace.addItem(new Item("Laser Beam", "laserBeam.png", heading, new Laserbeam()));
                    if (nextSpace.getWalls().contains(heading)) {
                        break;
                    }
                    nextSpace = nextSpace.board.getNeighbour(nextSpace, heading, true);
                    if (nextSpace == null || nextSpace.getWalls().contains(heading.prev().prev())) {
                        break;
                    } else if (nextSpace.hasItemName("Laser Beam")) {
                        break;
                    }
                }
            }
        }
    }
}
