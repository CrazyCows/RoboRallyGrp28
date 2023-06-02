package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.List;

public class LaserGun extends FieldAction {
    private Heading heading;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {

        ArrayList<Space> spaces = space.board.getLaserSpaces();
        for (Space s : spaces){ //This assumes theres only one action, which is a laser
            System.out.println("Firing laser");
            shootLaser(s, ((LaserGun)(s.getActions().get(0))).getHeading());
        }

        return false;
    }


    public void shootLaser(Space space, Heading heading) { //Will break if theres a wall in the laser
        System.out.println("Shooting laser on space [" + space.x + "," + space.y + "]");
        List<Heading> walls = space.getWalls(); //All walls from space

        for (Heading wall : walls){
            if (wall == heading.next().next()){ //If any wall is on the side of the laser
                return;
            }
        }
        Player player = space.getPlayer();
        if (player != null){
            player.addSpamCardToDiscardPile(); //To be repeated the amount of times that the laser is strong
            return;
        }

        //Check if we can continue. If theres a wall in the heading, we cant
        for (Heading wall : walls){
            if (wall == heading){
                return;
            }
        }
        Item laser = new Item("laser","checkpointhansi1.png",Heading.NORTH,)
        //space.addItem(laser);
        Space nextSpace = space.board.getNeighbour(space,heading);
        shootLaser(nextSpace,heading);
    }
}
