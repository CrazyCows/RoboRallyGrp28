package dk.dtu.compute.se.pisd.roborally.controller.item;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Laserbeam extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        System.out.println("FIRING LASER");
        backgroundAnimationThread(space, space.getBackground());
        space.getPlayer().addSpamCardToDiscardPile();
        return true;
    }
}
