package dk.dtu.compute.se.pisd.roborally.fileaccess;

public class helpRunStatic {
    public static void main(String[] args) throws Exception{
        ClientController jsonConnect = new ClientController("201");
        /*
        jsonConnect.createJSON("sharedBoard.json");
        jsonConnect.createJSON("playerData.json");
        jsonConnect.getJSON("playerData.json");

         */
        JsonReader jsonReader = new JsonReader();
        //System.out.println(jsonReader.getNames("collectivePLayerData.json", "readyState"));
        //System.out.println(jsonReader.getValuesFromBoard("sharedBoard.json", 3, 3, "walls").get(0));
    }
}
