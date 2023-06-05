package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.card.DamageAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class CardController {
    private static CardController cardController;
    private CardLoader cardLoader;
    private ArrayList<Card> universalDeck = new ArrayList<>();

    //Since these cards are simpler, we can just use a stack. Makes operations slightly simpler

    /**
     * ONLY TO BE USED DURING INITIAL LOADING
     */
    ArrayList<DamageCard> allDamageCards = new ArrayList<>();
    public Stack<DamageCard> virusPile = new Stack<>(); //Pile of cards to draw from
    public Stack<DamageCard> trojanPile = new Stack<>(); //Cards that have been run
    public Stack<DamageCard> wormPile = new Stack<>(); //Pile of cards to draw from
    public Stack<DamageCard> spamPile = new Stack<>(); //Cards that have been run

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

        this.allDamageCards.addAll(cardLoader.getDamageCards());

        for (DamageCard damageCard : allDamageCards){ //Sorts out the damage cards, since they all come in one pile
            switch (damageCard.getName()){
                case "Spam":
                    spamPile.push(damageCard);
                break;
                case "Trojan":
                    trojanPile.push(damageCard);
                break;
                case "Worm":
                    wormPile.push(damageCard);
                    break;
                case "Virus":
                    break;
                default:
                    System.out.println("Something went wrong. We might want to throw an exception");
            }
        }

        System.out.println("Created cardController and piles");
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

    private void addSpamCardToDiscardPile(Player player){
        //player.drawCard();



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
