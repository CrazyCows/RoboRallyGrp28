package dk.dtu.compute.se.pisd.roborally.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static dk.dtu.compute.se.pisd.roborally.model.Command.*;
//import static dk.dtu.compute.se.pisd.roborally.model.Command.LEFT;

public class CardLoader {
    private static CardLoader cardLoader = null;
    Random rand = new Random();
    CommandCard[] commandCards;

    public static CardLoader getInstance(){ //Singleton
        if (cardLoader == null){
            cardLoader = new CardLoader();
        }
        return cardLoader;
    }

    public CommandCard drawRandomCommandCard(){  // TODO: Should be Protected (?) as this should only be accessed in controller
        int int_random = rand.nextInt(9); // TODO: This is a teacher comment, but has been changed to public for now
        return commandCards[int_random];
    }

    public LinkedList<CommandCard> CreateCardPile(){ //TODO: ADD GENERICS
        //TODO: Actually make this the correct cards
        LinkedList<CommandCard> pile = new LinkedList<>(Arrays.asList(commandCards));
        return pile;
    }

    public LinkedList<Object> CreateEmptyPile(){
        return new LinkedList<Object>();
    }

    private CommandCard[] createCommandCards(){ //Private, only used once, in constructor
        commandCards = new CommandCard[]{ //This is hardcoded, very yummy. Shouldnt wary across game versions so its fine
                new CommandCard(MOVEONE),
                new CommandCard(MOVETWO),
                new CommandCard(MOVETHREE),
                new CommandCard(RIGHT),
                new CommandCard(LEFT),
                new CommandCard(UTURN),
                new CommandCard(BACKUP),
                new CommandCard(POWERUP),
                new CommandCard(AGAIN)
        };
        return commandCards;
    }

    private CardLoader(){ //Constructor
        System.out.println("Created singleton class");
        createCommandCards();
        System.out.println("Commandcards created");
    }
}
