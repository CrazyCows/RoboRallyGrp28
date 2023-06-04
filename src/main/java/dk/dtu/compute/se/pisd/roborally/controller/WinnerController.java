package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.StartRoboRally;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.awt.event.ActionEvent;

public class WinnerController {

    private RoboRally roboRally;
    private String playerName;
    private int numberOfPlayers;
    @FXML
    private Text winnerPlayer;

    @FXML
    public void initialize(RoboRally roboRally, String playerName, int numberOfPlayers) {
        this.roboRally = roboRally;
        this.playerName = playerName;
        this.numberOfPlayers = numberOfPlayers;
        setWinnerPlayerText();
    }

    private void setWinnerPlayerText(){
        winnerPlayer.setText(playerName);
    }

    @FXML
    public void onMainMenu(ActionEvent actionEvent){
        // when you click the main menu button on the winner screen.


        roboRally.start(roboRally.getStage());
    }

    @FXML
    public void onPlayAgain(ActionEvent actionEvent){
        // When you click the play again button on the winner screen

        AppController appController = new AppController(roboRally);
        appController.newGame(numberOfPlayers);

    }



}
