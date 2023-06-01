package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Card;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public abstract class CardAction<T extends Card> {

    public abstract boolean doAction(GameController gameController, Player player, T card);

}
