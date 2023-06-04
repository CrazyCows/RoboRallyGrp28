package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class JsonReader {

    // TODO: something


    ObjectMapper objectMapper = new ObjectMapper();


    /**
     * gets board data from the board json file
     * @param jsonFileName boardJsonFileName
     * @param key Value to be found
     * @param x key
     * @param y key
     * @return
     * @throws Exception
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
            System.out.println("Running");
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


    /**
     *
     * @param fileName
     * @param key
     * @return
     */
    public static JsonElement search(String fileName, String key) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(fileName)));
            JsonElement jsonElement = new Gson().fromJson(json, JsonElement.class);
            return searchInJson(jsonElement, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JsonElement searchInJson(JsonElement element, String key) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has(key)) {
                return object.get(key);
            } else {
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    JsonElement result = searchInJson(entry.getValue(), key);
                    if (result != null) {
                        return result;
                    }
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement value : array) {
                JsonElement result = searchInJson(value, key);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }


}
