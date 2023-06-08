package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ChatView extends Tab implements ViewObserver {

    private Player localPlayer;

    private VBox top;

    private Label chatLabel;

    private GridPane chatPane;

    private Button sendButton;

    private GameController gameController;
    TextArea chat;
    TextField inputField;
    ArrayList<Player> otherPlayers;
    boolean otherPlayersSetup;


    public ChatView(GameController gameController, Player localPlayer) {
        super("Chat");
        this.setStyle("-fx-text-base-color: black;");

        top = new VBox();
        this.setContent(top);

        this.gameController = gameController;
        this.localPlayer = localPlayer;
        this.otherPlayers = new ArrayList<>();
        this.otherPlayersSetup = false;

        chatLabel = new Label(localPlayer.getName());

        chatPane = new GridPane();
        chatPane.setVgap(2.0);
        chatPane.setHgap(2.0);

        chat = new TextArea("Chat started! \nSay Hello to the other players, " + localPlayer.getName() + "!\n");
        chat.setPrefHeight(160);
        chat.setEditable(false);

        inputField = new TextField();
        inputField.setPrefHeight(45);

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            displayMessage("You: " + inputField.getText());
            gameController.sendMessage(inputField.getText());
        });

        chatPane.add(chat, 0, 0);
        chatPane.add(chatLabel, 0, 1);
        chatPane.add(inputField, 0, 2);
        chatPane.add(sendButton, 1, 2);
        top.getChildren().add(chatPane);

        localPlayer.attach(this);
        update(localPlayer);

    }

    private void displayMessage(String message) {
        chat.appendText(message + "\n\n");
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == localPlayer && !otherPlayersSetup) {
            if (otherPlayers.isEmpty()) {
                for (Player player : gameController.board.getAllPlayers()) {
                    if (player != subject) {
                        player.attach(this);  // Attach the ChatView as an observer to the other players
                        update(player);  // Perform initial update for each player
                        otherPlayers.add(player);
                    }
                }
                if (!otherPlayers.isEmpty()) {
                    otherPlayersSetup = true;
                }
            }
        }
        else if (otherPlayers.contains(subject)) {
            for (Player player : otherPlayers) {
                if (subject == player) {
                    displayMessage(player.getName() + ": " + player.getMessage());
                }
            }
        }
    }

}
