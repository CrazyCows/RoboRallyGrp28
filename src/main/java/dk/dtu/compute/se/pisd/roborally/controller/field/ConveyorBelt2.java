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
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space){
        System.out.println("CONVEYOR");
        Player player = space.getPlayer();
        gameController.moveInDirection(player, 1, heading);
        Space newSpace = gameController.board.getNeighbour(space,heading,false); //We dont check for walls, as this is already getting done in moveInDirection.
        for (FieldAction fieldAction : newSpace.getActions()){
            if (fieldAction instanceof ConveyorBelt2){
                gameController.moveInDirection(player, 1, heading);
                break; //break theoretically speeds up code, but mostly makes it nicer to run through in debugger
            }
        }
        return true;
    }

}
