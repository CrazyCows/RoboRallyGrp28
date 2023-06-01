package dk.dtu.compute.se.pisd.roborally.controller.item;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;


public class Checkpoint extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }


    /**
     *
     * See the abstract class, 'FieldAction' for more information.
     *
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return boolean
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        String s = "12";
        for (Item item : space.getItems()){
            if (item.getName().equals("checkpoint")){
                s = item.getImage();
                break;
            }
        }
        String clean = s.replaceAll("\\D+",""); //Uses regex to remove all non-numbers from string
        int number = Integer.parseInt(clean);

        System.out.println("THE CHECKPOINT");
        Board board = gameController.board;
        Player currentPlayer = space.getPlayer();
        if (currentPlayer.getCheckpointsCollected() == number){
            currentPlayer.iterateCheckpointsCollected();
            if (currentPlayer.getCheckpointsCollected() == board.getNumberOfCheckpoints()){
                gameController.win(currentPlayer);
            }
        }
        return true;
    }

}



