package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ChatView extends Tab implements ViewObserver {

    private Player player;

    private VBox top;

    private Label chatLabel;

    private GridPane chatPane;

    private Button sendButton;

    private GameController gameController;
    TextArea chat;
    TextField inputField;


    public ChatView(GameController gameController, Player player) {
        super("Chat");
        this.setStyle("-fx-text-base-color: black;");

        top = new VBox();
        this.setContent(top);

        this.gameController = gameController;
        this.player = player;

        chatLabel = new Label(player.getName());

        chatPane = new GridPane();
        chatPane.setVgap(2.0);
        chatPane.setHgap(2.0);

        chat = new TextArea("Chat started! \nSay Hello to the other players, " + player.getName() + "!\n");
        chat.setPrefHeight(160);
        chat.setEditable(false);

        inputField = new TextField();
        inputField.setPrefHeight(45);

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            displayMessage(inputField.getText());
            gameController.sendMessage(inputField.getText());
        });

        chatPane.add(chat, 0, 0);
        chatPane.add(chatLabel, 0, 1);
        chatPane.add(inputField, 0, 2);
        chatPane.add(sendButton, 1, 2);
        top.getChildren().add(chatPane);
    }

    private void displayMessage(String message) {
        chat.appendText(message + "\n\n");
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == player) {
            displayMessage(player.getName() + ": " + player.getMessage());
        }
    }

}
