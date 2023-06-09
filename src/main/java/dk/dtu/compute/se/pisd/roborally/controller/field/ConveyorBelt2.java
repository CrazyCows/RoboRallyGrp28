/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBelt2 extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override //Issue: Player is pushed two away from conveyorbelt. This should only happen if the player is on a continuous conveyor
    public boolean doAction(@NotNull GameController gameController, @NotNull Space originalSpace){

        //MOVING FROM THE ORIGINAL SPACE TO THE FIRST SPACE
        System.out.println("CONVEYOR: Conveying robots and emotions alike");
        Player player = originalSpace.getPlayer();
        Heading originalHeading = this.heading;
        gameController.moveInDirection(player, 1, heading);
        Space firstDestinationSpace = player.getSpace();
        if (originalSpace == firstDestinationSpace){ //A bit unnecessary
            System.out.println("Player got conveyed into a wall");
            return false;
        }
        //Space firstDestinationSpace = gameController.board.getNeighbour(originalSpace,heading,false); //We dont check for walls, as this is already getting done in moveInDirection.
        Heading firstDestinationHeading = null;
        for (FieldAction fieldAction : firstDestinationSpace.getActions()){
            if (fieldAction instanceof ConveyorBelt2) {
                firstDestinationHeading = ((ConveyorBelt2) fieldAction).getHeading(); //Assumes theres ony
                break;
            }
        }

        //You may ask why prev() and next() are mixed like this.
        if (originalHeading == firstDestinationHeading.next()){ //turning one way
            player.setHeading(player.getHeading().prev());
        }
        if (originalHeading == firstDestinationHeading.prev()){//turning one way
            player.setHeading(player.getHeading().next());
        }
        if (originalHeading == firstDestinationHeading.next().next()){//180-degree turn. Rules don't specify what would happen in this case
            player.setHeading(player.getHeading().next().next());
        }


        //MOVING FROM THE FIRST SPACE TO THE SECOND

        gameController.moveInDirection(player, 1, firstDestinationHeading);
        Space secondDestinationSpace = player.getSpace();
        if (firstDestinationSpace == secondDestinationSpace){
            System.out.println("Player got conveyed into a wall");
            return false; //I think this works for if the player hits a wall
        }
        Heading secondDestinationHeading = null;
        for (FieldAction fieldAction : secondDestinationSpace.getActions()){
            if (fieldAction instanceof ConveyorBelt2) {
                secondDestinationHeading = ((ConveyorBelt2) fieldAction).getHeading();
                break;
            }
        }
        if (firstDestinationHeading == secondDestinationHeading.next()){ //turning one way
            player.setHeading(player.getHeading().prev());
        }
        if (firstDestinationHeading == secondDestinationHeading.prev()){//turning one way
            player.setHeading(player.getHeading().prev());
        }
        if (firstDestinationHeading == secondDestinationHeading.next().next()){//180-degree turn. Rules don't specify what would happen in this case
            player.setHeading(player.getHeading().next().next());
        }



        return true;
    }

}
