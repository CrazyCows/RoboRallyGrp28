package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController("tram");
        JsonReader jsonReader = new JsonReader();
        jsonConnect.createJSON("sharedBoard.json");
        jsonConnect.createJSON("playerData.json");
        jsonConnect.getJSON("playerData.json");
        jsonConnect.updateJSON("playerData.json");


    }
}
