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

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;
    public static CardLoader cardLoader;
    private BoardView boardView;
    private SpaceView spaceView;


    protected CardController cardController;
    private EventController eventController;

    public void setBoardView(BoardView boardView){
        this.boardView = boardView;
    }
    public GameController(Board board) {
        this.board = board;
        this.cardController = CardController.getInstance();
        for (Player player : board.getAllPlayers()) {
            cardController.drawCards(player);
        }
        System.out.println(board.getPlayer(0).getDrawnCards());
        board.setPhase(Phase.PROGRAMMING);
        this.eventController = new EventController(this);
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
        }
        player.setSpace(space);
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
        System.out.println(space.getItem());
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

    public Player getNextPlayer(Player currentPlayer){
        int amountOfPlayers = board.getPlayersNumber()-1;
        int playerNumber = board.getPlayerNumber(currentPlayer);

        if (playerNumber >= amountOfPlayers){
            return board.getPlayer(0);
        }
        return board.getPlayer(playerNumber+1);
    }

    public void finishProgrammingPhase() {
    }

    public void executeProgram(List<CommandCard> commandCards) {
        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (CommandCard commandCard : commandCards) {
                    eventController.doAction(board.getCurrentPlayer(), commandCard.command);
                    try {
                        Thread.sleep(420);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        commandThread.start(); // start the thread
    }

    public void newCheckpoint(Space space) {
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

    public void executeStep(Space space) {

        newCheckpoint(space);
        for (FieldAction fieldAction : space.getActions()) {
            fieldAction.doAction(this, space);


            /*if (FieldAction instanceof ConveyorBelt) {
                ConveyorBelt conveyorBelt = (ConveyorBelt) space.getActions().get(0);
                conveyorBelt.doAction(this, space);
            }*/
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This method should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method to be used by a handler
        //     is not yet implemented
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


    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
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
