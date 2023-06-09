package dk.dtu.compute.se.pisd.roborally.fileaccess;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ClientControllerTest {


    @Test
    void getJSON() {
        ClientController jsonConnect = new ClientController("kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.getJSON("cardSequenceRequest.json");
        jsonConnect.updateJSON("cardSequenceRequest.json");
        jsonConnect.getJSON("cardSequenceRequest.json");
    }


    @Test
    void createJSON() {
        ClientController jsonConnect = new ClientController("kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.createJSON("cardSequenceRequest.json");
    }

    @Test
    void updateJSON() {
        ClientController jsonConnect = new ClientController("kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.updateJSON("cardSequenceRequest.json");

    }

    @Test
    void getAllAvailableGames() {
        ClientController jsonConnect = new ClientController("kkkkkkkkkkkk");
        jsonConnect.availableGamesJSON();
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        ArrayList<String> games = jsonInterpreter.getAllGames();
        for (String game : games) {
            System.out.println(game);
        }
    }

    @Test
    void deleteJSON() {
    }

    @Test
    void updateReadyStatusForPlayer() {

    }

}