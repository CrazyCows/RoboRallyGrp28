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
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.item.LaserGun;
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

    private Timer timer;
    private int timerSecondsCount;
    private boolean timerIsRunning;
    private Space priorityAntennaSpace;

    private int numberOfCheckpoints = 0;

    private ArrayList<Space> laserSpaces = null;

    public Board(int width, int height) {
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

    public List<Player> getAllPlayers() {
        return players;
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }
        Heading reverse = Heading.values()[(heading.ordinal() + 2)% Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }


    // TODO: I forgot why this is places here??? (but it works though)
    public void startTimer() {
        timer = new Timer();
        timerIsRunning = true;
        notifyChange();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerSecondsCount += 1;
                System.out.println("timer: " + timerSecondsCount);
                if (timerSecondsCount % 6 == 0) {
                    notifyChange();
                }
                if (timerSecondsCount >= 30) {
                    timer.cancel();
                    timer.purge();
                    timerIsRunning = false;
                    notifyChange();
                    timerSecondsCount = 0;
                    System.out.println("Time to fire event!");
                }
            }
        }, 0, 1000);
    }

    public int getTimerSecondsCount() {
        return this.timerSecondsCount;
    }

    public boolean getTimerIsRunning() {
        return this.timerIsRunning;
    }

    public String getStatusMessage() {
        return "temp STATUS MESSAGE 0";
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

    public ArrayList<Space> getLaserSpaces(){ //Distinguish between types of lasers
        if (laserSpaces == null){
            for (int x = 0; x < width; x++) {
                for(int y = 0; y < height; y++) {
                    for (FieldAction fieldAction : spaces[x][y].getActions()){
                        if (fieldAction instanceof LaserGun){
                            laserSpaces.add(spaces[x][y]);
                        }
                    }
                }
            }
        }
        return laserSpaces;
    }
}
