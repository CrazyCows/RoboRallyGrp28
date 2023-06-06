package dk.dtu.compute.se.pisd.roborally.controller.card;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public abstract class CardAction<T extends Card> {

    public abstract boolean doAction(GameController gameController, Player player, T card);

}
