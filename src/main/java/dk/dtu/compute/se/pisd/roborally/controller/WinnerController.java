package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WinnerController {

    private RoboRally roboRally;
    private String playerName;
    @FXML
    private Text winnerPlayer;

    @FXML
    public void initialize(RoboRally roboRally, String playerName) {
        this.roboRally = roboRally;
        this.playerName = playerName;
        setWinnerPlayerText();
    }

    private void setWinnerPlayerText(){
        winnerPlayer.setText(playerName);
    }



}
