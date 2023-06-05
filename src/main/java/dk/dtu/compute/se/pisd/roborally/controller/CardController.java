package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.card.DamageAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class CardController {
    private static CardController cardController;
    private CardLoader cardLoader;
    private ArrayList<Card> universalDeck = new ArrayList<>();

    //Since these cards are simpler, we can just use a stack. Makes operations slightly simpler
    public Stack<CommandCardField> virusPile = new Stack<>(); //Pile of cards to draw from
    public Stack<CommandCardField> trojanPile = new Stack<>(); //Cards that have been run
    public Stack<CommandCardField> wormPile = new Stack<>(); //Pile of cards to draw from
    public Stack<CommandCardField> spamPile = new Stack<>(); //Cards that have been run

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
    private CardController() {
        this.cardLoader = CardLoader.getInstance();
        this.universalDeck.addAll(cardLoader.getProgrammingCards());

        for (int i = 0; i < 50; i++){ //50 is chosen arbitrarily, based on the size of the piles in the rulebook and because it gives nice, round numbers
            CommandCardField spam = new CommandCardField(null);
            spam.setCard(new DamageCard("SPAM","SPAM","/src/main/resources/checkpointhansi0.png","DamageAction"));
            //TODO: This is shit
            spamPile.push(spam);
        }

        for (int i = 0; i < 8; i++){ //8 is chosen arbitrarily, based on the size of the piles in the rulebook and because it gives nice, round numbers (8*3+50=74)
            CommandCardField virus = new CommandCardField(null);
            CommandCardField trojan = new CommandCardField(null);
            CommandCardField worm = new CommandCardField(null);
            virusPile.push(virus);
            trojanPile.push(trojan);
            wormPile.push(worm);
        }

        System.out.println("Created piles");
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
        for (Card card : player.getCopyOfHandPile()){
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
        Card card;
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
            ProgrammingCard c = (ProgrammingCard) commandCardField.getCard();
            if (c != null) {
                player.discardPile.add(c);
            }
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

    public void clearhand(Player player) {
        for (CommandCardField commandCardField : player.getHandPile()){
            commandCardField.setCard(null);
        }
    }
}
