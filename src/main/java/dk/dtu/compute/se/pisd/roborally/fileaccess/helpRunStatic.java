package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController("202");
        jsonConnect.createJSON("sharedBoard.json");
        jsonConnect.createJSON("");
        jsonConnect.createJSON("playerData.json");
        jsonConnect.getJSON("playerData.json");
    }
}
