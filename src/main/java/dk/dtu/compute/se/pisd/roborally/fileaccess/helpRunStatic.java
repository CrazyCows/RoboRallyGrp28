package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController();
        JsonSharedPlayerData jsonSharedPlayerData = new JsonSharedPlayerData();


        //jsonConnect.createBoard("117");
        jsonConnect.pushPlayerData("117");




        jsonConnect.getSharedPlayerData("117");



        jsonSharedPlayerData.JsonReader();
    }
}
