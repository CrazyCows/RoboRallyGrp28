package dk.dtu.compute.se.pisd.roborally.fileaccess;

import org.junit.jupiter.api.Test;

class ClientControllerTest {


    @Test
    void getJSON() {
        ClientController jsonConnect = new ClientController("kkkkkkkkkkkk");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
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
    void deleteJSON() {
    }
}