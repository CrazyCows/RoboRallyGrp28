package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonSharedPlayerData {
    ObjectMapper objectMapper = new ObjectMapper();

    /*
     *** This should be moved to another class. It is currently just to check the json files is OK
     */

    public void JsonReader() throws IOException{
        // Read JSON file and convert to list of Person
        JsonNode node = objectMapper.readTree(new File("data", "collectivePlayerData.json"));

        // Check if the node is an array
        if (node.isArray()) {
            // Iterate through array elements
            for (JsonNode element : node) {
                // If the "name" field is "John", print the "age" field
                if ("Player 2".equals(element.get("name").asText())) {
                    System.out.println("John's age is: " + element.get("color").asText());
                    break;
                }
            }
        }
    }

    public void getSharedData(){

    }

    // Should be used after each turn
    // Should only be used by the game leader
    public void deletePlayerDataFromServer(String ID){

    }


}
