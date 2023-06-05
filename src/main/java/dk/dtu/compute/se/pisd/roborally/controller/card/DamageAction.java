package dk.dtu.compute.se.pisd.roborally.controller.card;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.field.Pit;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public class DamageAction extends CardAction<DamageCard> {
    Pit pit = new Pit();



    @Override
    public boolean doAction(GameController gameController, Player player, DamageCard card) {
        switch (card.getName()){ //we are going for composition over anything else
            case "Spam":
                System.out.println("Spamming the spams");
                break;
            case "Trojan":
            case "Trojan Horse": //I'm assuming all cards do the same but fucking god knows

                break;
            case "Worm":
                pit.doAction(gameController,player);
                break;
            case "Virus":
                CardController cardController = CardController.getInstance();
                for (Player p : player.board.getAllPlayers()){
                    if (gameController.distanceToSpace(player.getSpace(),p.getSpace()) > 6){
                        p.discardPile.add(cardController.virusPile.pop());
                    }
                }
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
