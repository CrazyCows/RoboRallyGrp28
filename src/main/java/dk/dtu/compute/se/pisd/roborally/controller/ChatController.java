package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.ClientController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.JsonInterpreter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ThreadPoolManager;
import dk.dtu.compute.se.pisd.roborally.model.Player;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Future;


public class ChatController {

    ArrayList<String> names;
    JsonInterpreter jsonInterpreter;
    HashMap<String, String> newestReceivedMessages;
    ThreadPoolManager threadPoolManager;

    public ChatController(GameController gameController, ThreadPoolManager threadPoolManager) {
        this.jsonInterpreter = new JsonInterpreter();
        this.names = jsonInterpreter.getPlayerNames();
        this.newestReceivedMessages = new HashMap<>();
        for (String name : names) {
            this.newestReceivedMessages.put(name, "");
        }

        this.threadPoolManager = threadPoolManager;

        Future<?> chatFuture = threadPoolManager.submitTask(() -> {
            String message;
            while (true) {
                gameController.getClientController().getJSON("playerData.json");
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

        // Optional: Keep a reference to the Future in case you need to cancel the task or check its status
        // Not shown: Code to handle cancellation and exceptions
    }

    // Don't forget to shut down the thread pool when done
    public void shutdown() {
        threadPoolManager.shutdown();
    }
}
