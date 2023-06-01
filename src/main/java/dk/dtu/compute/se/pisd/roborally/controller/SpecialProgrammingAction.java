package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.SpecialProgrammingCard;

public class SpecialProgrammingAction extends CardAction<SpecialProgrammingCard> {

    @Override
    public boolean doAction(GameController gameController, Player player, SpecialProgrammingCard card) {
        // Implement the action specific to UpgradeCard
        // Use the gameController and card parameters as needed
        return true; // Return a boolean result indicating the success/failure of the action
    }

}
