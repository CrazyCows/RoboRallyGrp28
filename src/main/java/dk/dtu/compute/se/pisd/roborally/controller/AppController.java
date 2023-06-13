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
import dk.dtu.compute.se.pisd.roborally.model.Phase;
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

import java.io.*;
import java.util.*;

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
    final private List<Integer> PLAYER_START_X_POSITION = Arrays.asList(2, 3, 4, 5, 6, 7);
    final private List<Integer> PLAYER_START_Y_POSITION = Arrays.asList(2, 3, 4, 5, 6, 7);

    final private RoboRally roboRally;

    private GameController gameController;

    private ClientController clientController;
    private String chosenBoard;
    private JsonInterpreter jsonInterpreter;

    private String animationRobotDirection;
    ArrayList<Label> usernameLabels;
    ArrayList<CheckBox> checkBoxes;
    private Player localPlayer;
    private String username;
    private String gameID;
    private volatile boolean isMaster;
    private boolean isPrivate;
    private String gamePassword;
    private String userColor;
    @FXML
    private Text winnerPlayer;
    private boolean autoSave;
    private int amountOfPlayers;
    private boolean online;
    private boolean windowSuccess = true;


    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void resetSetupProcess() {
        gameController = null;
        clientController = null;
        chosenBoard = null;
        jsonInterpreter = null;
        localPlayer = null;
        username = null;
        gameID = null;
        isMaster = false;
        isPrivate = false;
        gamePassword = null;
        userColor = null;
        winnerPlayer = null;
        autoSave = false;
        online = false;
    }

    public void newGameForm() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Online game");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField gameNameField = new TextField();
        ComboBox<Integer> amountOfPlayersField = new ComboBox<>();
        amountOfPlayersField.getItems().addAll(PLAYER_NUMBER_OPTIONS);
        amountOfPlayersField.getSelectionModel().selectFirst();
        CheckBox autoSaveCheckBox = new CheckBox("Auto Save");
        Label infoLabel = new Label();

        gridPane.add(new Label("Game name:"), 0, 0);
        gridPane.add(gameNameField, 1, 0);
        gridPane.add(new Label("Number of players:"), 0, 1);
        gridPane.add(amountOfPlayersField, 1, 1);
        gridPane.add(autoSaveCheckBox, 0, 2, 2, 1);
        gridPane.add(infoLabel, 1, 3);

        Label boardsLabel = new Label("Boards");
        ComboBox<String> boards = new ComboBox<>();
        boards.getItems().addAll(getBoardNames());
        boards.getSelectionModel().selectFirst();
        gridPane.add(boardsLabel, 0, 4);
        gridPane.add(boards, 1, 4);

        Button continueButton = new Button("Continue");
        gridPane.add(continueButton,0, 5);
        continueButton.setOnAction(e -> {
            jsonInterpreter = new JsonInterpreter();
            if (gameNameField.getText().equals("") || jsonInterpreter.getAllGames().contains(gameNameField.getText())) {
                infoLabel.setText("Invalid name or game already exists");
                return;
            }
            this.gameID = gameNameField.getText();
            this.amountOfPlayers = amountOfPlayersField.getSelectionModel().getSelectedItem();
            this.autoSave = autoSaveCheckBox.isSelected();
            this.chosenBoard = boards.getSelectionModel().getSelectedItem();
            this.clientController = new ClientController(false, this.gameID);

            System.out.println("gameName: " + gameID);
            System.out.println("amountOfPlayers: " + amountOfPlayers);
            System.out.println("autoSave: " + autoSave);

            jsonInterpreter = new JsonInterpreter();
            try {
                if (jsonInterpreter.gameStarted() && online) {
                    infoLabel.setText("Error: " + gameID + " already exists. ");
                }
                else {
                    dialogStage.close();
                }
            } catch (Exception ex) {
                System.out.println("Game does not exist");
                dialogStage.close();
            }
        });

        dialogStage.setOnCloseRequest(ex -> {
            resetSetupProcess();
            this.windowSuccess = false;
            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 230);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void newGame() {
        if (isGameRunning()) {
            resetSetupProcess();
        }
        resetSetupProcess();
        this.online = false;

        newGameForm();
        if (!windowSuccess) {
            windowSuccess = true;
            return;
        }

        roboRally.setMusicVolume(0.0);

        roboRally.removeStartImage();
        Board board = LoadBoard.loadBoard(chosenBoard, true);
        board.setOnline(false);
        roboRally.pauseMusic();


        for (int i = 0; i < this.amountOfPlayers; i++) {
            Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
            board.addPlayer(player);
            player.setSpace(board.getSpace(i % board.width, i));
        }
        assert board != null;
        gameController = new GameController(roboRally, clientController, board, this.online, null);
        gameController.setPhase(Phase.INITIALISATION);

        board.setCurrentPlayer(board.getPlayer(0));
        roboRally.createBoardView(gameController);

        clientController = new ClientController(false, gameID);
        clientController.createJSON("sharedBoard.json");
        for (Player player : gameController.board.getAllPlayers()) {
            JsonPlayerBuilder jsonPlayerBuilder = new JsonPlayerBuilder(player);
            jsonPlayerBuilder.createPlayerJSON(this.gameController);
            clientController.createJSON("playerData.json");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameController.setPhase(Phase.PROGRAMMING);

    }

    public void newOnlineGame() {
        if (isGameRunning()) {
            resetSetupProcess();
        }
        resetSetupProcess();
        this.online = true;
        this.isMaster = true;
        onlineGame();
        if (!windowSuccess) {
            windowSuccess = true;
            return;
        }
        setupRobot();
        if (!windowSuccess) {
            windowSuccess = true;
            return;
        }
        gameLobby();
        if (!windowSuccess) {
            windowSuccess = true;
        }
    }

    public void joinOnlineGame() {
        if (isGameRunning()) {
            resetSetupProcess();
        }
        this.online = true;
        this.isMaster = false;

        onlineGame();
        if (!windowSuccess) {
            windowSuccess = true;
            return;
        }

        setupRobot();
        if (!windowSuccess) {
            windowSuccess = true;
            return;
        }
        gameLobby();
        if (!windowSuccess) {
            windowSuccess = true;
        }
    }

    public void saveGame() {

        if (!online) {
            for (Player player : gameController.board.getAllPlayers()) {
                JsonPlayerBuilder jsonPlayerBuilder = new JsonPlayerBuilder(player);
                jsonPlayerBuilder.createPlayerJSON(this.gameController);
                clientController.updateJSON("playerData.json");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            clientController.getJSON("playerData.json");
            LoadBoard.saveBoard(gameController.board);
            clientController.updateJSON("sharedBoard.json");

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Save Game");
            String message = "Game has been saved. " ;
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Save Game");
            String message = "You can't save online games. ";
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    }

    private static String padString(String input, int length) {
        int paddingLength = length - input.length();
        int leftPadding = paddingLength / 2;
        int rightPadding = paddingLength - leftPadding;

        String paddedString = String.format("%" + leftPadding + "s%s%" + rightPadding + "s", "", input, "");
        return paddedString;
    }

    public void loadGameForm(ArrayList<String> availableGames) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Load Game");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);


        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefWidth(370);
        textArea.setPrefHeight(300);
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-size: 18px; -fx-font-family: 'Courier New';");

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        gridPane.add(scrollPane, 0, 0);

        textArea.setOnMouseClicked(event -> {
            System.out.println("touched!");
            if (textArea.getCaretPosition() >= textArea.getText().length()) {
                return;
            }
            System.out.println(textArea.getCaretPosition());
            int position = textArea.getCaretPosition();
            int from = position;
            int to = position;
            while (!textArea.getText(to, to+1).equals(" ") && !textArea.getText(to, to+1).equals("-")) {
                to += 1;
            }
            while (!textArea.getText(from-1, from).equals(" ") && !textArea.getText(from-1, from).equals("-")) {
                from -= 1;
            }
            gameID = textArea.getText(from, to);
            System.out.println("Clicked on: " + gameID);
            while (!textArea.getText(to, to+1).equals("-")) {
                to += 1;
            }
            while (!textArea.getText(from-1, from).equals("-")) {
                from -= 1;
            }
            textArea.selectRange(from, to);

        });

        textArea.appendText("-----------------------------\n");
        for (String availableGame : availableGames) {
            textArea.appendText(padString(availableGame, 30));
            textArea.appendText("-----------------------------\n");
        }
        textArea.appendText("\n-----------------------------");


        Button continueButton = new Button("Continue");
        gridPane.add(continueButton,0, 1);
        continueButton.setOnAction(e -> {
            dialogStage.close();
        });

        dialogStage.setOnCloseRequest(ex -> {
            resetSetupProcess();
            this.windowSuccess = false;
            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 420, 520);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void loadGame() {

        clientController = new ClientController();
        clientController.availableGamesJSON();
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        ArrayList<String> availableGames = jsonInterpreter.getAllGames();

        loadGameForm(availableGames);

        if (gameID != null) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!

                if (!stopGame()) {
                    String message = "Error string ";  // we should probably remove this.
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("No saved games");
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    alert.showAndWait();
                    return;
                }
            }

            roboRally.removeStartImage();

            clientController = new ClientController(false, gameID);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientController.getJSON("playerData.json");
            clientController.getJSON("sharedBoard.json");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Board board = LoadBoard.loadBoard("sharedBoard.json", false);
            board.setOnline(false);
            roboRally.pauseMusic();

            JsonPlayerBuilder.createPlayersFromLoad(board, jsonInterpreter.getPlayerNames());

            assert board != null;
            gameController = new GameController(roboRally, clientController, board, this.online, null);
            gameController.setPhase(Phase.PROGRAMMING);
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
        if (!isMaster) {
            robotComboBox.getItems().removeAll(jsonInterpreter.getColorsInUse());
        }
        robotComboBox.getSelectionModel().selectFirst();

        gridPane.add(robotComboBox, 0, 0);

        Button continueButton = new Button("Continue");

        String[] directions = {"north", "east", "south", "west"};

        Thread countThread = new Thread(() -> {
            int counter = 0;
            while (!continueButton.isPressed() || dialogStage.isShowing()) {
                this.animationRobotDirection = directions[counter];
                Platform.runLater(() -> {
                    ObservableList<Node> children = gridPane.getChildren();
                    children.removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 0);
                    String selectedOption = robotComboBox.getValue();
                    //System.out.println("images/robots/" + selectedOption + "_" + this.animationRobotDirection + "_facing_robot" + ".png");
                    Image robotImage = new Image("images/robots/" + selectedOption + "_" + this.animationRobotDirection + "_facing_robot" + ".png");
                    ImageView robotImageView = new ImageView(robotImage);
                    gridPane.add(robotImageView, 1, 0);
                });
                //System.out.println(counter);
                counter += 1;
                if (counter == directions.length) {
                    counter = 0;
                }
                try {
                    Thread.sleep(300);
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
            Board board = null;
            if (isMaster) {
                board = LoadBoard.loadBoard(this.chosenBoard, true);
                board.setOnline(true);
                assert board != null;
                LoadBoard.saveBoard(board);
                clientController.createJSON("sharedBoard.json");
            }
            else {
                clientController.getJSON("sharedBoard.json");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                board = LoadBoard.loadBoard(null, false);
                board.setOnline(true);

            }
            roboRally.pauseMusic();

            assert board != null;

            this.localPlayer = new Player(board, userColor, username);
            if (this.isMaster) {
                localPlayer.setMasterStatus(true);
                localPlayer.setMaster(localPlayer.getName());
            }

            board.addPlayer(localPlayer);
            this.localPlayer.setSpace(board.getSpace(
                    PLAYER_START_X_POSITION.get(PLAYER_COLORS.indexOf(localPlayer.getColor())),
                    PLAYER_START_Y_POSITION.get(PLAYER_COLORS.indexOf(localPlayer.getColor())))
            );


            gameController = new GameController(roboRally, clientController, board, this.online, localPlayer);
            gameController.setPhase(Phase.INITIALISATION);

            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);

            dialogStage.close();
        });

        dialogStage.setOnCloseRequest(ex -> {
            resetSetupProcess();
            this.windowSuccess = false;
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

        if (isMaster) {
            Label boardsLabel = new Label("Boards");
            ComboBox<String> boards = new ComboBox<String>();
            boards.getItems().addAll(getBoardNames());
            gridPane.add(boardsLabel, 0, 4);
            gridPane.add(boards, 1, 4);

            boards.setOnAction(e -> chosenBoard = boards.getSelectionModel().getSelectedItem());
        }

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
        gridPane.add(continueButton,0, 5);
        continueButton.setOnAction(e -> {
            String username = usernameField.getText();
            String lobbyName = lobbyNameField.getText();
            String password = passwordField.getText();
            boolean privateGame = privateGameCheckbox.isSelected();

            // Perform actions with the form data
            System.out.println("Username: " + username);
            System.out.println("Lobby name: " + lobbyName);
            System.out.println("Private Game: " + privateGame);
            if (!(Objects.equals(passwordField.getText(),"") || passwordField.getText() == null)){
                System.out.println("Password: " + passwordField.getText());
            }
            this.username = username;
            this.gameID = lobbyName;
            this.isPrivate = privateGame;
            this.gamePassword = password;
            this.clientController = new ClientController(true, this.gameID);

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

        dialogStage.setOnCloseRequest(ex -> {
            resetSetupProcess();
            this.windowSuccess = false;
            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 230);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private ArrayList<String> getBoardNames() {

        ArrayList<String> boards = new ArrayList<>();

        // Attempt to retrieve board names from resources
        try {
            ClassLoader classLoader = AppController.class.getClassLoader();
            File resourceFolder = new File(Objects.requireNonNull(classLoader.getResource("boards")).getFile());

            File[] files = resourceFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        String boardName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                        boards.add(boardName);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Resource 'boards' not found in the resources folder. Attempting to read from local 'boards' folder...");
        }


        if (boards.isEmpty()) {
            boards.add("defaultBoard");
            boards.add("dizzyHighway");
            boards.add("emptyTestBoard");
            boards.add("twister");

        }

        return boards;
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
        this.clientController.createJSON("playerData.json");
        this.clientController.createJSON("cardSequenceRequest.json");

        this.clientController.getJSON("playerData.json");
        if (!isMaster) {
            localPlayer.setMaster(jsonInterpreter.getMaster());
            this.clientController.createJSON("sharedBoard.json");
        }

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

                /*
                for (String name : names) {
                    System.out.println(name + ": " + jsonInterpreter.isReady(name));
                }
                */

                this.localPlayer.setReady(checkBoxes.get(0).isSelected());
                jsonPlayerBuilder.updateDynamicPlayerData();
                clientController.updateJSON("playerData.json");


                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Check if the stage is closed
                if (!dialogStage.isShowing()) {
                    break;
                }
                else if (isMaster && checkBoxes.get(0).isSelected() && jsonInterpreter.getPlayerNames().size() > 1) {
                    jsonPlayerBuilder.updateDynamicPlayerData();
                    clientController.updateJSON("playerData.json");
                    clientController.getJSON("playerData.json");

                    JsonPlayerBuilder.createPlayersFromLoad(gameController.board, names);
                    localPlayer.setInGame(true);

                    gameController.setPhase(Phase.PROGRAMMING);

                    Platform.runLater(dialogStage::close);
                }
                else if (!isMaster && jsonInterpreter.isAllReady()) {
                    jsonPlayerBuilder.updateDynamicPlayerData();
                    clientController.updateJSON("playerData.json");
                    clientController.getJSON("playerData.json");

                    JsonPlayerBuilder.createPlayersFromLoad(gameController.board, names);
                    localPlayer.setInGame(true);

                    gameController.setPhase(Phase.PROGRAMMING);

                    Platform.runLater(dialogStage::close);
                }
            }

            gameController.setupOnline();
            System.out.println("Game lobby thread has ended");
        });
        countThread.setDaemon(true);
        countThread.start();

        Button continueButton = new Button("Continue");
        continueButton.setOnAction(e -> {
            dialogStage.close();
        });

        dialogStage.setOnCloseRequest(ex -> {
            resetSetupProcess();
            this.windowSuccess = false;
            dialogStage.close();
        });

        VBox vbox = new VBox(gridPane, continueButton);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);

        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
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
