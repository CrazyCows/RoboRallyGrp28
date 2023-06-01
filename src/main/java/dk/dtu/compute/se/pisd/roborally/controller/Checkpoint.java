package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Checkpoint extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    private int number = 0;


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
        System.out.println("THE CHECKPOINT");
        // TODO needs to be implemented
        Board board = gameController.board;
        Player currentPlayer = space.getPlayer();
        if (currentPlayer.getCheckpointsCollected() == number){
            currentPlayer.iterateCheckpointsCollected();
        }






        //OLD CODE BELOW
        BoardView boardView;
        SpaceView spaceView;
        List<Item> itemsToMove  = new ArrayList<>(space.getItems());
        // Iterate over the items in the space
        for (Item item : itemsToMove) {
            if (item.getName().equals("checkpoint")) {

                //if (currentPlayer.getCheckpointsCollected() == )
            }
        }

        return true;
    }

}



