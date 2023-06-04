package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class CardController {
    private static CardController cardController;
    private CardLoader cardLoader;
    private ArrayList<Card> universalDeck = new ArrayList<>();
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
        this.universalDeck.addAll(cardLoader.getProgrammingCards());
    }

    /**
     * Fills player hand with cards from pile
     * @param player player who draws cards
     */
    public void drawCards(Player player){
        int numberOfCards = getNumberOfCardsInHandPile(player);
        for (int i = 0; i < player.getHandSize() - numberOfCards; i++) {
            drawOneCard(player);
        }
    }

    int getNumberOfCardsInHandPile(Player player){
        int i = 0;
        for (Card card : player.getHandPile()){
            if (card != null){
                i++;
            }
        }
        return i;
    }

    /**
     * This function is WRONG.
     *
     * @param player player who draws a card
     */
    public void drawOneCard(Player player) {
        Card card = null;
        try{
            card = player.drawPile.get(0); //We add this try/catch for when the pile runs out of cards.
        } catch (IndexOutOfBoundsException e){
            //Ignore exception. It's expected. Expect the exception, be the exception
            System.out.println("Moving cards from discardPile to drawPile for " + player.getName());
            shuffleDeck(player.discardPile);
            player.drawPile.addAll(player.discardPile);
            card = player.drawPile.get(0);
        }

        player.drawPile.remove(card); //Removes it from the drawPile
        player.drawCard(card);
        //player.getHandPile().add(card);
    }

    /**
     * Shuffles a given deck (linkedList) of CommandCards
     */
    void shuffleDeck(ArrayList<Card> deck){
        Collections.shuffle(deck);
    }

    public CardLoader getCardLoader() {
        return this.cardLoader;
    }

    public void moveProgramIntoDiscardPile(Player player){
        for (CommandCardField commandCardField : player.getProgram()){
            player.discardPile.add(commandCardField.getCard());
        }
        clearProgram(player);
    }

    private void clearProgram(Player player) {
        for (CommandCardField CCF : player.getProgram()){
                CCF.setCard(null);
            }
        }


    /**
     * COPIES all cards from the universal deck to the player drawPile. Only time that universalDeck should be used afaik
     */
    public void copyOverUniversalDeck(Player player) {
        player.drawPile.addAll(universalDeck);
        shuffleDeck(player.drawPile);
    }
}
