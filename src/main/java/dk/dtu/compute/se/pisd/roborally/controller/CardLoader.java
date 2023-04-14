package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;

import java.util.Random;

import static dk.dtu.compute.se.pisd.roborally.model.Command.*;
//import static dk.dtu.compute.se.pisd.roborally.model.Command.LEFT;

public class CardLoader {
    private static CardLoader cardLoader = null;
    Random rand = new Random();


    public static CardLoader getInstance(){
        if (cardLoader == null){
            cardLoader = new CardLoader();
        }
        return cardLoader;
    }
    private CardLoader(){
        System.out.println("Created singleton class");
    }



    public CommandCard drawCommandCard(){
        int int_random = rand.nextInt(9);
        return CommandCards[int_random];
    }

    CommandCard[] CommandCards = new CommandCard[]{
            new CommandCard(MOVEONE),
            new CommandCard(MOVETWO),
            new CommandCard(MOVETHREE),
            new CommandCard(RIGHT),
            new CommandCard(LEFT),
            new CommandCard(UTURN),
            new CommandCard(BACKUP),
            new CommandCard(POWERUP),
            new CommandCard(AGAIN),
    };
}
