package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;

import java.sql.SQLOutput;
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
    private CardLoader(){ //Constructor
        System.out.println("Created singleton class");
        createCommandCards();
        System.out.println("Commandcards created");


    }

    protected CommandCard drawCommandCard(){ //Protected as this should only be accessed in controller
        int int_random = rand.nextInt(9);
        return commandCards[int_random];
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
}
