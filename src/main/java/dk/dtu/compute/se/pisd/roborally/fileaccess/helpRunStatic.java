package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController("116");
        jsonConnect.createJSON("sharedBoard.json");
    }
}
