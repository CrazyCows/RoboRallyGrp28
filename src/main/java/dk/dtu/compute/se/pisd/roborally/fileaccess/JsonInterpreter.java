package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import dk.dtu.compute.se.pisd.roborally.model.card.Card;
import dk.dtu.compute.se.pisd.roborally.model.card.DamageCard;
import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonInterpreter {

    // TODO: Remove jsonFileName from arguments and make it a static variable in each method


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
    public synchronized String getBoardData(String jsonFileName, String key, int x, int y) throws Exception {
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
    public synchronized String getPlayerData(String jsonFileName, String key, String playerName) throws IOException{
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

    public synchronized String getCardData(String jsonFileName, String playerName, String key) throws Exception {
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

    public synchronized static JsonElement search(String fileName, String key) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(fileName)));
            JsonElement jsonElement = new Gson().fromJson(json, JsonElement.class);
            return searchInJson(jsonElement, key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized static JsonElement searchInJson(JsonElement element, String key) {
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





    public synchronized ArrayList<String> getInfoFromAllPlayers(String jsonFileName, String key) {
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

    public synchronized ArrayList<String> getPlayerNames () {  //TDOO: ADD catch
        String json = getFileAsString("collectivePlayerData.json");
        List<String> playerNames = JsonPath.read(json, "$.[*].name");

        return new ArrayList<>(playerNames);
    }

    public synchronized String getMaster() {
        String json = getFileAsString("collectivePlayerData.json");
        List<String> master = JsonPath.read(json, "$.[?(@.isMaster == true)].name");
        return master.get(0);
    }

    public synchronized boolean gameStarted() {
        String json = getFileAsString("collectivePlayerData.json");
        return (Boolean) new ArrayList<>(JsonPath.read(json, "$.[?(@.isMaster == true)].inGame")).get(0);
    }

    public synchronized Boolean isReady (String playerName) {
        String json = getFileAsString("collectivePlayerData.json");
        return (Boolean) new ArrayList<>(JsonPath.read(json, "$.[?(@.name == '" + playerName + "')].readystate")).get(0);
    }

    public synchronized boolean isAllReady () {
        String json = getFileAsString("collectivePlayerData.json");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Boolean> playerReadyStates = JsonPath.read(json, "$.[*].readystate");

        if (playerReadyStates.contains(false)) {
            return false;
        }
        return true;
    }

    public synchronized Boolean isAnyReady (ArrayList<String> playerNames) {
        for (String name : playerNames) {
            if (isReady(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ArrayList<String> getColorsInUse() {
        String json = getFileAsString("collectivePlayerData.json");
        return JsonPath.read(json, "$.[*].color");
    }

    public synchronized String getSimplePlayerInfoString(String playerName, String key) {
        String json = getFileAsString("collectivePlayerData.json");
        List<String> info = null;
        while (info == null || info.size() == 0){
            info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return info.get(0);
    }

    public synchronized  boolean getSimplePlayerInfoBoolean(String playerName, String key) {
        String json = getFileAsString("collectivePlayerData.json");
        List<Boolean> info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
        return info.get(0);
    }
    public synchronized int getSimplePlayerInfoInt(String playerName, String key) {
        String json = getFileAsString("collectivePlayerData.json");
        List<Integer> info = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')]." + key);
        return info.get(0);
    }

    public synchronized String getMessage(String playerName) {
        String json = getFileAsString("collectivePlayerData.json");
        List<String> message = JsonPath.read(json, "$.[?(@.name == '" + playerName + "')].message");
        return message.get(0);
    }

    public synchronized ArrayList<Card> getAllCardsFromPlayer(String playerName, String cardsToGet) {
        ArrayList<Card> cards = new ArrayList<>();
        String json = getFileAsString("collectivePlayerData.json");
        int cardsAmount = 0;
        ArrayList<String> placeHolder = new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + cardsAmount + "'].name"));
        while (!placeHolder.isEmpty()) {
            cardsAmount += 1;
            placeHolder = new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + cardsAmount + "'].name"));
        }

        int current = 0;

        for (int i = 0; i < cardsAmount; i++) {
            if (cardsToGet.equals("permanentUpgradeCards")) {
                DamageCard damageCard = new DamageCard(
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "name")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "effect")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "imagePath")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "actionClassName")).get(0)
                );
                cards.add(damageCard);
                }
            else if (cardsToGet.equals("permanentCards")) {

                ProgrammingCard programmingCard = new ProgrammingCard(
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "name")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "imagePath")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "actionClassName")).get(0),
                        (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "commandName")).get(0)
                );
                cards.add(programmingCard);

            }
            else {
                // if cardTypeDeterminer.size() == 0, card is a damageCard (doesn't have commandName)
                ArrayList<String> cardTypeDeterminer = JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "commandName");
                if (cardTypeDeterminer.size() == 0) {
                    DamageCard damageCard = new DamageCard(
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "name")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "effect")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "imagePath")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "actionClassName")).get(0)
                    );
                    cards.add(damageCard);
                } else {
                    ProgrammingCard programmingCard = new ProgrammingCard(
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "name")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "imagePath")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "actionClassName")).get(0),
                            (String) new ArrayList<>(JsonPath.read(json, "$[?(@.name == '" + playerName + "')]." + cardsToGet + ".['" + current + "']." + "commandName")).get(0)
                    );
                    cards.add(programmingCard);
                }
            }
            current += 1;
        }
        return cards;
    }

    public synchronized ArrayList<String> getAllGames() {
        String json = getFileAsString("retrievedGames.json");
        return JsonPath.read(json, "$.[*]");
    }


    private synchronized String getFileAsString(String fileName) {
        String json = null;
        boolean access;
        do {
            access = AccessDataFile.requestFileAccess(fileName);
            if (access) {
                try {
                    json = new String(Files.readAllBytes(Paths.get("data/" + fileName)));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    AccessDataFile.releaseFileAccess(fileName);
                }
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    AccessDataFile.releaseFileAccess(fileName);
                    e.printStackTrace();
                }
            }
        } while(json == null);
        return json;
    }

    public boolean checkReceivedCardSequence(String playerName) {
        String json = getFileAsString("cardSequenceRequests.json");

        try {
            Map<String, Object> playerData = JsonPath.read(json, "$.['" + playerName + "']");
            if (playerData == null || playerData.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (PathNotFoundException e) {
            return false;
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
