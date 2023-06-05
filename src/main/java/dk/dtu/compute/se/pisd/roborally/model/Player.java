/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    public Space getStartSpace() {
        return startSpace;
    }

    //TODO: Use getters instead
    public Space startSpace;

    private String name;
    private String color;
    private boolean ready;
    private boolean leader;
    private boolean isMaster;
    private String master;
    private boolean inGame;

    //used for keeping track so the priorityAntenna doesn't wildly pick the same player 5 times in a row
    private int usedCards;

    private Space space;
    private Heading heading = SOUTH;
    private int energyCubes = 0;
    private Card lastCard = null;

    private ArrayList<CommandCardField> program = new ArrayList<>(); //Cards selected to be the in the program
    private ArrayList<CommandCardField> handPile = new ArrayList<>(); //Drawn cards
    public ArrayList<Card> drawPile = new ArrayList<>(); //Pile of cards to draw from
    public ArrayList<Card> discardPile = new ArrayList<>(); //Cards that have been run

    public Card getLastCard() {
        return lastCard;
    }

    public void setLastCard(Card lastCard) {
        this.lastCard = lastCard;
    }

    //The last card that the player used

    public void addEnergyCubes(int energyCubesAdded) {
        this.energyCubes += energyCubesAdded;
    }

    public int getUsedCards(){
        return usedCards;
    }
    public void incrementUsedCards(){
        usedCards++;
    }
    public void resetUsedCards(){ //Prevents misuse of usedCards
        usedCards = 0;
    }



    public int getEnergyCubes() {
        return energyCubes;
    }

    //The amount of energy a player has. Starts at zero

    public ArrayList<CommandCardField> getProgram() {
        return program;
    }

    public ArrayList<CommandCardField> getHandPile() {
        return handPile;
    }


    //This is used to keep track of how many checkpoints are collected. Each time a checkpoint is reached,
    //checkpointsCollected is to be incremented by one. Once it reaches the magic number, the player wins
    private int checkpointsCollected = 0;
    private static int handSize = 8;
    private static int programSize = 5;


    public boolean subtractEnergyCubes(int energyCubesUsed) {
        if (this.energyCubes - energyCubesUsed < 0){
            return false;
        }
        this.energyCubes -= energyCubesUsed;
        return true;
    }

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new ArrayList<>();
        for (int i = 0; i < programSize ; i++) {
            program.add(new CommandCardField(this));
        }

        handPile = new ArrayList<>();
        for (int i = 0; i < handSize; i++) {
            handPile.add(new CommandCardField(this));
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player
     * @param name new name
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

/*
* I think this is where players gets pushed?
 */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }

            if (this.startSpace == null && space != null){ //I assume this work
                this.startSpace = space;
            }
            notifyChange();
        }
    }

    public void setSpace(int x, int y) {
        space = board.getSpace(x, y);
        if (space != null) {
            space.setPlayer(this);
        }
        notifyChange();
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void setHeading(@NotNull String heading) {
        Heading head = Heading.valueOf(heading);//TODO: Can't we just do this?
        switch (heading) {
            case "NORTH":
                this.heading = Heading.NORTH;
                break;
            case "EAST":
                this.heading = Heading.EAST;
                break;
            case "SOUTH":
                this.heading = Heading.SOUTH;
                break;
            case "WEST":
                this.heading = Heading.WEST;
                break;
            default:

        }
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public CommandCardField getProgramField(int i) {
        try{
            return program.get(i);
        }
        catch (IndexOutOfBoundsException e){
            System.out.println("Something done goofed");
            return program.get(i);
        }


    }

    public CommandCardField getCardField(int i) {
        return handPile.get(i);
    }

    public int getCheckpointsCollected() {
        return checkpointsCollected;
    }

    public void incrementCheckpointsCollected() { //TODO: check if player has won
        this.checkpointsCollected += 1;
    }

    public void setCardField(int i, Object card){

    }

    public void drawCard(Card card) {
        //ProgrammingCard c = (ProgrammingCard) card; //TODO: Will this not break with damage cards?
        for (int i = 0; i < 1000;i++){ //TODO: Shouldnt be 1k
            if (handPile.get(i).getCard() == null){
                handPile.get(i).setCard(card);
                break;
            }
        }
    }

    public ArrayList<Card> getCopyOfHandPile() {
        ArrayList<Card>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.handPile) {
            commandCards.add(commandCardField.getCard());
        }
        return commandCards;
    }

    public ArrayList<ProgrammingCard> currentProgram2() {
        ArrayList<ProgrammingCard>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.program) {
            commandCards.add((ProgrammingCard) commandCardField.getCard());
        }
        return commandCards;
    }

    public ArrayList<Card> currentProgram() {
        ArrayList<Card>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.program) {
            commandCards.add(commandCardField.getCard());
        }
        return commandCards;
    }

    public int getHandSize() {
        return handSize;
    }

    public int getProgramSize() {
        return programSize;
    }

    public int getNextEmptyCardField() {
        for (int i = 0; i < handPile.size(); i++) {
            if (handPile.get(i).getCard() == null) {
                return i;
            }
        }
        return -1;
    }

    public int getNextEmptyProgramField() {
        for (int i = 0; i < program.size(); i++) {
            if (program.get(i).getCard() == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean isReady() {
        return ready;
    }
    public void setReady(boolean state) {
        this.ready = state;
    }

    public boolean isInGame() {
        return this.inGame;
    }

    public void setInGame(boolean status) {
        this.inGame = status;
    }

    public boolean isLeader() {
        return leader;
    }
    public boolean isMaster() { return this.isMaster; }
    public void setMasterStatus(boolean status) {
        this.isMaster = status;
    }
    public String getMaster() {
        return this.master;
    }
    public void setMaster(String name) {
        this.master = name;
    }

}
