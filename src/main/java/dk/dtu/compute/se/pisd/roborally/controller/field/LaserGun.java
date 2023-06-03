package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.item.Laserbeam;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LaserGun extends FieldAction {
    private Heading heading;
    private String name;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }


    @Override //Maybe this code should be moved to the gamecontroller? Doesnt really make sense that a lasergun controls this
    public boolean doAction(GameController gameController, Space space) {

        System.out.println("FIRING LASER");

        backgroundAnimationThread(space, space.getBackground());

        /*Queue<Space> visited = new LinkedList<>();
        ArrayList<Space> spaces = space.board.getLaserSpaces();
        for (Space s : spaces){ //This assumes there's only one action per space, which is a laser
            System.out.println("Firing laser");
            LaserGun lg = (LaserGun) s.getItems().get(0).getEvent(); //I think this works?
            visited.addAll(lg.shootLaser(s, s.getItems().get(0).getHeading())); //Runs shootLaser, which returns every field that has been passed by a laser
        }
        System.out.println(visited);

        shootLaser(gameController, space, heading);
        return true;*/
        return true;
    }


    //Some of this code should be moved to the laserbeam class
    public List<Space> shootLaser(GameController gameController, Space space, Heading heading) { //Will break if there's a wall in the laser.
        /*if (space == null){
            return visited;
        }

        List<Heading> walls = space.getWalls(); //All walls from space

        Item laser = new Item("laserbeam","checkpointhansi1.png", heading, new Laserbeam());
        System.out.println("Shooting laser on space [" + space.x + "," + space.y + "] in direction " + heading);
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
        if (nextSpace == null){ //this doesnt do anything afaik
            return visited;
        }
        shootLaser(nextSpace,heading);

        return visited;*/
        return null;
    }

    public void setup(Space space) {
        space.getWalls().add(heading.prev().prev());
        Space nextSpace = space.board.getNeighbour(space, heading);
        if (nextSpace != null) {
            if (!nextSpace.hasItemName("Laser Beam")) {
                while (nextSpace.getActions().stream().noneMatch(action -> action.getClass().equals(LaserGun.class))) {
                    nextSpace.addItem(new Item("Laser Beam", "laserBeam.png", heading, new Laserbeam()));
                    nextSpace = nextSpace.board.getNeighbour(nextSpace, heading);
                    if (nextSpace == null) {
                        break;
                    }
                    else if (!nextSpace.hasItemName("Laser Beam")) {
                        break;
                    }
                }
            }
        }
    }
}
