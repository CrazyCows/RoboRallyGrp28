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

    //This is only temporary and should be added in the constructor
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
        Board board = gameController.board;
        Player currentPlayer = space.getPlayer();
        if (currentPlayer.getCheckpointsCollected() == number){
            currentPlayer.iterateCheckpointsCollected(); //We could also just currentplayer.checkpointscolled = number
            if (currentPlayer.getCheckpointsCollected() == board.getNumberOfCheckpoints()){
                gameController.win(currentPlayer);
            }
        }
        return true;
    }

}



