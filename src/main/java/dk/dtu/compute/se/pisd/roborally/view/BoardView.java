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
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

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

    private Image[] timers;
    private int nextTimerInt = 0;
    private Image currentTimerImage;
    ImageView timerView;
    GridPane gridPane;
    GridPane upgradeShop;
    StackPane stackPane;
    Rectangle mask;

    private SpaceEventHandler spaceEventHandler;
    private ArrowKeyEventHandler arrowKeyEventHandler;
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
        timers = new Image[7];

        timers[0] = new Image("hourglass0.png");
        timers[1] = new Image("hourglass1.png");
        timers[2] = new Image("hourglass2.png");
        timers[3] = new Image("hourglass3.png");
        timers[4] = new Image("hourglass4.png");
        timers[5] = new Image("hourglass5.png");
        timers[6] = new Image("hourglass6.png");
        timerView = new ImageView(timers[0]);

        Button timerButton = new Button("Start timer");
        timerButton.setOnAction( e -> board.startTimer());

        Button upgradeShopButton = new Button("Upgrade Shop");
        upgradeShopButton.setOnAction( e -> displayUpgradeShop());

        GridPane timerGridPane = new GridPane();
        timerGridPane.addRow(0, timerView);
        timerGridPane.addRow(1, timerButton);
        timerGridPane.addRow(2, upgradeShopButton);

        upgradeShop = new GridPane();
        upgradeShop.setVisible(false);

        Image upgradeShopImage = new Image("upgradeShopBackGround.png");
        ImageView upgradeShopImageView = new ImageView(upgradeShopImage);
        upgradeShopImageView.setFitWidth(board.width * 75);
        upgradeShopImageView.setFitHeight(board.height * 75);
        upgradeShop.getChildren().add(upgradeShopImageView);
        System.out.println(board.height * 75);
        System.out.println(board.width * 75);

        mask = new Rectangle(board.width * 75, board.height * 75);
        mask.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 0, 0, 0.69)),
                new Stop(0.18, Color.rgb(0, 0, 0, 0.69)),
                new Stop(0.19, Color.rgb(0, 0, 0, 1)),
                new Stop(0.82, Color.rgb(0, 0, 0, 1)),
                new Stop(0.83, Color.rgb(0, 0, 0, 0.69)),
                new Stop(1, Color.rgb(0, 0, 0, 0.69))));

        upgradeShop.setClip(mask);



        stackPane = new StackPane(mainBoardPane, upgradeShop);

        // create a GridPane and add the nodes to it
        gridPane = new GridPane();
        gridPane.addRow(0, stackPane, timerGridPane);
        gridPane.addRow(1, playersView);
        gridPane.add(statusLabel, 0, 2, 2, 1); // spans 2 columns and 1 row

        this.getChildren().add(gridPane);

        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);
        arrowKeyEventHandler = new ArrowKeyEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaceView.setBackround(space.getBackground());
                for (Item item : space.getItems()) {
                    spaceView.updateOverlay(item.getImage());
                }
                spaces[x][y] = spaceView;

                //spaceView.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
                //spaceView.setPadding(new Insets(10));
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        board.attach(this);
        update(board);
    }

    private void displayUpgradeShop() {
        System.out.println("upgrade shop is no longer displayed");
        if (upgradeShop.isVisible()) {
            upgradeShop.setVisible(false);
        }
        else {
            System.out.println("upgrade shop is displayed");
            upgradeShop.setVisible(true);
        }
    }

    private void nextTimer() {
        if (board.getTimerIsRunning()) {
            nextTimerInt += 1;
            if (timers.length < nextTimerInt - 1) {
                nextTimerInt = 0;
            }
            this.timerView.setImage(timers[nextTimerInt]);
        }
        else {
            nextTimerInt = 0;
            this.timerView.setImage(timers[0]);
        }
    }


    @Override
    public void updateView(Subject subject) {
        System.out.println("Board update");
        if (subject == board) {
            Phase phase = board.getPhase();
            statusLabel.setText(board.getStatusMessage());
            nextTimer();
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

    private class ArrowKeyEventHandler implements EventHandler<KeyEvent> {

        final public GameController gameController;

        public ArrowKeyEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(KeyEvent event) {
            System.out.println(event.getCharacter());
            event.consume();
        }
    }

}
