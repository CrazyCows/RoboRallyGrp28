package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.Collections;
import java.util.LinkedList;

public class CardController {
    private static CardController cardController;
    private CardLoader cardLoader;
    private LinkedList<CommandCard> deck;

    public static CardController getInstance(){ //Singleton
        if (cardController == null){
            cardController = new CardController();
        }
        return cardController;
    }

    public CardController() {
        this.cardLoader = CardLoader.getInstance();
        this.deck = cardLoader.CreateCardPile();
        shuffleDeck(deck);
    }

    public void drawCards(Player player){
        for (int i = 0; i < player.getHandSize(); i++) {
            drawOneCard(player);
        }
    }

    public void drawOneCard(Player player) {
        CommandCard commandCard = deck.get(0);
        if (player.getNextEmptyCardField() != - 1) {
            player.drawCard(player.getNextEmptyCardField(), commandCard);
            deck.add(commandCard);
            deck.remove(0);
        }
        else {
            System.out.println("TRIED TO ADD CARD, BUT NO SPACE FOR IT ON HAND");
        }
    }

    private LinkedList<CommandCard> shuffleDeck(LinkedList<CommandCard> deck){
        Collections.shuffle(deck);
        return deck;
    }

}
