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
     * See the abstract class, 'FieldAction' for more information.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return Returns true no matter what at the moment
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
        Player currentPlayer = space.getPlayer();
        System.out.print(currentPlayer.getName() + " has landed on checkpoint #" + (number+1));
        Board board = gameController.board;
        if (currentPlayer.getCheckpointsCollected() == number){
            currentPlayer.incrementCheckpointsCollected();
            System.out.println(". They collect the checkpoint, are one point closer to winning");
            if (currentPlayer.getCheckpointsCollected() == board.getNumberOfItemsOnBoard("checkpoint")){
                gameController.win(currentPlayer);
            }
        }else{
            System.out.println(". Sadly they're on the lookout for #" + (currentPlayer.getCheckpointsCollected()+1) + ", so they are no closer to winning");
        }
        return true;
    }
}



