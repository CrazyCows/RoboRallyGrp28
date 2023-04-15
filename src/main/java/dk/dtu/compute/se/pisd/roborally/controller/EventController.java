package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import static dk.dtu.compute.se.pisd.roborally.model.Command.LEFT;
import static dk.dtu.compute.se.pisd.roborally.model.Command.RIGHT;

public class EventController {

    private GameController gameController;

    public EventController(GameController gameController) {
        this.gameController = gameController;
    }


    public void doAction(Player player, Command command) {
        switch (command) {
            case MOVEONE -> {
                gameController.moveForward(player);
            }
            case MOVETWO -> {
                for (int i = 0; i < 2; i++) {
                    gameController.moveForward(player);
                }
            }
            case MOVETHREE -> {
                for (int i = 0; i < 3; i++) {
                    gameController.moveForward(player);
                }
            }
            case RIGHT -> {
                gameController.turnPlayer(player, RIGHT);
            }
            case LEFT -> {
                gameController.turnPlayer(player, LEFT);
            }
            case OPTIONLEFTRIGHT -> {
                System.out.println("OPTIIONLEFTRIGHT - Not yet implemented");
            }
            case UTURN -> {
                for (int i = 0; i < 2; i++) {
                    gameController.turnPlayer(player, LEFT);
                }
            }
            case BACKUP -> {
                gameController.moveInDirection(player, -1, player.getHeading());
            }
            case POWERUP -> {
                System.out.println("POWERUP - Not yet implemented");
            }
            case AGAIN -> {
                System.out.println("AGAIN - Not yet implemented");
            }
        }
    }


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
