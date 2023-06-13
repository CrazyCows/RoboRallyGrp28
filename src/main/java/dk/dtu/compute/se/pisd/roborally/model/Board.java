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
import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.field.RebootToken;
import javafx.application.Platform;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 * This is the model of the Board.
 * The board contains space objects of type 'Space'
 *
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;
    private int timerSecondsCount;
    private boolean timerIsRunning;
    private Space priorityAntennaSpace;
    private Space RebootTokenSpace;
    private UpgradeShop upgradeShop;

    private int numberOfCheckpoints = 0;
    private boolean online;

    private ArrayList<Space> laserSpaces = new ArrayList<>();

    public Board(int width, int height) {
        this.upgradeShop = new UpgradeShop();
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        if (priorityAntennaSpace == null) {
            System.out.println("ERROR: NO PRIORITY ANTENNA");
            //TODO: IDK why this error is here. PriorityAntenna still works somehow
        }
        this.stepMode = false;
        this.timerSecondsCount = 0;
        this.timerIsRunning = false;
    }

    public Space getPriorityAntennaSpace() {
        if (priorityAntennaSpace == null) {
            for (Space[] spaceRow : spaces) {
                for (Space space : spaceRow) {
                    if (!space.getItems().isEmpty()) {
                        if (space.getItems().get(0).getName().equals("priorityAntenna")) {
                            priorityAntennaSpace = space;
                        }
                    }
                }
            }
        }
        return priorityAntennaSpace;
    }

    public Space getRebootTokenSpace() {
        if (RebootTokenSpace == null) {
            for (Space[] spaceRow : spaces) {
                for (Space space : spaceRow) {
                    if (!space.getActions().isEmpty()) {
                        if (space.getActions().get(0).getClass() == RebootToken.class) {
                            RebootTokenSpace = space;
                        }
                    }
                }
            }
        }
        return RebootTokenSpace;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }
/*
    public void loadBoard(int boardNumber) {
        if (boardNumber == 1) {
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    spaces[i][j].setImage("test_field2.jpg");
                }
            }
        }
    }
 */
    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }


    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    public synchronized List<Player> getAllPlayers() {
        return players;
    }
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * If checkForWalls is true, then the function returns null in case it is not possible to pass (because of a wall)
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour comapred to the original field
     * @param checkForWalls should there be checked for walls?
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading, boolean checkForWalls) {
        /*if (space.getWalls().contains(heading)) {
            return null;
        }*/
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.
        //      "It might not need that at all" -Tsun Tsu, the art of war (Anton)

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1);
                break;
            case WEST:
                x = (x - 1);
                break;
            case NORTH:
                y = (y - 1);
                break;
            case EAST:
                x = (x + 1);
                break;
        }
        Board board = space.board;
        if (y < 0 || y > board.height) {
            return null;
        }
        else if (x < 0 || x > board.width) {
            return null;
        }
        Space result = getSpace(x, y);
        if (checkForWalls){
            Heading reverse = Heading.values()[(heading.ordinal() + 2)% Heading.values().length]; //Think you can just use heading.next.next here
            if (result != null) {
                if (result.getWalls().contains(reverse)) {
                    return null;
                }
            }
        }
        return result;
    }



    public int getTimerSecondsCount() {
        return this.timerSecondsCount;
    }

    public void setTimerSecondsCount(int seconds) {
        this.timerSecondsCount = seconds;
        Platform.runLater(this::notifyChange);
    }

    public void setTimerIsRunning(boolean status) {
        this.timerIsRunning = status;
        this.notifyChange();
    }

    public boolean getTimerIsRunning() {
        return this.timerIsRunning;
    }

    public String getStatusMessage() {
        return ("STATUS: "+phase);
    }

    public int getNumberOfItemsOnBoard(String spaceName) {
        if (numberOfCheckpoints == 0){
            int counter = 0;
            for (int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    for (Item item : spaces[x][y].getItems()){
                        if (Objects.equals(item.getName(), spaceName)){
                            counter += 1;
                        }
                    }
                }
            }
            numberOfCheckpoints = counter;
        }
        return numberOfCheckpoints;
    }

    public UpgradeShop getUpgradeShop() {
        return this.upgradeShop;
    }

    public ArrayList<Space> getLaserSpaces(){ //Distinguish between types of lasers
        if (laserSpaces.size() == 0){
            for (int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    for (Item item : spaces[x][y].getItems()){
                        if (Objects.equals(item.getName(),"laserGun")){
                            laserSpaces.add(spaces[x][y]);
                        }
                        System.out.println(item.getName());
                        System.out.println(x + "," + y);
                    }
                }
            }
        }
        return laserSpaces;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean getOnline() {
        return online;
    }
}
