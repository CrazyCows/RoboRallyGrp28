package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class Checkpoint extends FieldAction {
    private Heading heading;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }





    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        System.out.println("THE CHECKPOINT");
        Board board = gameController.board;
        System.out.println(space.getItem() + " <<-------------");
        space.setItem(null);
        Random rand = new Random();
        int maxHeight = rand.nextInt(board.height);
        int maxWidth = rand.nextInt(board.width);
        space = board.getSpace(maxWidth, maxHeight);
        space.setItem("checkpoint");

        return false;
    }



}



