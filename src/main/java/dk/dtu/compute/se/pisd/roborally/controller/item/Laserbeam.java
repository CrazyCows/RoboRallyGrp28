package dk.dtu.compute.se.pisd.roborally.controller.item;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Laserbeam extends FieldAction {

    public static List<String> backgrounds = new ArrayList<>(Arrays.asList("laserBeam.png", "laserBeam1.png", "laserBeam2.png"));

    @Override
    public boolean doAction(GameController gameController, Space space) {
        CardController cardController = CardController.getInstance();
        System.out.println("FIRING LASER");
        //backgroundAnimationThread(space, backgrounds);
        cardController.drawSpamCardToDiscardPile(space.getPlayer());
        return true;
    }
}
