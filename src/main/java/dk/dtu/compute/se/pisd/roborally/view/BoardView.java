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
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ImageLoader;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BoardView extends VBox implements ViewObserver {

    private Board board;

    private GridPane mainBoardPane;
    private SpaceView[][] spaces;

    private PlayersView playersView;

    private Label statusLabel;

    private Timer timer;

    private SpaceEventHandler spaceEventHandler;
    int timerSecondsCount;

    public SpaceView[][] getSpaces(){
        return this.spaces;
    }


    public BoardView(@NotNull GameController gameController) {
        board = gameController.board;

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        ImageView imageView = new ImageView(new Image("checkpoint.png"));

        timerSecondsCount = 0;
        startTimer();

        // create a GridPane and add the nodes to it
        GridPane gridPane = new GridPane();
        gridPane.addRow(0, mainBoardPane, imageView);
        gridPane.addRow(1, playersView);
        gridPane.add(statusLabel, 0, 2, 2, 1); // span 2 columns and 1 row


        /*this.getChildren().add(mainBoardPane);
        this.getChildren().add(imageView);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);*/
        this.getChildren().add(gridPane);

        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaceView.setBackround(space.getBackground());
                spaces[x][y] = spaceView;

                //spaceView.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
                //spaceView.setPadding(new Insets(10));
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
                if (space.getItem() != null){
                    spaceView.addCheckpoint();
                }
            }
        }

        board.attach(this);
        update(board);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerSecondsCount += 1;
                System.out.println("counting: " + timerSecondsCount);
                if (timerSecondsCount >= 30) {
                    timer.cancel();
                    timer.purge();
                    System.out.println("Time to fire event!");
                }
            }
        }, 0, 1000);
    }


    @Override
    public void updateView(Subject subject) {
        System.out.println("Board update");
        if (subject == board) {
            Phase phase = board.getPhase();
            statusLabel.setText(board.getStatusMessage());
            }

    }

    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView) {
                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.moveCurrentPlayerToSpace(space);
                    event.consume();
                }
            }
        }

    }

}
