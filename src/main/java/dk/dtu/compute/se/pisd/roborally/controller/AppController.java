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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.fileaccess.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 * The AppController is used to set up a game, and
 * initializes most of the objects that will be used
 * throughout the game.
 * It uses JavaFX input to start / close / save a game.
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    private List<String> savedBoards;

    final private RoboRally roboRally;

    private GameController gameController;

    private ClientController clientController;
    private JsonInterpreter jsonInterpreter;

    private String animationRobotDirection;
    ArrayList<Label> usernameLabels;
    ArrayList<CheckBox> checkBoxes;
    private Player localPlayer;
    private String username;
    private String lobbyID;
    private boolean isMaster;
    private boolean isPrivate;
    private String gamePassword;
    private String userColor;
    @FXML
    private Text winnerPlayer;


    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    String ID;

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        roboRally.setMusicVolume(0.0);
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            roboRally.removeStartImage();
            Board board = LoadBoard.loadBoard(null, true);
            roboRally.pauseMusic();

            int no = result.get();
            for (int i = 1; i <= no; i++) { //TODO: Changed by Anton so players dont start on pit
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            assert board != null;
            gameController = new GameController(roboRally, board, false, null);

            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);

        }
    }
    public void newGame(int numberOfPlayers) {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        roboRally.setMusicVolume(0.0);
        Optional<Integer> result = numberOfPlayers == -1 ? dialog.showAndWait() : Optional.of(numberOfPlayers);

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            roboRally.removeStartImage();
            Board board = LoadBoard.loadBoard(null, true);
            roboRally.pauseMusic();

            int no = result.get();
            for (int i = 1; i <= no; i++) { //TODO: Changed by Anton so players dont start on pit
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            assert board != null;
            gameController = new GameController(roboRally, board, false, null);

            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);

        }
    }

    // Should be deleted at some point. Proof of concept.

    public void newOnlineGame() {
        this.isMaster = true;
        onlineGame();
        setupRobot();
        gameLobby();
    }

    public void joinOnlineGame() {
        this.isMaster = false;

        onlineGame();

        setupRobot();
        gameLobby();
    }

    public void saveGame() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Name the save file");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String saveName = result.get();
            LoadBoard.saveBoard(gameController.board, saveName);
        }
    }

    public void loadGame() {
        // XXX needs to be implememged eventually
        // for now, we just create a new game
        /*if (gameController == null) {
            newGame();
        }*/
        //ClientController clientController = new ClientController();
        this.savedBoards = new ArrayList<>();

        File folder = new File("./Save Games");
        File[] listJsonFiles = folder.listFiles();

        if (listJsonFiles == null) {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("OK");
            dialog.setTitle("Load Game");
            dialog.setHeaderText("There are no saved games");
            roboRally.setMusicVolume(0.0);
            //Optional<String> result = dialog.showAndWait();
        }
        for (File file : listJsonFiles) {
            if (file.isFile() && file.getName().endsWith("_board.json")) {
                savedBoards.add(file.getName());
            }
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(this.savedBoards.get(0), this.savedBoards);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Select a game to load");
        roboRally.setMusicVolume(0.0);
        Optional<String> result = dialog.showAndWait();

        System.out.println(result);

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }


            roboRally.removeStartImage();
            Board board = LoadBoard.loadBoard(result.orElse(""), false);
            roboRally.pauseMusic();

            int no = 2;
            for (int i = 0; i < no; i++) {
                assert board != null;
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            gameController = new GameController(roboRally, board, false, null);
            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);
        }

    }

    public void setupRobot() {

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Setup robot");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        ComboBox<String> robotComboBox = new ComboBox<>();
        robotComboBox.getItems().addAll(PLAYER_COLORS);
        robotComboBox.getItems().removeAll(jsonInterpreter.getColorsInUse());
        robotComboBox.getSelectionModel().selectFirst();

        gridPane.add(robotComboBox, 0, 0);

        /*robotComboBox.setOnAction(e -> {
            // Remove previous ImageView, if exists
            int columnIndex = 1;
            int rowIndex = 0;
            ObservableList<Node> children = gridPane.getChildren();
            children.removeIf(node -> GridPane.getColumnIndex(node) == columnIndex && GridPane.getRowIndex(node) == rowIndex);

            // Add new ImageView for the selected option
            String selectedOption = robotComboBox.getValue();
            if (selectedOption != null) {
                Image robotImage = new Image("images/robots/" + selectedOption + "_north_facing_robot" + ".png");
                ImageView robotImageView = new ImageView(robotImage);
                robotImageView.setFitWidth(100);
                robotImageView.setFitHeight(100);
                gridPane.add(robotImageView, columnIndex, rowIndex);
            }
        });*/

        Button continueButton = new Button("Continue");

        String[] directions = {"north", "east", "south", "west"};
        String direction = "";
        Thread countThread = new Thread(() -> {
            int counter = 0;
            while (!continueButton.isPressed()) {
                this.animationRobotDirection = directions[counter];
                Platform.runLater(() -> {
                    ObservableList<Node> children = gridPane.getChildren();
                    children.removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 0);
                    String selectedOption = robotComboBox.getValue();
                    System.out.println("images/robots/" + selectedOption + "_" + this.animationRobotDirection + "_facing_robot" + ".png");
                    Image robotImage = new Image("images/robots/" + selectedOption + "_" + this.animationRobotDirection + "_facing_robot" + ".png");
                    ImageView robotImageView = new ImageView(robotImage);
                    gridPane.add(robotImageView, 1, 0);
                });
                System.out.println(counter);
                counter += 1;
                if (counter == directions.length) {
                    counter = 0;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Check if the stage is closed
                if (!dialogStage.isShowing()) {
                    break;
                }
            }
            System.out.println("Animation thread has ended");
        });
        countThread.start();

        continueButton.setOnAction(e -> {
            String selectedRobot = robotComboBox.getValue();
            System.out.println(selectedRobot);
            this.userColor = selectedRobot;


            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            roboRally.removeStartImage();
            Board board = LoadBoard.loadBoard(null, true);
            roboRally.pauseMusic();

            assert board != null;

            this.localPlayer = new Player(board, userColor, username);
            if (this.isMaster) {
                localPlayer.setMasterStatus(true);
            }

            board.addPlayer(localPlayer);
            localPlayer.setSpace(board.getSpace(1 % board.width, 1));

            gameController = new GameController(roboRally, board, true, localPlayer);

            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);


            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane, continueButton);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void onlineGame() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Online game");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField usernameField = new TextField();
        TextField lobbyNameField = new TextField();
        CheckBox privateGameCheckbox = new CheckBox("Private Game");
        TextField passwordField = new TextField();
        Label infoLabel = new Label();

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Lobby name:"), 0, 1);
        gridPane.add(lobbyNameField, 1, 1);
        gridPane.add(privateGameCheckbox, 0, 2, 2, 1);
        gridPane.add(infoLabel, 1, 3);

        Label passwordLabel = new Label("password");
        gridPane.add(passwordLabel, 0, 3);
        gridPane.add(passwordField, 1,3);
        passwordField.setVisible(false);
        passwordLabel.setVisible(false);

        privateGameCheckbox.setOnAction(e -> {
            if (privateGameCheckbox.isSelected()) {
                passwordField.setVisible(true);
                passwordLabel.setVisible(true);
                System.out.println("Private game selected");
            } else {
                passwordField.setVisible(false);
                passwordLabel.setVisible(false);
                System.out.println("Private game not selected");
            }
        });

        Button continueButton = new Button("Continue");
        continueButton.setOnAction(e -> {
            String username = usernameField.getText();
            String lobbyName = lobbyNameField.getText();
            String password = passwordField.getText();
            boolean privateGame = privateGameCheckbox.isSelected();

            // Perform actions with the form data
            System.out.println("Username: " + username);
            System.out.println("Lobby name: " + lobbyName);
            System.out.println("Private Game: " + privateGame);
            System.out.println("Password: " + passwordField);
            this.username = username;
            this.lobbyID = lobbyName;
            this.isPrivate = privateGame;
            this.gamePassword = password;
            this.clientController = new ClientController(this.lobbyID);

            jsonInterpreter = new JsonInterpreter();
            try {
                clientController.getJSON("playerData.json");
                if (jsonInterpreter.gameStarted()) {
                    infoLabel.setText("Error: Game already started");
                }
                else if (jsonInterpreter.getPlayerNames().size() > 6) {
                    infoLabel.setText("Game has too many players. ");
                }
                else {
                    dialogStage.close();
                }
            } catch (Exception ex) {
                System.out.println("Game does not exist");
                if (!isMaster) {
                    infoLabel.setText("Error: game doesn't exist");
                }
                else {
                    dialogStage.close();
                }
            }
        });

        VBox vbox = new VBox(gridPane, continueButton);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void gameLobby() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Game lobby");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        usernameLabels = new ArrayList<>();
        checkBoxes = new ArrayList<>();
        usernameLabels.add(new Label(this.username));
        checkBoxes.add(new CheckBox());
        for (int i = 0; i < 5; i++) {
            Label usernameLabel = new Label();
            CheckBox checkBox = new CheckBox();
            checkBox.setDisable(true);
            usernameLabels.add(usernameLabel);
            checkBoxes.add(checkBox);
        }

        for (int i = 0; i < 6; i++) {
            gridPane.add(checkBoxes.get(i), 0, i);
            gridPane.add(usernameLabels.get(i), 1, i);
        }

        JsonPlayerBuilder jsonPlayerBuilder = new JsonPlayerBuilder(this.localPlayer);
        jsonInterpreter = new JsonInterpreter();
        System.out.println(localPlayer.getName());
        this.clientController.createJSON("sharedBoard.json");
        this.clientController.createJSON("playerData.json");

        this.clientController.getJSON("playerData.json");
        localPlayer.setMaster(jsonInterpreter.getMaster());

        Thread countThread = new Thread(() -> {
            while (true) {

                this.clientController.getJSON("playerData.json");
                ArrayList<String> names = jsonInterpreter.getPlayerNames();
                names.removeIf(s -> s.equals(username));

                System.out.println("All ready: " + jsonInterpreter.isAllReady());

                Platform.runLater(() -> {
                    if (!names.isEmpty()) {
                        for (int i = 1; i <= names.size(); i++) {
                            usernameLabels.get(i).setText(names.get(i - 1));
                        }

                        for (int i = 1; i <= names.size(); i++) {
                            checkBoxes.get(i).setSelected(jsonInterpreter.isReady(names.get(i - 1)));
                        }
                    }
                });

                this.localPlayer.setReady(checkBoxes.get(0).isSelected());
                jsonPlayerBuilder.updateDynamicPlayerData();
                clientController.updateJSON("playerData.json");


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Check if the stage is closed
                if (!dialogStage.isShowing()) {
                    break;
                }
                else if (isMaster && checkBoxes.get(0).isSelected()) {
                    localPlayer.setInGame(true);
                    jsonPlayerBuilder.updateDynamicPlayerData();
                    clientController.updateJSON("playerData.json");
                    clientController.getJSON("playerData.json");

                    createAllNonLocalPlayers(jsonInterpreter, gameController.board, names);

                    Platform.runLater(dialogStage::close);
                    break;
                }
            }
            System.out.println("Game lobby thread has ended");
        });
        countThread.start();




        Button continueButton = new Button("Continue");
        continueButton.setOnAction(e -> {
            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane, continueButton);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void createAllNonLocalPlayers(JsonInterpreter jsonInterpreter, Board board, ArrayList<String> names) {

        int counter = 2;
        for (String name : names) {
            Player player = new Player(board, jsonInterpreter.getSimplePlayerInfo(name, "color"), name);
            player.setInGame(Boolean.parseBoolean(jsonInterpreter.getSimplePlayerInfo(name, "inGame")));
            player.setReady(Boolean.parseBoolean(jsonInterpreter.getSimplePlayerInfo(name, "readystate")));
            player.setMasterStatus(Boolean.parseBoolean(jsonInterpreter.getSimplePlayerInfo(name, "master")));
            board.addPlayer(player);
            player.setSpace(board.getSpace(counter % board.width, counter));
            counter += 1;

        }
    }


    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
