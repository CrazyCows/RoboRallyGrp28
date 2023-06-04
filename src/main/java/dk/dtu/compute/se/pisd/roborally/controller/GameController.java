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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.field.LaserGun;
import dk.dtu.compute.se.pisd.roborally.controller.field.Pit;
import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonPlayerBuilder;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonReader;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

//import java.util.*;

/**
 * ...
 * Controls the main flow of the game.
 * The GameController is mainly used to control player movement.
 *
 */
public class GameController {

    final public Board board;
    //final private JsonPlayerBuilder jsonPlayerBuilder;

    private Pit pit = new Pit();
    private RoboRally roboRally;
    protected CardController cardController;


    public GameController(RoboRally roboRally, Board board) {
        this.roboRally = roboRally;
        this.board = board;
        this.cardController = CardController.getInstance();
        for (Player player : board.getAllPlayers()) {
            cardController.drawCards(player);
        }
        board.setPhase(Phase.PROGRAMMING);
        JsonPlayerBuilder jsonPlayerBuilder = new JsonPlayerBuilder(board.getPlayer(0));
        //this.eventController = new CommandCardController(this);

    }


    //TODO: En metode der tager et commandCardField og læser commands,
    // og kalder de metoder med den korrekte spiller (f.eks. moveForward).

    // TODO lot of stuff missing here


    /**
     * Returns true if all goes as planned. If all future calls of the function needs to be cancelled, as in the case
     * of falling into a pit or off the map, the function returns false.
     **/
    public boolean moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading,false);
            try {
                return (moveToSpace(player, target, heading));
            } catch (ImpossibleMoveException e) {
                // we don't do anything here  for now; we just catch the
                // exception so that we do not pass it on to the caller
                // (which would be very bad style).
                System.out.println("Impossible move caught");
                return true; //TODO: Not sure if this should return true or false
            }
        }
        return true;
    }
    /**
    * Returns true if all goes as planned. If all future calls of the function needs to be cancelled, as in the case
    * of falling into a pit or off the map, the function returns false.
    **/
    boolean moveToSpace(@NotNull Player originalPlayer, Space originalTarget, @NotNull Heading heading) throws ImpossibleMoveException {
        //There is already checks for walls somewhere else, but because this is called recursively I cant use that

        boolean OGTargetIsNull = (originalTarget == null);
        boolean cond1 = originalPlayer.getSpace().getWalls().contains(heading);//Checks whether theres a wall in the way on the start field
        boolean cond2 = false;
        if (!OGTargetIsNull) {cond2 = originalTarget.getWalls().contains(heading.next().next());} //Checks whether theres a wall on the destination field, facing the start field

        if (cond1 || cond2){
            System.out.println(originalPlayer.getName() + " hit a wall");
            Player nextPlayer = getNextPlayer(originalPlayer);
            board.setCurrentPlayer(nextPlayer);
            return false;
        }

        if (OGTargetIsNull){
            pit.doAction(this,originalPlayer.getSpace());
            Player nextPlayer = getNextPlayer(originalPlayer);
            board.setCurrentPlayer(nextPlayer);
            return false;
        }
        //jsonPlayerBuilder.updateDynamicPlayerData(board.getPlayer(0));
        assert board.getNeighbour(originalPlayer.getSpace(), heading,true) == originalTarget; // make sure the move to here is possible in principle
        Player other = originalTarget.getPlayer();
        if (other != null){ //If player needs to be pushed
            Space newTarget = board.getNeighbour(originalTarget, heading,true);
            return(moveToSpace(other,newTarget,heading));
        }

        for (FieldAction fieldAction : originalTarget.getActions()){
            if (fieldAction instanceof Pit){
                ((Pit)fieldAction).doAction(this,originalPlayer); //TODO: originalTarget doesnt contain the player
                break;
            }
        }

        originalPlayer.setSpace(originalTarget);// I don't understand this.... Lucas? - Crazy
        return true;
    }

    public void moveCurrentPlayerToSpace(Space space) {

        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved
        if (space.getPlayer() != null) return;

        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setSpace(space);

        Player nextPlayer = getNextPlayer(currentPlayer);
        board.setCurrentPlayer(nextPlayer);
        if (!space.getItems().isEmpty()) {
            for (Item item : space.getItems()) {
                System.out.println(item.getName());
            }
        }
    }

    public void moveInDirection(@NotNull Player player, int amount, @NotNull Heading heading) {
        Space space = player.getSpace();
        int[] spacePosition = space.getPosition();
        for (int i = 0; i < amount; i++) {
            switch (heading) {
                case NORTH -> {
                    spacePosition[1] -= 1;
                }
                case WEST -> {
                    spacePosition[0] -= 1;
                }
                case SOUTH -> {
                    spacePosition[1] += 1;
                }
                case EAST -> {
                    spacePosition[0] += 1;
                }
            }
            try {
                Space nextSpace = board.getSpace(spacePosition[0], spacePosition[1]);
                if (moveToSpace(player, nextSpace, heading)){ //Basically checks if the player is moved into a pit
                    for (FieldAction fieldAction : nextSpace.getActions()){
                        if (fieldAction instanceof Pit){
                            fieldAction.doAction(this,nextSpace);
                            break;
                        }
                    }
                }



            } catch (ImpossibleMoveException e) {
                System.out.println("Impossible move caught");
                break;
            }
        }
        //player.setSpace(board.getSpace(spacePosition[0], spacePosition[1]));
    }

    public void turnPlayer(Player player, Command direction) {
        if (direction == Command.LEFT) {
            player.setHeading(player.getHeading().prev());
        } else if (direction == Command.RIGHT) {
            player.setHeading(player.getHeading().next());
        }

    }
    /*
    * !!!!!!!!!DISABLED FOR TESTING; REMEMBER TO REACTIVATE!!!!!!
    * !!!!!!!!!DISABLED FOR TESTING; REMEMBER TO REACTIVATE!!!!!!
    * !!!!!!!!!DISABLED FOR TESTING; REMEMBER TO REACTIVATE!!!!!!
    *
     */
    // returns the player who is closest to the Priority antenna
    public Player getNextPlayer(Player currentPlayer){

        Space priorityAntenna = board.getPriorityAntennaSpace();
        Player closestPlayerToAntenna = currentPlayer;

        int usedCards = Integer.MAX_VALUE;
        for (Player player : board.getAllPlayers()){
            if (player.getUsedCards() < usedCards){ //Finds minimum value
                usedCards = player.getUsedCards();
            }
        }
        ArrayList<Player> possiblePlayers = new ArrayList<>();
        for (Player player : board.getAllPlayers()){
            if (player.getUsedCards() == usedCards){
                possiblePlayers.add(player);
            }
        }

        double closest = Double.MAX_VALUE;

        double closeness;
        for (Player player : possiblePlayers) {//Determines the closest of the eligible players
            int playerX = player.getSpace().getPosition()[0];
            int playerY = player.getSpace().getPosition()[1];
            closeness = distanceToSpace(priorityAntenna, playerX, playerY);
            if (closeness < closest) {
                closest = closeness;
                closestPlayerToAntenna = player;
            }
        }
        System.out.println("closest player to antenna: " + closestPlayerToAntenna.getColor());
        //return board.getPlayer(board.getPlayerNumber(closestPlayerToAntenna));
        return closestPlayerToAntenna;


        /*int amountOfPlayers = board.getPlayersNumber()-1;
        int playerNumber = board.getPlayerNumber(currentPlayer);

        if (playerNumber >= amountOfPlayers){
            return board.getPlayer(0);
        }
        return board.getPlayer(playerNumber+1);*/
    }

    // Calculates the distance between the priority antenna and a given player
    private double distanceToSpace(Space space, int otherX, int otherY) {
        int x = space.getPosition()[0];
        int y = space.getPosition()[1];
        int dx = x - otherX;
        int dy = y - otherY;
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * 'Used in the single player version only, afaik' -Anton
     */
    public void finishProgrammingPhase() {

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                board.getCurrentPlayer().currentProgram();
                Player currentPlayer = board.getCurrentPlayer();
                while (true){
                    try {
                        ProgrammingCard programmingCard = currentPlayer.currentProgram().get(currentPlayer.getUsedCards());
                        System.out.println("\nCurrent player is " + board.getCurrentPlayer().getName() + ", they play " + programmingCard.getName() + " and they've used " + currentPlayer.getUsedCards() + " cards (about to use one more)");
                        programmingCard.getAction().doAction(GameController.this, board.getCurrentPlayer(), programmingCard); //I hate this implementation
                        Thread.sleep(420);
                    }
                    catch (NullPointerException e) {
                        System.out.println("Error: No more commandCards");
                    }
                    catch (InterruptedException e) {
                        //This is just here for the sleep. Shouldn't really happen
                    }
                    catch (IndexOutOfBoundsException e){
                        System.out.println("Trying to get a card that was removed from the hand");
                    }
                    currentPlayer.incrementUsedCards();
                    currentPlayer = getNextPlayer(currentPlayer);
                    board.setCurrentPlayer(currentPlayer);
                    boolean toBreak = true;
                    for (Player player : board.getAllPlayers()){
                        if (player.getUsedCards() < Player.NO_REGISTERS && !(player.currentProgram().size() == 0)){
                            toBreak = false;
                        }
                    }
                    if (toBreak){
                        break;
                    }
                }

                for (Player player : board.getAllPlayers()){
                    player.resetUsedCards();
                }
            }
        });
        commandThread.start();
    }

    // Executes the commandCards
    public void executeProgram(List<ProgrammingCard> programmingCards) {

        cardController.getCardLoader().sendCardSequenceRequest(programmingCards);

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (ProgrammingCard commandCard : programmingCards) {
                    try {
                        commandCard.getAction().doAction(GameController.this, board.getCurrentPlayer(), commandCard); //I hate this implementation
                        Thread.sleep(420);
                    } catch (NullPointerException e) {
                        System.out.println("Error: No more commandCards");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        commandThread.start(); // start the thread
    }

    // executes a single step
    public void executeStep(Space space) {
        // Execute field actions
        for (FieldAction fieldAction : space.getActions()) {
            fieldAction.doAction(this, space);
        }

        // Create a copy of the items list
        List<Item> itemsToProcess = new ArrayList<>(space.getItems());

        // Process items in the copied list
        for (Item item : itemsToProcess) {
            System.out.println(item.getName() + " is space things ");
            item.getEvent().doAction(this, space);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("end of executestep");
    }


    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This method should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method to be used by a handler
        //     is not yet implemented
    }

    public void win(Player currentPlayer) {
        //TODO: Display that a player won with some graphics and stop GUI(?)

        FXMLLoader fxmlLoader = new FXMLLoader(RoboRally.class.getResource("scenes/winnerScreen.fxml"));
        try {
            Parent parent = fxmlLoader.load();
            WinnerController winnerController = fxmlLoader.<WinnerController>getController();
            winnerController.initialize(roboRally, currentPlayer.getName(), board.getAllPlayers().size());
            roboRally.getStage().getScene().setRoot(parent);
            roboRally.getStage().getScene().getWindow().sizeToScene();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("The player " + currentPlayer.getName() + " has won!");

    }


    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

    public boolean clearField(@NotNull CommandCardField source){
        source.setCard(null);
        return true;
    }

    // Makes cards movable from one slot to another.
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        ProgrammingCard sourceCard = source.getCard();
        ProgrammingCard targetCard = target.getCard();
        if (sourceCard != null & targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            System.out.println("Current Program status: " + Arrays.toString(board.getCurrentPlayer().currentProgram().toArray()));
            return true;
        } else {
            return false;
        }
    }
}
