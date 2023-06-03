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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public abstract class FieldAction {



    public void backgroundAnimationThread(Space space, List<String> background) {

        Thread thread = new Thread(() -> {
            for (int i = 1; i < background.size(); i++) {
                System.out.println(background.get(i));
                try {
                    space.animate(new ArrayList<>(Arrays.asList(background.get(0), background.get(i))));
                    Thread.sleep(1000); // Delay for half a second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            space.animate(background);
        });
        thread.start();
    }


    /**
     *
     * This is the holy grail of methods.
     * 'doAction' makes it possible for an item, space or card to perform an action.
     * By using this implementation, any 'doAction' will be run, when a given object
     * uses its doAction - and it will find out by itself, which doAction to use (simplified)
     *
     * Please note, that command cards that determines movement uses the CommandCardController.
     *
     *
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return whether the action was successfully executed
     */
    public abstract boolean doAction(GameController gameController, Space space);

}
