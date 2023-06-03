package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController();
        jsonConnect.createJSON("116", "sharedBoard.json");
    }
}
