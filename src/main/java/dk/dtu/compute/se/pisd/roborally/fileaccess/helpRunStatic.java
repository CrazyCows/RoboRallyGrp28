package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController(false, "tram");
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        jsonConnect.createJSON("sharedBoard.json");
        jsonConnect.createJSON("playerData.json");
        jsonConnect.getJSON("sharedBoard.json");


    }
}
