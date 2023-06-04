package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.ArrayList;
import java.util.Collections;

public class CardController {
    private static CardController cardController;
    private CardLoader cardLoader;
    private ArrayList<ProgrammingCard> universalDeck;
    //TODO: This deck needs to be removed. Player (model) has cards, controller doesn't.
    //TODO: Also, each player needs to have their own cards


    public static CardController getInstance(){ //Singleton, make private?
        if (cardController == null){
            cardController = new CardController();
        }
        return cardController;
    }

    /**
     * Singleton constuctor.
     * Creates a card pile, and shuffles them
     * @return
     */
    public CardController() {
        this.cardLoader = CardLoader.getInstance();
        this.universalDeck = cardLoader.getProgrammingCards();
        shuffleDeck(universalDeck);
    }

    /**
     * Fills player hand with cards from pile
     * @param player player who draws cards
     */
    public void drawCards(Player player){
        for (int i = 0; i < player.getHandSize(); i++) {
            drawOneCard(player);
        }
    }

    /**
     * This function is WRONG.
     *
     * @param player player who draws a card
     */
    public void drawOneCard(Player player) {
        ProgrammingCard commandCard = universalDeck.get(0);
        if (player.getNextEmptyCardField() != - 1) {
            player.drawCard(player.getNextEmptyCardField(), commandCard);
            universalDeck.add(commandCard); //This shouldn't be done
            universalDeck.remove(0);
        }
        else {
            System.out.println("TRIED TO ADD CARD, BUT NO SPACE FOR IT ON HAND");
        }
    }

    /**
     * Shuffles a given deck (linkedList) of CommandCards
     */
    private ArrayList<ProgrammingCard> shuffleDeck(ArrayList<ProgrammingCard> deck){
        Collections.shuffle(deck);
        return deck;
    }

    public CardLoader getCardLoader() {
        return this.cardLoader;
    }


    /**
     * COPIES all cards from the universal deck to the player drawPile. Only time that universalDeck should be used afaik
     */
    public void copyOverUniversalDeck(Player player) {
        player.drawPile.addAll(universalDeck);
    }
}
