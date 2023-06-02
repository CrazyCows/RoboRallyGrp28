package dk.dtu.compute.se.pisd.roborally.controller.item;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.field.Laserbeam;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.List;

public class LaserGun extends FieldAction {
    private Heading heading = Heading.EAST; //TODO: This value is temporary
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }
    List<Space> visited = new ArrayList<>();


    @Override //Maybe this code should be moved to the gamecontroller? Doesnt really make sense that a lasergun controls this
    public boolean doAction(GameController gameController, Space space) {

        return false;
    }


    //Some of this code should be moved to the laserbeam class
    public List<Space> shootLaser(Space space, Heading heading) { //Will break if there's a wall in the laser.
        System.out.println("Shooting laser on space [" + space.x + "," + space.y + "] in direction " + heading);
        List<Heading> walls = space.getWalls(); //All walls from space

        Item laser = new Item("laserbeam","checkpointhansi1.png", Heading.NORTH, new Laserbeam());
        space.addItem(laser);
        visited.add(space);

        for (Heading wall : walls){
            if (wall == heading.next().next()){ //If any wall is on the side of the laser
                return visited;
            }
        }
        Player player = space.getPlayer();
        if (player != null){
            player.addSpamCardToDiscardPile(); //To be repeated the amount of times that the laser is strong
            return visited;
        }

        //Check if we can continue. If theres a wall in the heading, we cant
        for (Heading wall : walls){
            if (wall == heading){
                return visited;
            }
        }
        Space nextSpace = space.board.getNeighbour(space,heading);
        shootLaser(nextSpace,heading);

        return visited;
    }
}
