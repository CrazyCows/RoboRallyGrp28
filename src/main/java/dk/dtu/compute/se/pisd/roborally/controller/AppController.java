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

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
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

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

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
            gameController = new GameController(board);
            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);
        }
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
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            gameController = new GameController(board);
            board.setCurrentPlayer(board.getPlayer(0));
            roboRally.createBoardView(gameController);
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
