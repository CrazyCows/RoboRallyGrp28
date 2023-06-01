package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.ProgrammingCard;
import dk.dtu.compute.se.pisd.roborally.model.TempUpgradeCard;

import static dk.dtu.compute.se.pisd.roborally.model.Command.LEFT;
import static dk.dtu.compute.se.pisd.roborally.model.Command.RIGHT;

public class ProgrammingAction extends CardAction<ProgrammingCard> {

    @Override
    public boolean doAction(GameController gameController, Player player, ProgrammingCard card) {
        // Implement the action specific to UpgradeCard
        // Use the gameController and card parameters as needed

        switch (card.getCommand()) {
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
                gameController.turnPlayer(player, RIGHT);
                gameController.turnPlayer(player, RIGHT);
                gameController.moveForward(player);
                gameController.turnPlayer(player, RIGHT);
                gameController.turnPlayer(player, RIGHT);
            }
            case POWERUP -> {
                System.out.println("POWERUP - Not yet implemented");
            }
            case AGAIN -> {
                System.out.println("AGAIN - Not yet implemented");
            }
        }
        return true; // Return a boolean result indicating the success/failure of the action
    }


    public void moveToSpace(Player player) {

    }

    public void turnPlayer(Player player) {

    }

    public void powerDown(Player player) {

    }

    public void repair(Player player) {

    }

    /*public void fireLaser(Player player, Space space) {

    }*/

}
