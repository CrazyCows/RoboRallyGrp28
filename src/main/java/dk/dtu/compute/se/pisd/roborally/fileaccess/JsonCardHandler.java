package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCardHandler {
    private Player player;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> cardData = new HashMap<>();
    File file;


    // NOTE: Only first registered player is saved here. It is assumed the first player registered is always the local player, also online.

    public JsonCardHandler(Player player){
        this.player = player;
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        for (int i = 0; i < player.getProgramSize(); i++){
            card = player.getProgramField(i).getCard();

            //cardData.put("name");

        }


        /*
        playerData.put("startplace", player.getStartSpace());
        playerData.put("heading", player.getHeading());
         */
        file = new File("data", "playerData.json");
/*
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException d){
            System.out.println(d);
        }

 */
    }


    public void updateDynamicPlayerData(){

/*        if (this.player.getName().equals(player.getName())){
            playerData.put("posX", player.getSpace().getPosition()[0]);
            playerData.put("posY", player.getSpace().getPosition()[1]);
            playerData.put("readyState", player.isReady());
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException d){
            System.out.println(d);
        }

 */
    }
}
