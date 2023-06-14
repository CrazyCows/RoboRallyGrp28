package dk.dtu.compute.se.pisd.roborally.fileaccess;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

@Disabled
class ClientControllerTest {

    @Test
    void getJSON() {
        ClientController jsonConnect = new ClientController(false, "kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.getJSON("cardSequenceRequest.json");
        jsonConnect.updateJSON("cardSequenceRequest.json");
        jsonConnect.getJSON("cardSequenceRequest.json");
    }


    @Test
    void createJSON() {
        ClientController jsonConnect = new ClientController(false, "kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.createJSON("cardSequenceRequest.json");
    }

    @Test
    void createAndGetBoard() {
        ClientController jsonConnect = new ClientController(false, "hejeje");

        jsonConnect.createJSON("sharedBoard.json");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jsonConnect.getJSON("sharedBoard.json");
    }

    @Test
    void updateJSON() {
        ClientController jsonConnect = new ClientController(false, "kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.updateJSON("cardSequenceRequest.json");

    }

    @Test
    void getAllAvailableGames() {
        ClientController jsonConnect = new ClientController(false, "kkkkkkkkkkkk");
        jsonConnect.availableGamesJSON();
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        ArrayList<String> games = jsonInterpreter.getAllGames();
        for (String game : games) {
            System.out.println(game);
        }
    }

    @Test
    void startLocalRestfulServer() {
        assertTrue(ClientController.startRestful());
    }

    @Test
    void deleteJSON() {
    }

    @Test
    void updateReadyStatusForPlayer() {

    }

}