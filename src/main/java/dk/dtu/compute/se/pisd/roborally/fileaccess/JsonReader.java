package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonReader {

    // TODO: Remove jsonFileName from arguments adn make it a static variable in each method


    ObjectMapper objectMapper = new ObjectMapper();


    /**
     * gets board data from the board json file
     * @param jsonFileName boardJsonFileName
     * @param key Value to be found
     * @param x position
     * @param y position
     * @return String value
     * @throws Exception Laziness
     */
    public String getBoardData(String jsonFileName, String key, int x, int y) throws Exception {
        JsonNode rootNode = objectMapper.readTree(new File("data", jsonFileName));
        JsonNode spacesNode = rootNode.get("spaces");

        for (JsonNode spaceNode : spacesNode) {
            if (spaceNode.get("x").asInt() == x && spaceNode.get("y").asInt() == y) {
                if (spaceNode.has(key)) {
                    // If the field is an array, return the entire array as a string
                    if (spaceNode.get(key).isArray()) {
                        if (spaceNode.get(key).size() == 1) {
                            return spaceNode.get(key).get(0).asText();
                        } else {
                            return spaceNode.get(key).toString();
                        }
                    }
                    // If the field is not an array but a simple key-value pair
                    else {
                        return spaceNode.get(key).asText();
                    }
                }
                if (spaceNode.has("actions")) {
                    for (JsonNode actionNode : spaceNode.get("actions")) {
                        if (actionNode.has("CLASSNAME") && key.equals("CLASSNAME")) {
                            return actionNode.get("CLASSNAME").asText();
                        }
                        if (actionNode.has("INSTANCE") && actionNode.get("INSTANCE").has(key)) {
                            return actionNode.get("INSTANCE").get(key).asText();
                        }
                    }
                }
                if (spaceNode.has("items")) {
                    for (JsonNode itemNode : spaceNode.get("items")) {
                        if (itemNode.has(key)) {
                            return itemNode.get(key).asText();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a value related to a player
     * Might require a NullPointerException
     * @param jsonFileName Json file name
     * @param key Json key attribute
     * @param playerName Player to get value from
     * @return String value of key
     * @throws IOException Im lazy :(
     */
    public String getPlayerData(String jsonFileName, String key, String playerName) throws IOException{
        // Read JSON file and convert to list of Person
        JsonNode node = objectMapper.readTree(new File("data", jsonFileName));

        // Check if the node is an array
        if (node.isArray()) {
            // Iterate through array elements
            for (JsonNode element : node) {
                // If the valueToCompare field is the key element, print the value is fo
                if (playerName.equals(element.get("name").asText())) {
                    System.out.println(element.get(key));
                    return element.get(key).asText();
                }
            }
        } else if (playerName.equals(node.get("name").asText())) {
            System.out.println(node.get(key));
            return node.get(key).asText();
        }
        return "No data";
    }

    public String getCardData(String jsonFileName, String playerName, String key) throws Exception {
        // Read JSON file and convert to list of Person
        JsonNode node = objectMapper.readTree(new File("data", jsonFileName));

        // Check if the node is an array
        if (node.isArray()) {
            // Iterate through array elements
            System.out.println("RUnning");
            for (JsonNode element : node) {
                // If the valueToCompare field is the key element, print the value is fo
                if (playerName.equals(element.get("name").asText())) {
                    System.out.println(element.get(key));
                    return element.get(key).asText();
                }
            }
        } else if (playerName.equals(node.get("name").asText())) {
            System.out.println(node.get(key));
            return node.get(key).asText();
        }
        return null;
    }
}
