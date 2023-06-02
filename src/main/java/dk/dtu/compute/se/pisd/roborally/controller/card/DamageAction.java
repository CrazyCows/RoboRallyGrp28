package dk.dtu.compute.se.pisd.roborally.controller.card;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public class DamageAction extends CardAction<DamageCard> {

    @Override
    public boolean doAction(GameController gameController, Player player, DamageCard card) {
        // Implement the action specific to UpgradeCard
        // Use the gameController and card parameters as needed
        return true; // Return a boolean result indicating the success/failure of the action
    }


}