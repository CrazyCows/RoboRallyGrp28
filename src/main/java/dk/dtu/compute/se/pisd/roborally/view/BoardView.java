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
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.field.EnergySpace;
import dk.dtu.compute.se.pisd.roborally.controller.field.LaserGun;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.*;

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
    VBox upgradeShopVBox;
    Pane upgradeShopBlurBackground;
    StackPane stackPane;
    Rectangle mask;
    Button timerButton;
    Button upgradeShopButton;
    UpgradeShop upgradeShop;
    ImageView permUpgradeCardImage;
    ImageView tempUpgradeCardImage;
    Label energyAmount;
    Label priceTempLabel;
    Label pricePermLabel;

    private SpaceEventHandler spaceEventHandler;
    private ArrowKeyEventHandler arrowKeyEventHandler;
    private GameController gameController;
    int timerSecondsCount;

    public SpaceView[][] getSpaces(){
        return this.spaces;
    }


    public BoardView(@NotNull GameController gameController) {
        this.gameController = gameController;
        board = gameController.board;
        upgradeShop = board.getUpgradeShop();


        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");
        ImageView imageView = new ImageView(new Image("checkpoint.png"));

        timers = new Image[7];

        timers[0] = new Image("hourglass0.png");
        timers[1] = new Image("hourglass1.png");
        timers[2] = new Image("hourglass2.png");
        timers[3] = new Image("hourglass3.png");
        timers[4] = new Image("hourglass4.png");
        timers[5] = new Image("hourglass5.png");
        timers[6] = new Image("hourglass6.png");
        timerView = new ImageView(timers[0]);

        timerButton = new Button("Start timer");
        timerButton.setOnAction( e -> gameController.startTimer());

        upgradeShopButton = new Button("Upgrade Shop");
        upgradeShopButton.setOnAction( e -> {
            if (gameController.getLocalPlayer() != null) {
                setEnergyLabel(gameController.getLocalPlayer().getEnergyCubes());
            }
            else {
                setEnergyLabel(board.getCurrentPlayer().getEnergyCubes());
            }
            displayUpgradeShop();
        });

        GridPane timerGridPane = new GridPane();
        timerGridPane.addRow(0, timerView);
        timerGridPane.addRow(1, timerButton);
        timerGridPane.addRow(2, upgradeShopButton);

        setupUpgradeShop();

        stackPane = new StackPane(mainBoardPane, upgradeShopBlurBackground, this.upgradeShopVBox);

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

                for (Heading heading : space.getWalls()) {
                    boolean laserGun = false;
                    for (FieldAction fieldAction : space.getActions()) {
                        if (fieldAction instanceof LaserGun) {
                            laserGun = true;
                            break;
                        }
                    }
                    if (!laserGun) {
                        spaceView.updateOverlay("wall.png", heading.toString());
                    }
                }

                for (Item item : space.getItems()) {
                    spaceView.updateOverlay(item.getImage(), item.getHeading().toString());
                }
                spaces[x][y] = spaceView;

                //spaceView.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
                //spaceView.setPadding(new Insets(10));
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        board.attach(this);
        upgradeShop.attach(this);
        update(board);
        update(upgradeShop);
    }

    private void setEnergyLabel(int energyCubes) {
        energyAmount.setText(String.valueOf(energyCubes));
    }

    private void setupUpgradeShop() {

        upgradeShopBlurBackground = new Pane();
        upgradeShopBlurBackground.setStyle("-fx-background-color: rgba(128, 128, 128, 0.9);");
        upgradeShopBlurBackground.setVisible(false);

        upgradeShopVBox = new VBox();
        upgradeShopVBox.setFillWidth(true); // Allow the VBox to stretch horizontally

        upgradeShopVBox.setVisible(false);

        Text tempUpgradeText = new Text( "\n\n\n       TEMPORARY");
        tempUpgradeText.setFont(Font.font("System Bold", 16));
        tempUpgradeText.setTextAlignment(TextAlignment.CENTER);

        Text permUpgradeText = new Text("\n\n\n       PERMANENT");
        permUpgradeText.setFont(Font.font("System Bold", 16));
        permUpgradeText.setTextAlignment(TextAlignment.CENTER);


        ImageView upgradeCardImages = new ImageView();

        upgradeCardImages.setPreserveRatio(true);
        upgradeCardImages.setPickOnBounds(true);

        tempUpgradeCardImage = new ImageView(new Image(upgradeShop.getSelectedTemporaryCardImage()));
        tempUpgradeCardImage.setPreserveRatio(true);
        tempUpgradeCardImage.setPickOnBounds(true);
        tempUpgradeCardImage.setFitWidth(180);

        permUpgradeCardImage = new ImageView(new Image(upgradeShop.getSelectedPermanentCardImage()));
        permUpgradeCardImage.setPreserveRatio(true);
        permUpgradeCardImage.setPickOnBounds(true);
        permUpgradeCardImage.setFitWidth(180);

        Player player = board.getCurrentPlayer();

        Text playerName = new Text("\n" + player.getName());
        playerName.setFont(Font.font("System Bold",  16));
        playerName.setTextAlignment(TextAlignment.LEFT);


        Label energyLabel = new Label("\n\n\nEnergy Cubes: " + player.getEnergyCubes() + " ◈");
        energyLabel.setFont(Font.font("System Bold", 16));
        energyLabel.setTextAlignment(TextAlignment.LEFT);

        Label title = new Label("UPGRADE SHOP");
        title.setFont(Font.font("System Bold", 30));
        title.setTextAlignment(TextAlignment.CENTER);

        ImageView right_arrow = new ImageView(new Image("/images/right_arrow.png"));
        Button permButton = new Button();
        permButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        permButton.setGraphic(right_arrow);
        permButton.setOnAction(e -> gameController.nextPermanentUpgradeCard());


        ImageView left_arrow = new ImageView(new Image("/images/left_arrow.png"));
        Button tempButton = new Button();
        tempButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        tempButton.setGraphic(left_arrow);
        tempButton.setOnAction(e -> gameController.nextTemporaryUpgradeCard());

        Label tempLabel = new Label("Temporary");
        Label permLabel = new Label("Permanent");

        energyAmount = new Label("9");
        energyAmount.setFont(Font.font("System Bold", 30));
        energyAmount.setTextAlignment(TextAlignment.CENTER);

        ImageView energyImage = new ImageView(new Image("images/energy.png"));
        energyImage.setPreserveRatio(true);
        energyImage.setPickOnBounds(true);
        energyImage.setFitWidth(55);

        Label emptyLabel = new Label("              ");

        priceTempLabel = new Label("    Price: " + upgradeShop.getSelectedTemporaryCard().getCost() + " energy");
        pricePermLabel = new Label("    Price: " + upgradeShop.getSelectedPermanentCard().getCost() + " energy");

        Button purchaseTempButton = new Button("Purchase");
        Button purchasePermButton = new Button("Purchase");

        purchaseTempButton.setOnAction(e -> {
            gameController.purchaseTemporaryUpgradeCard(board.getCurrentPlayer());
            energyAmount.setText(String.valueOf(board.getCurrentPlayer().getEnergyCubes()));
        });
        purchasePermButton.setOnAction(e -> {
            gameController.purchasePermanentUpgradeCard(board.getCurrentPlayer());
            energyAmount.setText(String.valueOf(board.getCurrentPlayer().getEnergyCubes()));});


        // ENERGY
        GridPane energyPane = new GridPane();
        energyPane.add(emptyLabel, 0, 0);
        energyPane.add(energyImage, 1, 0);
        energyPane.add(energyAmount, 2, 0);
        energyPane.setAlignment(Pos.CENTER_RIGHT);

        // TOP
        GridPane topGridPane = new GridPane();
        topGridPane.setAlignment(Pos.CENTER);
        topGridPane.add(title, 0, 0);
        topGridPane.add(energyPane, 1, 0);


        // PURCHASE
        GridPane purchaseTempGridPane = new GridPane();
        purchaseTempGridPane.add(purchaseTempButton, 0, 0);
        purchaseTempGridPane.add(priceTempLabel, 1, 0);

        // PURCHASE
        GridPane purchasePermGridPane = new GridPane();
        purchasePermGridPane.add(purchasePermButton, 0, 0);
        purchasePermGridPane.add(pricePermLabel, 1, 0);

        // LEFT
        GridPane leftGridPane = new GridPane();
        leftGridPane.setAlignment(Pos.CENTER);
        // Configure leftGridPane as needed
        leftGridPane.add(tempButton, 0, 1);
        leftGridPane.add(tempLabel, 1, 0);
        leftGridPane.add(tempUpgradeCardImage, 1, 1);
        leftGridPane.add(purchaseTempGridPane, 1, 2);

        // RIGHT
        GridPane rightGridPane = new GridPane();
        rightGridPane.setAlignment(Pos.CENTER);
        // Configure rightGridPane as needed
        rightGridPane.add(permButton, 1, 1);
        rightGridPane.add(permLabel, 0,0);
        rightGridPane.add(permUpgradeCardImage, 0, 1);
        rightGridPane.add(purchasePermGridPane, 0, 2);

        // CONTAINER (LEFT - RIGHT) - mainGridPane
        GridPane mainGridPane = new GridPane();
        mainGridPane.setAlignment(Pos.CENTER);

        // Add all to the mainGridPane
        GridPane.setConstraints(mainGridPane, 0, 0, 2, 1); // 2 columns, 1 row
        GridPane.setConstraints(topGridPane, 0, 0, 2, 1); // 2 columns, 1 row
        mainGridPane.getChildren().add(topGridPane);

        GridPane.setConstraints(leftGridPane, 0, 1);
        mainGridPane.getChildren().add(leftGridPane);

        GridPane.setConstraints(rightGridPane, 1, 1);
        mainGridPane.getChildren().add(rightGridPane);


        // Set alignment and style of upgradeShop
        this.upgradeShopVBox.setStyle("-fx-background-color: #8f9295;");
        this.upgradeShopVBox.setAlignment(Pos.CENTER);

        // Add mainGridPane to UpgradeShop
        this.upgradeShopVBox.getChildren().add(mainGridPane);

        // Set the VBox (upgradeShop) to stretch horizontally and vertically
        VBox.setVgrow(upgradeShopVBox, Priority.ALWAYS);
        upgradeShopVBox.setMaxWidth(Double.MAX_VALUE);
        upgradeShopVBox.setMaxHeight(Double.MAX_VALUE);
        upgradeShopVBox.setAlignment(Pos.CENTER);
        upgradeShopVBox.setMaxHeight(400);

    }


    public void InterationRestrictor(Phase phase) {
        switch (phase) {
            case INITIALISATION -> {
                timerButton.setDisable(true);
                upgradeShopButton.setDisable(true);
            }
            case UPGRADE -> {
                timerButton.setDisable(true);
                upgradeShopButton.setDisable(false);
            }
            case PROGRAMMING -> {
                timerButton.setDisable(false);
                upgradeShopButton.setDisable(false); // SHOULD BE DISABLED
            }
            case SYNCHRONIZATION -> {
                timerButton.setDisable(true);
                upgradeShopButton.setDisable(true);
            }
            case ACTIVATION -> {
                timerButton.setDisable(true);
                upgradeShopButton.setDisable(true);
            }
            default -> {
                System.out.println("Something went wrong: Phase not defined");
            }
        }
    }

    private void displayUpgradeShop() {

        if (upgradeShopVBox.isVisible()) {
            System.out.println("upgrade shop is no longer displayed");
            upgradeShopBlurBackground.setVisible(false);
            upgradeShopVBox.setVisible(false);
        }
        else {
            System.out.println("upgrade shop is displayed");
            upgradeShopBlurBackground.setVisible(true);
            upgradeShopVBox.setVisible(true);
        }
    }

    private void nextTimer() {
        int k = (int) Math.floor(((float)board.getTimerSecondsCount())/5) + 1; //I hope this maths the math
        this.timerView.setImage(timers[k]);
        if (board.getTimerSecondsCount() >= 29){
            this.timerView.setImage(timers[0]);
        }
    }


    @Override
    public void updateView(Subject subject) {
        if (subject == board) {

            Phase phase = board.getPhase();
            InterationRestrictor(phase);
            statusLabel.setText(board.getStatusMessage());
            if (board.getTimerIsRunning()){
                nextTimer();
            }
        }
        if (subject == upgradeShop) {
            this.permUpgradeCardImage.setImage(new Image(upgradeShop.getSelectedPermanentCardImage()));
            this.tempUpgradeCardImage.setImage(new Image(upgradeShop.getSelectedTemporaryCardImage()));
            this.pricePermLabel.setText("    Price: " + String.valueOf(upgradeShop.getSelectedPermanentCard().getCost()) + " energy");
            this.priceTempLabel.setText("    Price: " + String.valueOf(upgradeShop.getSelectedTemporaryCard().getCost()) + " energy");
            energyAmount.setText(String.valueOf(board.getCurrentPlayer().getEnergyCubes()));
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
