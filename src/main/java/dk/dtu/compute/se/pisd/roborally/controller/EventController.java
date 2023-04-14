package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class EventController {





    public void moveSpaces(Player player, Board board) {
        int amount = 2;
        Space space = player.getSpace();
        int[] spacePosition = space.getPosition();
        for (int i = 0; i < amount; i++) {
            switch (player.getHeading()) {
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
        }
        player.setSpace(board.getSpace(spacePosition[0], spacePosition[1]));
    }

    public void moveToSpace(Player player) {

    }

    public void turnPlayer(Player player) {

    }

    public void powerDown(Player player) {

    }

    public void repair(Player player) {

    }

    public void fireLaser(Player player, Space space) {

    }

}
