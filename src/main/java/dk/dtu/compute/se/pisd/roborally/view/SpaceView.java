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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.field.EnergySpace;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ImageLoader;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

/**
 * ...
 * The spaceView is tied together with every space on the board.
 * Here, there are two ImageViews that are important for the implementation
 * of the 'SpaceView' class: backgroundImageView and overlayImageView.
 * This is because, that every space can have a background and an image
 * on top of the background. This way, we have the ability to better
 * distinguish between what IS the 'board' and what is on top of it.
 * It is primarily used to make a space able to hold an item on top of it.
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 75;
    final public static int SPACE_WIDTH = 75;

    final private static String emptyEnergySpace = "navn.png";

    public final Space space;
    private ImageView backgroundImageView;
    private ImageView overlayImageView;
    private ImageLoader imageLoader = new ImageLoader();
    private String heading;
    private ImageView playerImageView;
    private ColorAdjust colorAdjust;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);


        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: black; -fx-border-color: black; -fx-border-width: 4px;");
        } else {
            this.setStyle("-fx-background-color: black; -fx-border-color: black; -fx-border-width: 4px;");
        }

        if (!this.space.getWalls().isEmpty()) {
            for (Heading heading : this.space.getWalls()) {
                if (heading == NORTH) {
                    System.out.println("wall");
                    this.setStyle("-fx-border-width: 4; -fx-border-color: red black black black;");
                }
                if (heading == EAST) {
                    this.setStyle("-fx-border-width: 4; -fx-border-color: black red black black;");
                    System.out.println("wall");
                }
                if (heading == SOUTH) {
                    this.setStyle("-fx-border-width: 4; -fx-border-color: black black red black;");
                    System.out.println("wall");
                }
                if (heading == WEST) {
                    this.setStyle("-fx-border-width: 4; -fx-border-color: black black black red;");
                    System.out.println("wall");
                }
            }
        }

        //this.imageView.setPreserveRatio(true);
        //this.imageView.fitHeightProperty().bind(this.heightProperty());
        //this.imageView.fitWidthProperty().bind(this.widthProperty());
        //this.getChildren().add(this.imageView);

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    public void setBackround(List<String> background) {
        // TODO: background is a list of ressource image strings
        // TODO: cycle through them for animations.
        String imagePath;
        if (background.size() != 0) {
            this.heading = background.get(0);
            imagePath = background.get(1);

            System.out.println(imagePath + "<<<");
            this.backgroundImageView = imageLoader.getImageView(imagePath);
            switch (this.heading) {
                case "NORTH":
                    this.backgroundImageView.setRotate(0);
                    break;
                case "EAST":
                    this.backgroundImageView.setRotate(90);
                    break;
                case "SOUTH":
                    this.backgroundImageView.setRotate(180);
                    break;
                case "WEST":
                    this.backgroundImageView.setRotate(270);
                    break;
            }
            this.backgroundImageView.setFitHeight(SPACE_HEIGHT-4);
            this.backgroundImageView.setFitWidth(SPACE_WIDTH-4);
            this.getChildren().add(this.backgroundImageView);
            updatePlayer();
        }
        else {
            imagePath = "test_field.png";
            this.backgroundImageView = imageLoader.getImageView(imagePath);
            this.backgroundImageView.setFitHeight(SPACE_HEIGHT-4);
            this.backgroundImageView.setFitWidth(SPACE_WIDTH-4);
            this.getChildren().add(this.backgroundImageView);
            updatePlayer();
        }
    }

    public void updateItem(String overlayImagePath) {
        this.overlayImageView = imageLoader.getImageView(overlayImagePath);
        this.overlayImageView.setFitHeight(SPACE_HEIGHT-4);
        this.overlayImageView.setFitWidth(SPACE_WIDTH-4);
        this.getChildren().add(this.overlayImageView);
    }

    public void updateOverlay(String overlayImagePath) {
        // Remove the existing overlay ImageView, if it exists
        this.getChildren().remove(overlayImageView);

        // Create a new ImageView for the overlay image
        this.overlayImageView = imageLoader.getImageView(overlayImagePath);
        overlayImageView.setFitHeight(SPACE_HEIGHT - 4);
        overlayImageView.setFitWidth(SPACE_WIDTH - 4);

        // Add the overlay ImageView to the SpaceView
        this.getChildren().add(overlayImageView);

        // Trigger a layout pass to update the view
        this.requestLayout();
    }

    public void removeOverlay() {
        // Remove the overlay ImageView, if it exists
        this.getChildren().remove(overlayImageView);

        // Trigger a layout pass to update the view
        this.requestLayout();
    }

    private void updatePlayer() {
        // Remove the existing player image, if it exists
        if (playerImageView != null) {
            this.getChildren().remove(playerImageView);
            playerImageView = null;
        }

        Player player = space.getPlayer();

        if (player != null) {
            // Create an ImageView with the player image
            playerImageView = new ImageView();
            Image playerImage = new Image("images/robots/" + player.getColor() + "_" + player.getHeading().toString().toLowerCase() + "_facing_robot.png");
            playerImageView.setFitWidth(SPACE_WIDTH);
            playerImageView.setFitHeight(SPACE_HEIGHT);
            playerImageView.setImage(playerImage);

            // Set the rotation of the player image
            //playerImageView.setRotate((90 * player.getHeading().ordinal()) % 360);    // old 1 image per robot code

            // Add the ImageView to the pane
            this.getChildren().add(playerImageView);
            playerImageView.toFront();
        }
        // Add the ImageView back again
    }   

    private boolean energyFieldUpdater(Subject subject){
        Space sp8z = (Space)subject;
        List<FieldAction> sp8zActions = sp8z.getActions();
        if (sp8zActions.size() > 0 && space.board.getPhase() == Phase.ACTIVATION){
            FieldAction fieldAction = sp8zActions.get(0);
            if (fieldAction instanceof EnergySpace && ((EnergySpace) fieldAction).getEnergyCubes() == 0){ //I DONT KNOW WHAT IM DOING
                ArrayList<String> h = new ArrayList();
                h.add(0,"NORTH"); h.add(1,emptyEnergySpace);
                space.setBackground(h);
                return true;
            }
        } //This might be the single most ghetto solution, but it currently seems to work. Should probably test a tiny bit more for the sake of Lucas sanity
        return false;
    }

    @Override
    public void updateView(Subject subject) {

        if (subject == this.space) {
            if (energyFieldUpdater(subject)){
                System.out.println("Updated an energy space image");
            }

            setBackround(space.getBackground());
            if (!space.getItems().isEmpty()) {
                updateOverlay(space.getItems().get(space.getItems().size() - 1).getImage());
            }
            else {
                if (overlayImageView != null) {
                    removeOverlay();
                }
            }
        }
        updatePlayer();
    }
}
