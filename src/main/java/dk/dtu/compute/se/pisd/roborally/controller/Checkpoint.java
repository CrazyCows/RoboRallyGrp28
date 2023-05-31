package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Space;
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

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        System.out.println("THE CHECKPOINT");
        // TODO needs to be implemented
        Board board = gameController.board;
        BoardView boardView;
        SpaceView spaceView;

        Space newSpace = board.getSpace(7, 3);
        List<Item> itemsToMove  = new ArrayList<>(space.getItems());

        // Iterate over the items in the space
        for (Item item : itemsToMove) {
            if (item.getName().equals("checkpoint")) {
                // Move the item to the new space
                newSpace.addItem(item);
                space.removeItem(item); // Add the item to the removal list
            }
        }

        return true;
    }



}



