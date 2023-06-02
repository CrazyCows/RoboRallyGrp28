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

import dk.dtu.compute.se.pisd.roborally.controller.item.LaserGun;
import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import java.util.*;

/**
 * ...
 * Controls the main flow of the game.
 * The GameController is mainly used to control player movement.
 *
 */
public class GameController {

    final public Board board;
    private BoardView boardView;
    private SpaceView spaceView;


    protected CardController cardController;

    public void setBoardView(BoardView boardView){
        this.boardView = boardView;
    }
    public GameController(Board board) {
        this.board = board;
        this.cardController = CardController.getInstance();
        for (Player player : board.getAllPlayers()) {
            cardController.drawCards(player);
        }
        board.setPhase(Phase.PROGRAMMING);
        //this.eventController = new CommandCardController(this);
    }


    //TODO: En metode der tager et commandCardField og læser commands,
    // og kalder de metoder med den korrekte spiller (f.eks. moveForward).

    // TODO lot of stuff missing here



    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do not pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        } //If player needs to be pushed
        player.setSpace(space);
        Player nextPlayer = getNextPlayer(player);
        board.setCurrentPlayer(nextPlayer);
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
                moveToSpace(player, nextSpace, heading);
            } catch (ImpossibleMoveException e) {
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

    // returns the player who is closest to the Priority antenna
    public Player getNextPlayer(Player currentPlayer){
        Space priorityAntenna = board.getPriorityAntennaSpace();
        Player closestPlayerToAntenna = currentPlayer;
        double closest = Double.MAX_VALUE;
        double closeness;
        for (Player player : board.getAllPlayers()) {
            int playerX = player.getSpace().getPosition()[0];
            int playerY = player.getSpace().getPosition()[1];
            System.out.println(player.getColor() + ": " + playerX + ", " + playerY);
            closeness = distanceToSpace(priorityAntenna, playerX, playerY);
            System.out.println(player.getColor() + ": " + closeness);
            if (closeness < closest) {
                closest = closeness;
                closestPlayerToAntenna = player;
            }
        }
        System.out.println("closest player to antenna: " + closestPlayerToAntenna.getColor());
        return board.getPlayer(board.getPlayerNumber(closestPlayerToAntenna));


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

    public void finishProgrammingPhase() {
    }

    // Executes the commandCards
    public void executeProgram(List<ProgrammingCard> programmingCards) {

        cardController.getCardLoader().sendCardSequenceRequest(programmingCards);

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (ProgrammingCard commandCard : programmingCards) {
                    try {
                        commandCard.getAction().doAction(GameController.this, board.getCurrentPlayer(), commandCard);
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
        List<Space> visited = new ArrayList<Space>();
        ArrayList<Space> spaces = space.board.getLaserSpaces();
        for (Space s : spaces){ //This assumes theres only one action per space, which is a laser
            System.out.println("Firing laser");
            LaserGun lg = (LaserGun) s.getItems().get(0).getEvent(); //I think this works?
            visited.addAll(lg.shootLaser(s, s.getItems().get(0).getHeading())); //Runs shootLaser, which returns every field that has been passed by a laser
        }
        System.out.println(visited);

        for (Space s : spaces){
        }



        /*if (space.getItem() != null) {
            if (space.getItem().equals("checkpoint")) {
                space.setItem(null);
                Random rand = new Random();
                int maxHeight = rand.nextInt(board.height);
                int maxWidth = rand.nextInt(board.width);
                SpaceView updatedSpaceView = boardView.getSpaces()[maxWidth][maxHeight];
                space = board.getSpace(maxWidth, maxHeight);
                System.out.println(space.x + " and " + space.y);
                space.setItem("checkpoint");
                updatedSpaceView.addCheckpoint();
            }
        }*/
    }

    /*public void newCheckpoint(Space space) {
        // Checks if player is on checkpoint
        if (space.getItem() != null) {
            if (space.getItem().equals("checkpoint")) {
                // Removes the checkpoint
                space.setItem(null);
                // Randomize a position for next checkpoint
                Random rand = new Random();

                int maxHeight = rand.nextInt(board.height);
                int maxWidth = rand.nextInt(board.width);
                SpaceView updatedSpaceView = boardView.getSpaces()[maxWidth][maxHeight];
                space = board.getSpace(maxWidth, maxHeight);
                while (!space.getActions().isEmpty() || space.getItem() != null || space.getPlayer() != null) {
                    System.out.println("item on place is : " + space.getItem() + " Action on the space is: " + space.getActions() + " Player is on space " + space.getPlayer());
                    maxHeight = rand.nextInt(board.height);
                    maxWidth = rand.nextInt(board.width);
                    updatedSpaceView = boardView.getSpaces()[maxWidth][maxHeight];
                    space = board.getSpace(maxWidth, maxHeight);
                }
                // If the space is free, place a new checkpoint
                space.setItem("checkpoint");
                updatedSpaceView.addCheckpoint();
            }
        }
    }
     */



    /*public void executeStep(Space space) {

        newCheckpoint(space);
        for (FieldAction fieldAction : space.getActions()) {
            fieldAction.doAction(this, space);
        }
    }*/

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This method should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method to be used by a handler
        //     is not yet implemented
    }

    public void win(Player currentPlayer) { //TODO: Add something about winning
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
