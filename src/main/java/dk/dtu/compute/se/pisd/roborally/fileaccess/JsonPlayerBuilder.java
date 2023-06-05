package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonPlayerBuilder {
    private Player player;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> playerData = new HashMap<>();
    File file;


    // NOTE: Only first registered player is saved here. It is assumed the first player registered is always the local player, also online.

    public JsonPlayerBuilder(Player player){
        this.player = player;

        playerData.put("name", player.getName());
        playerData.put("color", player.getColor());
        playerData.put("readystate", player.isReady());
        playerData.put("playerisLead", player.isLeader());
        playerData.put("posx", player.getSpace().getPosition()[0]);
        playerData.put("posy", player.getSpace().getPosition()[1]);
        playerData.put("energycubes",player.getEnergyCubes()); //TODO: Does this work?
        /*
        playerData.put("startplace", player.getStartSpace());
        playerData.put("heading", player.getHeading());
         */
        file = new File("data", "playerData.json");

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException d){
            System.out.println(d);
        }
    }


    public void updateDynamicPlayerData(){
        if (this.player.getName().equals(player.getName())){
            playerData.put("posX", player.getSpace().getPosition()[0]);
            playerData.put("posY", player.getSpace().getPosition()[1]);
            playerData.put("readystate", player.isReady());
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException d){
            System.out.println(d);
        }
    }
}
