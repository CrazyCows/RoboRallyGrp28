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
import dk.dtu.compute.se.pisd.roborally.model.card.TempUpgradeCard;
import dk.dtu.compute.se.pisd.roborally.model.card.UpgradeCard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {


    //VARIABLES

    final public static int NO_REGISTERS = 5;
    public static int NO_CARDS = 9; //Not final because a permanent upgrade card can increase this. Merge with other values

    final public Board board;

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

    //The amount of energy a player has. Starts at zero
    private int energyCubes = 0;

    //The last card that the player used
    private Card lastCard = null;
    private int checkpointsCollected = 0;

    //TODO: These are already defined, duplicates are unnecessary
    private static int handSize = 9;  //Not final because a permanent upgrade card can increase this
    private static final int programSize = 5;
    private ArrayList<CommandCardField> program = new ArrayList<>(); //Cards selected to be the in the program
    private ArrayList<CommandCardField> handPile = new ArrayList<>(); //Drawn cards
    public ArrayList<Card> drawPile = new ArrayList<>(); //Pile of cards to draw from
    public ArrayList<Card> discardPile = new ArrayList<>(); //Cards that have been run
    public String message;
    public ArrayList<Card> permUpgradeCards;
    public ArrayList<Card> tempUpgradeCards;

    //
    //SIMPLE GETTERS AND SETTERS
    //

    public Card getLastCard() {
        return lastCard;
    }

    /**
     * Should be used for respawning when not on the same board as the reboot token
     * @return the space the player initially started on.
     */
    public Space getStartSpace() {return startSpace;}
    public void setLastCard(Card lastCard) {
        this.lastCard = lastCard;
    }
    public int getUsedCards(){
        return usedCards;
    }
    public void incrementUsedCards(){
        usedCards++;
    }
    public int getEnergyCubes() { //Maybe make this concurrent?
        return energyCubes;
    }
    public ArrayList<Card> getDrawPile() {
        return this.drawPile;
    }
    public ArrayList<Card> getDiscardPile() {
        return this.discardPile;
    }
    public void setEnergyCubes(int amount) {
        this.energyCubes = amount;
    }
    public Space getSpace() {
        return space;
    }
    public String getName() {
        return name;
    }
    public ArrayList<CommandCardField> getProgram() {return program;}
    public ArrayList<CommandCardField> getHandPile() {
        return handPile;
    }
    public void addEnergyCubes(int energyCubesAdded) {
        this.energyCubes += energyCubesAdded;
    }
    public void resetUsedCards(){ //Prevents misuse of usedCards
        usedCards = 0;
    }
    public Heading getHeading() {
        return heading;
    }
    public CommandCardField getCardField(int i) {
        return handPile.get(i);
    }
    public int getCheckpointsCollected() {
        return checkpointsCollected;
    }
    public void setCheckpointsCollected(int amount) {
        this.checkpointsCollected = amount;
    }
    public void incrementCheckpointsCollected() {this.checkpointsCollected += 1;}
    public int getHandSize() {
        return handSize;
    }
    public int getProgramSize() {
        return programSize;
    }
    public boolean isReady() {
        return ready;
    }
    public synchronized void setReady(boolean state) {
        this.ready = state;
    }
    public boolean isInGame() {
        return this.inGame;
    }
    public void setInGame(boolean status) {
        this.inGame = status;
        notifyChange();
    }

    public int getAmountAllUpgradeCards() {
        return permUpgradeCards.size() + tempUpgradeCards.size();
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
    public boolean subtractEnergyCubes(int energyCubesUsed) {
        if (this.energyCubes - energyCubesUsed < 0){
            return false;
        }
        this.energyCubes -= energyCubesUsed;
        return true;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyChange();
    }

    public String getMessage() {
        return this.message;
    }



    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.message = "";

        this.space = null;
        this.permUpgradeCards = new ArrayList<>();
        this.tempUpgradeCards = new ArrayList<>();

        program = new ArrayList<>();
        for (int i = 0; i < programSize ; i++) {
            program.add(new CommandCardField(this));
        }

        handPile = new ArrayList<>();
        for (int i = 0; i < handSize; i++) {
            handPile.add(new CommandCardField(this));
        }
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

    public String getColor() {return color;}
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

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

    public void setAllCards(ArrayList<Card> cards) {
        int count = 0;
        for (CommandCardField field : this.getProgram()) {
            field.setCard(cards.get(count));
            count += 1;
        }
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
        //Heading head = Heading.valueOf(heading); This should be a simplification but due to time constraints and lack
        //of unit tests, we are keeping the old method (it just works)
        switch (heading) {
            case "NORTH" -> this.heading = Heading.NORTH;
            case "EAST" -> this.heading = Heading.EAST;
            case "SOUTH" -> this.heading = Heading.SOUTH;
            case "WEST" -> this.heading = Heading.WEST;
            default -> {
            }
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

    /**
     * Draws the one card and inserts it into the first free CommandCardField in the handPile
     * @param card Card to be drawn
     */
    public void drawCard(Card card) {
        for (int i = 0; i < handSize + 1;i++){
            if (handPile.get(i).getCard() == null){
                handPile.get(i).setCard(card);
                break;
            }
        }
    }


    /**
     * THIS FUNCTION IS WRONG
     * @return
     */
    public Card drawCardFromPile() {
        Card card = drawPile.get(0);
        drawPile.remove(card);
        return card;
    }

    /**
     * DEPRECATED
     * A bit dirty in the name and in the game. It returns the cards from each commandCardField in the handPile of the player
     * @return An arrayList of the cards in the handPile
     */
    public ArrayList<Card> getCopyOfHandPile() {
        ArrayList<Card>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.handPile) {
            commandCards.add(commandCardField.getCard());
        }
        return commandCards;
    }

    /**
     * You should use getCopyOfHandPile instead. This casts all cards to ProgrammingCard, which will result in an error
     * if the player has a damage card
     * @return
     */
    public ArrayList<ProgrammingCard> currentProgramProgrammingCards() {
        ArrayList<ProgrammingCard>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.program) {
            try {
                commandCards.add((ProgrammingCard) commandCardField.getCard());
            } catch (ClassCastException e) {
                System.out.println("Warning: A card in program was not a programmingCard");
                e.printStackTrace();
            }
        }
        return commandCards;
    }

    public void addPermanentUpgradeCard(UpgradeCard permanentUpgradeCard) {
        this.permUpgradeCards.add(permanentUpgradeCard);
    }
    public void removePermanentUpgradeCard(UpgradeCard permanentUpgradeCard) {
        this.permUpgradeCards.remove(permanentUpgradeCard);
    }
    public void removePermanentUpgradeCard(String name) {
        for (int i = 0; i < permUpgradeCards.size(); i++) {
            if (permUpgradeCards.get(i).getName().equals(name)) {
                permUpgradeCards.remove(i);
                break;
            }
        }
    }
    public ArrayList<Card> getPermanentUpgradeCards() {
        return this.permUpgradeCards;
    }

    public Card getPermanentUpgradeCard(int atPosition) {
        return this.permUpgradeCards.get(atPosition);
    }

    public void addTemporaryUpgradeCard(TempUpgradeCard temporaryUpgradeCard) {
        this.tempUpgradeCards.add(temporaryUpgradeCard);
    }
    public void removeTemporaryUpgradeCard(TempUpgradeCard temporaryUpgradeCard) {
        this.tempUpgradeCards.remove(temporaryUpgradeCard);
    }
    public void removeTemporaryUpgradeCard(String name) {
        for (int i = 0; i < tempUpgradeCards.size(); i++) {
            if (tempUpgradeCards.get(i).getName().equals(name)) {
                tempUpgradeCards.remove(i);
                break;
            }
        }
    }
    public ArrayList<Card> getTemporaryUpgradeCards() {
        return this.tempUpgradeCards;
    }

    public Card getTemporaryUpgradeCard(int atPosition) {
        return this.tempUpgradeCards.get(atPosition);
    }




    /**
     * @return The cards from the current program. Always 5 long, but some can be null if not filled out
     */
    public ArrayList<Card> currentProgram() {
        ArrayList<Card>  commandCards = new ArrayList<>();
        for (CommandCardField commandCardField : this.program) {
            commandCards.add(commandCardField.getCard());
        }
        return commandCards;
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

    public boolean hasCard(String cardName) {
        for (Card card : permUpgradeCards){
            if (Objects.equals(card.getName(), cardName)){
                return true;
            }
        }
        return false;
    }
}
