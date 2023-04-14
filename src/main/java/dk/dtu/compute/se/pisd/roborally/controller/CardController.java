package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class CardController {
    private static CardController cardController;
    public static CardController getInstance(){ //Singleton
        if (cardController == null){
            cardController = new CardController();
        }
        return cardController;
    }



    public void drawCards(Player player){
        ArrayList<Object> deck = player.getDeck; //TODO: ADD getDeck and deck
        player.
    }
    private LinkedList<Object> shuffleDeck(LinkedList<Object> deck){
        Collections.shuffle(deck);
        return deck;
    }

}
