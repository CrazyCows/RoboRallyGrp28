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
import dk.dtu.compute.se.pisd.roborally.controller.field.Pit;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ClientController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonInterpreter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonPlayerBuilder;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.*;

//import java.util.*;

/**
 * ...
 * Controls the main flow of the game.
 * The GameController is mainly used to control player movement.
 *
 */
public class GameController {

    final public Board board;
    private boolean online;

    private Pit pit = new Pit();
    private RoboRally roboRally;

    public CardController getCardController() {
        return cardController;
    }
    private ClientController clientController;

    protected CardController cardController;
    private JsonInterpreter jsonInterpreter;

    private Player localPlayer;
    boolean MoreAdvancedGame = true;
    boolean firstRound;

    private Timer timer;


    public GameController(RoboRally roboRally, ClientController clientController, Board board, boolean online, Player localPlayer) {
        this.roboRally = roboRally;
        this.clientController = clientController;
        this.board = board;
        this.cardController = CardController.getInstance();
        this.jsonInterpreter = new JsonInterpreter();
        for (Player player : board.getAllPlayers()) {
            cardController.copyOverUniversalDeck(player);
        }
        this.online = online;
        setPhase(Phase.PROGRAMMING);
        JsonPlayerBuilder jsonPlayerBuilder = new JsonPlayerBuilder(board.getPlayer(0));
        //this.eventController = new CommandCardController(this); //TODO: Should these two be removed?

        if (online) {
            this.localPlayer = localPlayer;
            firstRound = true;
            localPlayer.setReady(false);
            jsonPlayerBuilder.updateDynamicPlayerData();
            clientController.updateJSON("playerData.json");
        }

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
                ((Pit)fieldAction).doAction(this,originalPlayer);
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
                case NORTH -> spacePosition[1] -= 1;
                case WEST -> spacePosition[0] -= 1;
                case SOUTH -> spacePosition[1] += 1;
                case EAST -> spacePosition[0] += 1;
            }
            try {
                Space nextSpace = board.getSpace(spacePosition[0], spacePosition[1]);
                if (moveToSpace(player, nextSpace, heading)){ //Moves the player and basically checks if they fell in a pit
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

    public double distanceToSpace(Space space, Space space2) {
        int x = space.getPosition()[0];
        int y = space.getPosition()[1];
        int otherX = space2.getPosition()[0];
        int otherY = space2.getPosition()[1];
        int dx = x - otherX;
        int dy = y - otherY;
        return Math.sqrt(dx*dx + dy*dy);
    }
    private double distanceToSpace(Space space, int otherX, int otherY) {
        int x = space.getPosition()[0];
        int y = space.getPosition()[1];
        int dx = x - otherX;
        int dy = y - otherY;
        return Math.sqrt(dx*dx + dy*dy);
    }

    void setPhase(Phase phase){
        if (phase == PROGRAMMING){
            for (Player player : board.getAllPlayers()){
                cardController.drawCards(player); //I dont think this breaks MVC?
            }
        }
        board.setPhase(phase);
    }

    public void startTimer() {
        timer = new Timer();
        board.setTimerIsRunning(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                board.setTimerSecondsCount(board.getTimerSecondsCount() + 1);
                System.out.println("timer: " + board.getTimerSecondsCount());
                if (board.getTimerSecondsCount() >= 30) {
                    timer.cancel();
                    timer.purge();
                    board.setTimerIsRunning(false);
                    board.setTimerSecondsCount(0);
                    System.out.println("Time to fire event!");
                }
            }
        }, 0, 1000);
        board.setTimerSecondsCount(0);
    }

    public void synchronize() {
        setPhase(SYNCHRONIZATION);
        int getReadyTries = 0;
        while (!jsonInterpreter.isAllReady() || !localPlayer.isReady()) {
            try {
                System.out.println("Info: All local timers should have ended. ");
                Thread.sleep(1000);
                getReadyTries += 1;
                if (getReadyTries > 30) {
                    for (Player player : board.getAllPlayers()) {
                        if (!jsonInterpreter.isReady(player.getName())) {
                            board.removePlayer(player);
                            System.out.println(player.getName() + " has been removed from game due to unavailability");
                            System.out.println("Warning: May cause unexpected behavior. ");
                        }
                    }
                    break;
                }
            } catch (InterruptedException e) {
                System.out.println("Error: Unexpected synchronization behavior. ");
                e.printStackTrace();
                break;
            }
        }


        if (firstRound) {
            cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
            clientController.createJSON("cardSequenceRequest.json");
            firstRound = false;
        }

        for (Player player : board.getAllPlayers()) {
            int count = 0;
            for (Card card : player.currentProgram()) {
                if (card instanceof DamageCard) {
                    while (card instanceof DamageCard) {
                        card = player.drawCardFromPile();
                    }
                    player.getProgram().get(count).setCard(card);
                }
                count += 1;
            }
        }

        cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
        clientController.updateJSON("cardSequenceRequest.json");
        clientController.getJSON("cardSequenceRequest.json");
        for (Player player : board.getAllPlayers()) {
            System.out.println(player.getName());
            if (player != localPlayer) {
                cardController.emptyProgram(player);
                ArrayList<ProgrammingCard> cards = cardController.getCardLoader().loadCardSequence(player.getName());
                int counter = 0;
                for (CommandCardField field : player.getProgram()) {
                    field.setCard(cards.get(counter));
                    counter += 1;
                }
            }
        }
    }


    public void finishProgrammingPhase() {

        //TODO: Check for spam and trojan cards,and replaces the card somehow?
        //TODO: Very much WIP

        if (online) {
            synchronize();
        }

        setPhase(Phase.ACTIVATION);

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                board.getCurrentPlayer().currentProgram();
                Player currentPlayer = board.getCurrentPlayer();
                while (true){
                    try {
                        Card card = currentPlayer.currentProgram().get(currentPlayer.getUsedCards());
                        System.out.println("\nCurrent player is " + board.getCurrentPlayer().getName() + ", they play " + card.getName() + " which is at slot number " + (currentPlayer.getUsedCards() + 1));
                        card.getAction().doAction(GameController.this, board.getCurrentPlayer(), card); //I hate this implementation
                        Thread.sleep(420); //Generify?
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
                    cardController.emptyProgram(player);

                    if (!MoreAdvancedGame){
                        cardController.clearhand(player);
                    }
                }
                setPhase(Phase.PROGRAMMING);
            }
        });
        commandThread.start();
    }

    // Executes the commandCards
    public void executeProgram(List<Card> cards) {

        if (online) {
            for (Player player : board.getAllPlayers()) {
                int count = 0;
                for (Card card : player.currentProgram()) {
                    if (card instanceof DamageCard) {
                        while (card instanceof DamageCard) {
                            card = player.drawCardFromPile();
                        }
                        player.getProgram().get(count).setCard(card);
                    }
                    count += 1;
                }
            }
            cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
        }

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Card card : cards) {

                    if (card instanceof ProgrammingCard){
                        ProgrammingCard commandCard = (ProgrammingCard) card;
                        try {
                            commandCard.getAction().doAction(GameController.this, board.getCurrentPlayer(), commandCard); //I hate this implementation
                            Thread.sleep(420);
                        } catch (NullPointerException e) {
                            System.out.println("Error: No more commandCards");
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("Something needs to be done about this card");
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
        roboRally.winScreen(currentPlayer);
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

    /**
     * TODO: This function should probably be deleted, as this is in the domain of the cardController
     */
    public boolean clearField(@NotNull CommandCardField source){
        source.setCard(null);
        return true;
    }

    // Makes cards movable from one slot to another.
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        Card sourceCard =  source.getCard();
        Card targetCard = target.getCard();
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
