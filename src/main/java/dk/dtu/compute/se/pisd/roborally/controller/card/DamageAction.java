package dk.dtu.compute.se.pisd.roborally.controller.card;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.field.Pit;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public class DamageAction extends CardAction<DamageCard> {
    Pit pit = new Pit();


    /**
     * We didn't have any documentation implying otherwise, so I am assuming that all damage cards do the same,
     * as the rules are a bit unclear on this subject matter (and the images and description don't agree)
     * @param gameController
     * @param player
     * @param card
     * @return
     */
    @Override
    public boolean doAction(GameController gameController, Player player, DamageCard card) {
        CardController cardController = CardController.getInstance(); //Has to be inside the method for threading reasons
        switch (card.getName()){ //we are going for composition over anything else
            case "Spam":
                System.out.println("TEMPORARY PLACEHOLDER FEATURE: TURNING PLAYER RIGHT AS SPAM");
                player.setHeading("EAST");
                break;
            case "Trojan":
            case "Trojan Horse": //I'm assuming all cards do the same but god knows
                cardController.drawSpamCardToDiscardPile(player);
                cardController.drawSpamCardToDiscardPile(player);
                System.out.println("TEMPORARY PLACEHOLDER FEATURE: TURNING PLAYER RIGHT AS SPAM");
                player.setHeading("EAST");
                break;
            case "Worm":
                pit.doAction(gameController,player);
                break;
            case "Virus":
                for (Player p : player.board.getAllPlayers()){
                    if (gameController.distanceToSpace(player.getSpace(),p.getSpace()) > 6){
                        cardController.drawVirusCardToDiscardPile(p);
                        cardController.drawSpamCardToDiscardPile(player);
                    }
                }
                System.out.println("TEMPORARY PLACEHOLDER FEATURE: TURNING PLAYER RIGHT AS SPAM");
                player.setHeading("EAST");
                break;
            default:
                System.out.print("Something went wrong. We might want to throw an exception: ");
                System.out.println(card.getName());
        }

        // Implement the action specific to UpgradeCard
        // Use the gameController and card parameters as needed
        return true; // Return a boolean result indicating the success/failure of the action
    }


}
