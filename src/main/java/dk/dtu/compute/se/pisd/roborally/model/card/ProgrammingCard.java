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
package dk.dtu.compute.se.pisd.roborally.model.card;

import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;
import dk.dtu.compute.se.pisd.roborally.model.Command;

import java.lang.reflect.InvocationTargetException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ProgrammingCard extends Card {

    //final public Command command;

    CardAction<ProgrammingCard> action;
    String commandName;
    Command command;

    public ProgrammingCard(String name, String imagePath, String actionClassName, String commandName) {
        this.name = name;
        this.imagePath = imagePath;
        this.actionClassName = actionClassName;
        this.commandName = commandName;
    }

    @SuppressWarnings("unchecked")
    public void createAction() {
        try {
            Class<?> eventClass = Class.forName(this.actionClassName);
            this.action = (CardAction<ProgrammingCard>) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.action = null;
        }
    }

    public void createCommand() {
        this.command = Command.valueOf(commandName.toUpperCase());
    }


    public void setAction(CardAction<ProgrammingCard> action) {
        this.action = action;
    }

    public CardAction<ProgrammingCard> getAction() {
        return this.action;
    }
    public Command getCommand() {
        return this.command;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getActionClassName() {
        return this.actionClassName;
    }

}
