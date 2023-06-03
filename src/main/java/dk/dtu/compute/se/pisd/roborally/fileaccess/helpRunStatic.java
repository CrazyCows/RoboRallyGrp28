package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController();
        JsonSharedPlayerData jsonSharedPlayerData = new JsonSharedPlayerData();


        //jsonConnect.createBoard("118");
        //jsonConnect.pushPlayerData("118");




        //jsonConnect.getSharedPlayerData("118");

        //jsonConnect.getPlayerData("118");


        jsonSharedPlayerData.JsonReader();
    }
}
