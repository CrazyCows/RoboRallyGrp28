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
package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import java.io.File;
import java.io.IOException;


/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;

    private Stage stage;
    private BorderPane boardRoot;
    private MediaPlayer mediaPlayer;
    private AppController appController;
    @FXML
    private Text winnerPlayer;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;


        Image icon = new Image("robotIcon.png");
        stage.getIcons().add(icon);

        this.appController = new AppController(this);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);

        addImageToWindow();
        this.mediaPlayer = play8BitMusic();
        new Thread(() -> {
            // Play the music at half volume
            mediaPlayer.setVolume(0.00);
            mediaPlayer.play();
        }).start();

        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);
        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();

        stage.show();
    }

    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();


        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    private void addImageToWindow() {
        // create an ImageView object for the image
        ImageView imageView = new ImageView(new Image("file:Bruno!.jpeg"));

        // set the size and position of the image
        imageView.setFitHeight(420);
        imageView.setFitWidth(620);
        imageView.setPreserveRatio(false);

        // add the image to the bottom of the BorderPane
        boardRoot.setBottom(imageView);
    }

    public void removeStartImage() {
        ObservableList<Node> children = boardRoot.getChildren();
        if (!children.isEmpty() && children.get(children.size() - 1) instanceof ImageView) {
            children.remove(children.size() - 1);
        }
    }

    private MediaPlayer play8BitMusic() {
        // Use a Media object to load the music file from disk
        Media musicFile = new Media(new File("8bityo.mp3").toURI().toString());

        // Create a MediaPlayer object to play the music file
        MediaPlayer mediaPlayer = new MediaPlayer(musicFile);

        // Set the MediaPlayer to loop indefinitely
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Play the music
        //mediaPlayer.play();

        // Return the MediaPlayer object
        return mediaPlayer;
    }

    public void setMusicVolume(double volume) {
        this.mediaPlayer.setVolume(volume);
    }

    public void playMusic() {
        this.mediaPlayer.play();
    }

    public void pauseMusic() {
        this.mediaPlayer.pause();
    }

    public Stage getStage(){
        return stage;
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public void winScreen(Player currentPlayer) {
        Stage winnerStage = new Stage();
        winnerStage.setTitle("Winner Screen");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/winnerScreen.fxml"));
        try {
            Pane parent = fxmlLoader.load();
            Scene winnerScene = new Scene(parent);
            winnerStage.setScene(winnerScene);

            winnerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The player " + currentPlayer.getName() + " has won!");
        stage.close();
    }

    @FXML
    public void onExitGame(ActionEvent actionEvent) {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}