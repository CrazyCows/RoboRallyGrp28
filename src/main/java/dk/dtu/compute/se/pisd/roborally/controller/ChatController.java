package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.ClientController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonInterpreter;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatController {

    ArrayList<String> names;
    JsonInterpreter jsonInterpreter;
    HashMap<String, String> newestReceivedMessages;
    ClientController clientController;

    public ChatController(GameController gameController, ClientController clientController) {
        this.clientController = clientController;
        this.jsonInterpreter = new JsonInterpreter();
        this.names = jsonInterpreter.getPlayerNames();
        this.newestReceivedMessages = new HashMap<>();
        for (String name : names) {
            this.newestReceivedMessages.put(name, "");
        }

        Thread chatThread = new Thread(() -> {
            String message;
            while (true) {
                clientController.getJSON("playerData.json");
                for (String name : names) {
                    message = jsonInterpreter.getMessage(name);
                    if (!message.equals(this.newestReceivedMessages.get(name))) {
                        System.out.println("NEW MESSAGE");
                        this.newestReceivedMessages.put(name, message);
                        Player player = gameController.board.getPlayer(name);
                        player.setMessage(message);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        chatThread.start();
    }

}
