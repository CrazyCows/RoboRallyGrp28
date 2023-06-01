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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 * This is the model of a space. Each space is contained within
 * an array of spaces in a Board. The space has different components
 * that define it, such as: walls, actions and items.
 *
 * A space itself can have zero-to-many actions, all while a space
 * can also have zero-to-many items - and these items can also have actions
 * Please note, that an 'item' is an object of type Item, and that is defined
 * as such.
 *
 */
public class Space extends Subject {

    private Player player;

    private ArrayList<Item> items = new ArrayList<>();
    private List<Heading> walls = new ArrayList<>();
    private List<FieldAction> actions = new ArrayList<>();
    private List<String> background = new ArrayList<>();

    public final Board board;

    // The coordinates on the board
    public final int x;
    public final int y;


    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
    }


    // removes the player from its old position
    // and places the player on this new space instead
    // TODO: THIS IS THE MODEL. SHOULD BE HANDLED BY CONTROLLER
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
        notifyChange();
    }

    // Removes all items on the space
    // from name of the item
    public void removeItem(String name) {
        this.items.removeIf(item -> item.name.equals(name));
        notifyChange();
    }

    public void removeItem(Item item) {
        this.items.remove(item);
        notifyChange();
    }


    public List<Heading> getWalls() {
        return walls;
    }

    public List<FieldAction> getActions() {
        return actions;
    }

    public List<String> getBackground() {
        return background;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public int[] getPosition() {
        return new int[] {this.x, this.y};
    }

}
