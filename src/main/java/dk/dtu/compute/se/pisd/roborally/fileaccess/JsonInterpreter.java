package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonInterpreter {

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
            System.out.println("Running");
            for (JsonNode element : node) {
                // If the valueToCompare field is the key element, print the value is fo
                if (playerName.equals(element.get("name").asText())) {
                    System.out.println(element.get(key));
                    return element.get(key).asText();
                }
            }
        } else if (playerName.equals(node.get("name").asText())) {
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


    /**
     *
     * @param
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





    public ArrayList<String> getInfoFromAllPlayers(String jsonFileName, String key) {
        try {
            JsonNode node = objectMapper.readTree(new File("data", jsonFileName));
            List<JsonNode> nodes = node.findValues(key);
            ArrayList<String> finish = new ArrayList<>();
            int i = 0;
            for (JsonNode json : nodes) {
                finish.add(nodes.get(i).toString());
                i++;
            }
            return finish;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getPlayerNames () {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<String> playerNames = JsonPath.read(json, "$.[*].name");

            return new ArrayList<>(playerNames);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMaster() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<String> master = JsonPath.read(json, "$.[?(@.master == true)].name");
            return master.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean gameStarted() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            return (Boolean) new ArrayList<>(JsonPath.read(json, "$.[?(@.master == true)].inGame")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public Boolean isReady (String playerName) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            return (Boolean) new ArrayList<>(JsonPath.read(json, "$.[?(@.name == '" + playerName + "')].readystate")).get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAllReady () {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<Boolean> playerReadyStates = JsonPath.read(json, "$.[*].readystate");

            if (playerReadyStates.contains(false)) {
                return false;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean isAnyReady (ArrayList<String> playerNames) {
        for (String name : playerNames) {
            if (isReady(name)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getColorsInUse() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            return JsonPath.read(json, "$.[*].color");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSimplePlayerInfoString(String playerName, String key) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<String> info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
            return info.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean getSimplePlayerInfoBoolean(String playerName, String key) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<Boolean> info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
            return info.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getSimplePlayerInfoInt(String playerName, String key) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<Integer> info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
            return info.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getMessage(String playerName) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("data/collectivePlayerData.json")));
            List<String> message = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')].message");
            return message.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

/*
    public List<String> getValuesFromBoard(String jsonFileName, @Nullable Integer x,@Nullable Integer y, String... keys){
        try {
            JsonNode nodes = objectMapper.readTree(new File("data", jsonFileName));
            List<String> values = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            Configuration conf = Configuration.defaultConfiguration();
            conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
                    .addOptions(Option.ALWAYS_RETURN_LIST);
            JsonArray spaces = JsonPath.using(conf).parse(nodes.toString()).read("$.spaces");

            if (x != null && y != null) {

                System.out.println("here");
                for (Object space : spaces) {
                    System.out.println("here2");
                    int spaceX = JsonPath.read(space, "$.x");
                    int spaceY = JsonPath.read(space, "$.y");
                    if (spaceX == x && spaceY == y) {
                        System.out.println("here3");
                        for (String key : keys) {
                            String value = JsonPath.read(space, "$." + key);
                            values.add(value);
                        }
                    }
                }
            }

/*
        for (String key : keys) {
            resultMap.put(key, null);
        }



            return values;
        } catch (Exception e){

        }
        return null;
    }
    */
}
