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
        playerData.put("readyState", player.isReady());
        playerData.put("posX", player.getSpace().getPosition()[0]);
        playerData.put("posY", player.getSpace().getPosition()[1]);
        playerData.put("energyCubes",player.getEnergyCubes()); //TODO: Does this work?

        file = new File("data", "collectivePlayerData.json");

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException d){
            System.out.println(d);
        }
    }


    public void updateDynamicPlayerData(Player player){
        if (this.player.getName().equals(player.getName())){
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
    }
}
