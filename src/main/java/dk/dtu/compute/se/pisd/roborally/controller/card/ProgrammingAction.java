package dk.dtu.compute.se.pisd.roborally.controller.card;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;

import static dk.dtu.compute.se.pisd.roborally.model.Command.*;

public class ProgrammingAction extends CardAction<ProgrammingCard> {

    /**
     * The ProgrammingAction controller class works much like the FieldController,
     * although it is not abstract. It makes it possible to execute a
     * command upon a given player. This is used for programming the robot,
     * and simply translates the command into action.
     */

    ProgrammingCard lastProgrammingCard = null;
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
                player.addEnergyCubes(1);
                System.out.println(player.getName() + " now has " + player.getEnergyCubes() + " energy cubes");
            }
            case AGAIN -> {
                if (lastProgrammingCard.getCommand() == AGAIN){
                    System.out.println("Dont recurse me forever daddy");
                    return false;
                }
                return doAction(gameController,player,lastProgrammingCard);
            }
        }
        lastProgrammingCard = card;
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
