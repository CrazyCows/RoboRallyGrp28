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

import com.sun.jdi.ThreadGroupReference;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.field.Pit;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ClientController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonInterpreter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonPlayerBuilder;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.card.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.*;

//import java.util.*;

/**
 * ...
 * Controls the main flow of the game.
 *
 */
public class GameController {

    final public Board board;
    private final boolean online;
    private Pit pit = new Pit();
    private RoboRally roboRally;
    public CardController getCardController() {
        return cardController;
    }
    private ClientController clientController;
    private ChatController chatController;
    protected CardController cardController;
    private JsonInterpreter jsonInterpreter;
    private String gamePath;
    private static Player localPlayer;
    JsonPlayerBuilder jsonPlayerBuilder;
    boolean MoreAdvancedGame = true;
    volatile boolean firstRound;
    private Timer timer;
    private boolean localStartedTimer;
    AtomicBoolean stopTimerBeforeTime = new AtomicBoolean(false);
    ArrayList<String> playerNames;

    public GameController(RoboRally roboRally, ClientController clientController, Board board, boolean online, Player localPlayer) {
        this.roboRally = roboRally;
        this.clientController = clientController;
        this.board = board;

        for (Player player : board.getAllPlayers()) {
            player.addEnergyCubes(5);
        }
        this.cardController = CardController.getInstance();
        ArrayList<UpgradeCard> permanentUpgradeCards = this.cardController.getCardLoader().getPermUpgradeCards();
        ArrayList<TempUpgradeCard> temporaryUpgradeCards = this.cardController.getCardLoader().getTempUpgradeCards();
        this.board.getUpgradeShop().setPermanentUpgradeDeck(permanentUpgradeCards);
        this.board.getUpgradeShop().setTemporaryUpgradeDeck(temporaryUpgradeCards);
        this.jsonInterpreter = new JsonInterpreter();
        for (Player player : board.getAllPlayers()) {
            cardController.copyOverUniversalDeck(player);
            for (Card card : player.getPermanentUpgradeCards()) {
                board.getUpgradeShop().removePermanentUpgradeCardByName(card.getName());
            }
            for (Card card : player.getTemporaryUpgradeCards()) {
                board.getUpgradeShop().removeTemporaryUpgradeCardByName(card.getName());
            }
        }
        this.localStartedTimer = false;
        this.online = online;
        jsonPlayerBuilder = new JsonPlayerBuilder(board.getPlayer(0));
        //this.eventController = new CommandCardController(this); //TODO: Should these two be removed?
        if (online) {
            this.localPlayer = localPlayer;
            firstRound = true;
        }
    }

    public void setupOnline() {

        localPlayer.setReady(true);
        jsonPlayerBuilder.updateDynamicPlayerData();
        clientController.updateJSON("playerData.json");
        clientController.getJSON("playerData.json");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chatController = new ChatController(this);
        localPlayer.setReady(false);
        jsonPlayerBuilder.updateDynamicPlayerData();
        clientController.updateJSON("playerData.json");
        clientController.getJSON("playerData.json");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (jsonInterpreter.isAnyReady(jsonInterpreter.getPlayerNames())) {
            try {
                clientController.getJSON("playerData.json");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Thread countThread = new Thread(() -> {
            playerNames = new ArrayList<>();
            for (Player player : board.getAllPlayers()) {
                if (player != localPlayer) {
                    playerNames.add(player.getName());
                }
            }
            getUpdates(playerNames);
        });
        countThread.setDaemon(true);
        countThread.start();
    }

    public synchronized void getUpdates(ArrayList<String> playerNames) {
        clientController.getJSON("playerData.json");
        System.out.println(jsonInterpreter.isAnyReady(playerNames) + " and " + getLocalPlayer().isReady());
        System.out.println("Continually polling for updates");
        while (!jsonInterpreter.isAnyReady(playerNames) && !getLocalPlayer().isReady()) {
            try {
                clientController.getJSON("playerData.json");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stopped polling for updates");
        try {
            Thread.sleep(200); //Just trying to avoid the data race
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startTimer();
    }

    /**
     * Returns true if all goes as planned. If all future calls of the function needs to be cancelled, as in the case
     * of falling into a pit or off the map, the function returns false.
     **/
    public boolean moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            if (space == null) {
                System.out.println("Bug #115");
                ; //TODO: Fix this. Bug #115. //UPDATE: Doesn't seem to occur anymore, but im not sure why
            }
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading, false);
            try {
                return (moveToSpace(player, target, heading));
            } catch (ImpossibleMoveException e) {
                System.out.println("Impossible move caught");
                return false;
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
        boolean isWall1 = originalPlayer.getSpace().getWalls().contains(heading);//Checks whether theres a wall in the way on the start field
        boolean isWall2 = false;
        if (!OGTargetIsNull) {
            Heading next = heading.next().next();
            System.out.println(next);
            isWall2 = originalTarget.getWalls().contains(heading.next().next());
        } //Checks whether there's a wall on the destination field, facing the start field

        if (isWall1 || isWall2) {
            System.out.println(originalPlayer.getName() + " hit a wall");
            return false;
        }

        if (OGTargetIsNull) {
            pit.doAction(this, originalPlayer);
            Player nextPlayer = getNextPlayer();
            board.setCurrentPlayer(nextPlayer);//TODO: This shouldn't be done here. But falling into a pit breaks if it isnt, for some reason
            return false;
        }
        boolean otherPlayerMoved = true;
        assert board.getNeighbour(originalPlayer.getSpace(), heading, true) == originalTarget; // make sure the move to here is possible in principle. This just throws an assertionException if exceptions are enabled, which isn't super useful
        Player other = originalTarget.getPlayer();
        if (other != null) { //If a player needs to be pushed
            if (originalPlayer.hasCard("Virus Module")) {
                cardController.drawVirusCardToDiscardPile(other);
            }
            if (originalPlayer.hasCard("Trojan Needler")) {
                cardController.drawTrojanCardToDiscardPile(other);
            }
            if (originalPlayer.hasCard("Blue Screen of Death")) {
                cardController.drawWormCardToDiscardPile(other);
            }

            Space newTarget = board.getNeighbour(originalTarget, heading, false);
            otherPlayerMoved = (moveToSpace(other, newTarget, heading));
        }

        for (FieldAction fieldAction : originalTarget.getActions()) {
            if (fieldAction instanceof Pit) { //It seems that we do indeed check for pits twice
                ((Pit) fieldAction).doAction(this, originalPlayer);
                break;
            }
        }
        if (otherPlayerMoved) {
            originalPlayer.setSpace(originalTarget);// I don't understand this.... Lucas? - Crazy
        }
        return true;
    }

    public void moveCurrentPlayerToSpace(Space space) {

        if (space.getPlayer() != null) return;

        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setSpace(space);

        Player nextPlayer = getNextPlayer();
        board.setCurrentPlayer(nextPlayer);
        if (!space.getItems().isEmpty()) {
            for (Item item : space.getItems()) {
                System.out.println(item.getName());
            }
        }
    }

    //TODO: int amount should always be 1, so it could be removed and the code simplified
    public void moveInDirection(@NotNull Player player, int amount, @NotNull Heading heading) { //Ideally this should return a boolean
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
                if (moveToSpace(player, nextSpace, heading)) {
                    System.out.println("Player moved one space");
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
    public Player getNextPlayer() {

        Space priorityAntenna = board.getPriorityAntennaSpace();
        Player closestPlayerToAntenna = null;

        int usedCards = Integer.MAX_VALUE;
        for (Player player : board.getAllPlayers()) {
            if (player.getUsedCards() < usedCards) { //Finds minimum value
                usedCards = player.getUsedCards();
            }
        }
        ArrayList<Player> possiblePlayers = new ArrayList<>();
        for (Player player : board.getAllPlayers()) {
            if (player.getUsedCards() == usedCards) {
                possiblePlayers.add(player);
            }
        }

        double closest = Double.MAX_VALUE;

        double closeness;
        for (Player player : possiblePlayers) {//Determines the closest of the eligible players
            int playerX;
            int playerY;
            if (player.getSpace() == null) {
                playerX = Integer.MAX_VALUE; //TODO: This is not a very pretty solution, but it somewhat fixes the issue by simply not all
                playerY = Integer.MAX_VALUE;
            } else {
                playerX = player.getSpace().getPosition()[0];
                playerY = player.getSpace().getPosition()[1];
            }

            closeness = distanceToSpace(priorityAntenna, playerX, playerY);
            if (closeness < closest) {
                closest = closeness;
                closestPlayerToAntenna = player;
            }
        }
        assert closestPlayerToAntenna != null;
        //System.out.println("closest player to antenna: " + closestPlayerToAntenna.getName());
        //return board.getPlayer(board.getPlayerNumber(closestPlayerToAntenna));
        return closestPlayerToAntenna;

    }

    /**
     * Calculates the distance between two spaces
     * @return distance, as a double, calculated with the pythagorean theorem
     */
    public double distanceToSpace(Space space, Space space2) {
        int x = space.getPosition()[0];
        int y = space.getPosition()[1];
        int otherX = space2.getPosition()[0];
        int otherY = space2.getPosition()[1];
        int dx = x - otherX;
        int dy = y - otherY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double distanceToSpace(Space space, int otherX, int otherY) {
        int x = space.getPosition()[0];
        int y = space.getPosition()[1];
        int dx = x - otherX;
        int dy = y - otherY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Sets the phase. If the phase is programming, cards are automatically drawn from drawpile to hand
     * @param phase
     */
    void setPhase(Phase phase) {
        if (phase == PROGRAMMING) {
            if (online) {
                cardController.drawCards(localPlayer);
            } else {
                for (Player player : board.getAllPlayers()) {
                    cardController.drawCards(player);
                }
            }
        }
        board.setPhase(phase);
    }


    public void startTimer() {

        timer = new Timer(true);
        board.setTimerIsRunning(true);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (online) {
                        clientController.getJSON("playerData.json");
                    }
                    if (jsonInterpreter.isAllReady() || (!online && stopTimerBeforeTime.get())) {
                        board.setTimerSecondsCount(0);
                        timer.cancel();
                        timer.purge();
                        board.setTimerIsRunning(false);
                        System.out.println("Timer stopped prematurely");
                        countDownLatch.countDown();
                        return;
                    }
                } catch (NullPointerException e) {
                    //We ignore this. Just happens if the localplayer is null,
                    // meaning its offline.
                }

                board.setTimerSecondsCount(board.getTimerSecondsCount() + 1);
                System.out.println("timer: " + board.getTimerSecondsCount());
                if (board.getTimerSecondsCount() >= 30) {
                    timer.cancel();
                    timer.purge();
                    board.setTimerIsRunning(false);
                    board.setTimerSecondsCount(0);
                    System.out.println("Time to fire event!");
                    countDownLatch.countDown();
                }
            }
        }, 0, 1000);
        board.setTimerSecondsCount(0);

        Thread threadTimerDone = new Thread(() -> {
            try {
                countDownLatch.await();
                if (online) localPlayer.setReady(true);
                jsonPlayerBuilder.updateDynamicPlayerData();
                clientController.updateJSON("playerData.json");
                try {
                    Thread.sleep(1500); //We need to wait long enough for the data to reach the server and get back
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                setPhase(ACTIVATION);
                if (online) {
                    cardController.fillPlayersProgramFromHandOnline(localPlayer);
                } else {
                    cardController.fillAllPlayersProgramFromHand(board);
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Something very bad with the timer implementation happened");
                e.printStackTrace();
            }
            for (Player player : board.getAllPlayers()) {
                player.setReady(true);
            }
            finishProgrammingPhase();

        });
        threadTimerDone.setDaemon(true);
        threadTimerDone.setPriority(10);
        threadTimerDone.start();
        System.out.println("threadtimerDone started");
    }

    public synchronized void synchronize() {


        System.out.println("______________SYNC_______________");
        setPhase(SYNCHRONIZATION);


        localPlayer.setReady(true);
        jsonPlayerBuilder.updateDynamicPlayerData();
        clientController.updateJSON("playerData.json");
        clientController.getJSON("playerData.json");


        int count = 0;
        for (Card card : localPlayer.currentProgram()) {
            if (card instanceof DamageCard) {
                while (card instanceof DamageCard) {
                    card = localPlayer.drawCardFromPile();
                }
                localPlayer.getProgram().get(count).setCard(card);
            }
            count += 1;
        }


        if (firstRound) {
            cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
            clientController.createJSON("cardSequenceRequest.json");
            clientController.getJSON("cardSequenceRequest.json");
            firstRound = false;
        } else {
            cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
            clientController.updateJSON("cardSequenceRequest.json");

            for (int i = 0; i < 3; i++) {
                clientController.getJSON("cardSequenceRequest.json");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        int getReadyTries = 0;
        clientController.updateJSON("playerData.json"); //Makes sure we have the newest json
        while (!jsonInterpreter.isAllReady()) {
            try {
                clientController.updateJSON("playerData.json");
                clientController.getJSON("playerData.json");
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


        //cardController.fillProgramFromHand(localPlayer);

        cardController.getCardLoader().sendCardSequenceRequest(localPlayer.currentProgramProgrammingCards(), localPlayer.getName());
        clientController.updateJSON("cardSequenceRequest.json");
        clientController.getJSON("cardSequenceRequest.json");
        for (Player player : board.getAllPlayers()) {
            System.out.println(player.getName());
            if (player != localPlayer) {
                //cardController.emptyProgram(player); this shouldnt fuck anything up but I dont like it either way
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    //
                }

                while (!jsonInterpreter.checkReceivedCardSequence(player.getName())) {
                    //clientController.updateJSON("cardSequenceRequest.json"); Json has already been updated by the player further up in this function
                    clientController.getJSON("cardSequenceRequest.json");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<ProgrammingCard> cards = cardController.getCardLoader().loadCardSequence(player.getName()); //This one returns a list that is too long

                int counter = 0;
                for (CommandCardField field : player.getProgram()) {
                    field.setCard(cards.get(counter));
                    counter += 1;
                }
            }
        }
    }

    /**
     * Only to be used for testing, not final release
     */
    public void timerButtonPressed() {
        startTimer();
    }

    public void intermediateFunction() {
        stopTimerBeforeTime.set(true); //Used for offline gameplay
        if (online) {
            localPlayer.setReady(true);
            jsonPlayerBuilder.updateDynamicPlayerData();
            clientController.updateJSON("playerData.json");
        } else {
            finishProgrammingPhase();
        }
    }

    public void finishProgrammingPhase() {

        if (online) {
            localPlayer.setReady(true); //These shouldn't be necessary at all, but better to do one too many than one too few
            jsonPlayerBuilder.updateDynamicPlayerData();
            clientController.updateJSON("playerData.json");
            synchronize();
            localPlayer.setReady(false);
            jsonPlayerBuilder.updateDynamicPlayerData();
            clientController.updateJSON("playerData.json");
        }


        System.out.println("___________________FINISH PROGRAMMING___________________");
        setPhase(Phase.ACTIVATION);

        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Player currentPlayer = null;
                currentPlayer = getNextPlayer();
                board.setCurrentPlayer(currentPlayer);
                int sleep = 5000; //I'd like to make this dynamically decrease, so that plays accelerate. Not done for now though TODO: This should probably be upped in final release, but is kept low for testing
                while (true) {
                    try {
                        Thread.sleep(sleep);
                        Card card = currentPlayer.currentProgram().get(currentPlayer.getUsedCards());

                        System.out.println("\nCurrent player is " + board.getCurrentPlayer().getName() + ", they play " + card.getName() + " which is at slot number " + (currentPlayer.getUsedCards() + 1));
                        card.getAction().doAction(GameController.this, currentPlayer, card); //I hate this implementation
                        List<FieldAction> fieldActions = currentPlayer.getSpace().getActions();
                        for (FieldAction fieldAction : fieldActions) {
                            Thread.sleep(2000); //Generify?
                            fieldAction.doAction(GameController.this, currentPlayer.getSpace());
                        }
                        List<Item> items = currentPlayer.getSpace().getItems();
                        for (Item item : items) {
                            Thread.sleep(2000);
                            item.getEvent().doAction(GameController.this, currentPlayer.getSpace());
                        }
                        Thread.sleep(500);

                    } catch (NullPointerException e) {
                        System.out.println("Error: No more commandCards");
                    } catch (InterruptedException e) {
                        //This is just here for the sleep. Shouldn't really happen
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Trying to get a card that was removed from the hand");
                    }
                    currentPlayer.incrementUsedCards();
                    currentPlayer = getNextPlayer();
                    board.setCurrentPlayer(currentPlayer);

                    boolean toBreak = true;
                    for (Player player : board.getAllPlayers()) {
                        if (player.getUsedCards() < Player.NO_REGISTERS && !(player.currentProgram().size() == 0)) {
                            toBreak = false;
                        }
                    }
                    if (toBreak) {
                        break;
                    }
                }

                for (Player player : board.getAllPlayers()) {
                    player.resetUsedCards();
                    cardController.emptyProgram(player);

                    if (!MoreAdvancedGame) {
                        cardController.clearhand(player);
                    }
                }

                if (online) {
                    localPlayer.setReady(false);
                    jsonPlayerBuilder.updateDynamicPlayerData();
                    clientController.updateJSON("playerData.json");
                    clientController.getJSON("playerData.json");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1500); //Stopping the data race wherever I go
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                setPhase(Phase.PROGRAMMING);
                if (online) {
                    getUpdates(playerNames);
                }

            }

        });
        commandThread.setDaemon(true);
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

                    if (card instanceof ProgrammingCard) {
                        ProgrammingCard commandCard = (ProgrammingCard) card;
                        try {
                            commandCard.getAction().doAction(GameController.this, board.getCurrentPlayer(), commandCard); //I hate this implementation
                            Thread.sleep(420);
                        } catch (NullPointerException e) {
                            System.out.println("Error: No more commandCards");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Something needs to be done about this card");
                    }
                }
            }
        });
        commandThread.setDaemon(true);
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
     * A method for future use with some buttons. Does nothing in current verison of RoboRally
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method to be used by a handler
        //     is not yet implemented
    }

    /**
     * Run when the player wins
     * @param currentPlayer winner
     */
    public void win(Player currentPlayer) {
        roboRally.winScreen(currentPlayer);
    }

    /**
     * Retrieves the next permanent upgrade card from the upgrade shop
     */
    public void nextPermanentUpgradeCard() {
        board.getUpgradeShop().nextPermanentCard();
    }

    /**
     * Retrieves the next permanent upgrade card from the upgrade shop
     */
    public void nextTemporaryUpgradeCard() {
        board.getUpgradeShop().nextTemporaryCard();
    }

    public void purchaseTemporaryUpgradeCard(Player player) {
        TempUpgradeCard card = board.getUpgradeShop().getSelectedTemporaryCard();
        if (player.getEnergyCubes() >= card.getCost() && player.getAmountAllUpgradeCards() >= 3) {
            board.getUpgradeShop().removeTemporaryUpgradeCard(card);
            player.addTemporaryUpgradeCard(card);
            player.addEnergyCubes(-card.getCost());
        }
    }

    public void purchasePermanentUpgradeCard(Player player) {
        UpgradeCard card = board.getUpgradeShop().getSelectedPermanentCard();
        if (player.getEnergyCubes() >= card.getCost() && player.getAmountAllUpgradeCards() <= 3) {
            board.getUpgradeShop().removePermanentUpgradeCard(card);
            player.addPermanentUpgradeCard(card);
            player.addEnergyCubes(-card.getCost());
        }
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


    // Note: Anti-spam - You can't send the same message twice in a row!
    public void sendMessage(String message) {
        localPlayer.setMessage(message);
        jsonPlayerBuilder.updateDynamicPlayerData();
        clientController.updateJSON("playerData.json");
    }

    // Makes cards movable from one slot to another.
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        Card sourceCard = source.getCard();
        Card targetCard = target.getCard();
        if (sourceCard != null & targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            System.out.print("Current Program status: ");
            for (Card card : board.getCurrentPlayer().currentProgram()){
                if (card != null) System.out.print(card.getName() + ", ");
                else System.out.print("[empty], ");
            }
            System.out.println("\n");
            return true;
        } else {
            return false;
        }
    }

    public synchronized Player getLocalPlayer() {
        if (this.localPlayer != null) {  //This should be simplified, but its hilarious
            return this.localPlayer;
        } else return null;
    }

    public boolean getOnline() {
        return online;
    }

    public ClientController getClientController() {
        return this.clientController;
    }
}