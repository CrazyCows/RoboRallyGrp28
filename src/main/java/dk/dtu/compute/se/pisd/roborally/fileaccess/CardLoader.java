package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;
import dk.dtu.compute.se.pisd.roborally.controller.field.LaserGun;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.card.*;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;

import java.io.*;
import java.util.*;


public class CardLoader {
    private static CardLoader cardLoader = null;
    Random rand = new Random();
    ArrayList<ProgrammingCard> programmingCards;
    ArrayList<SpecialProgrammingCard> specialProgrammingCards;
    ArrayList<DamageCard> damageCards;
    ArrayList<UpgradeCard> upgradeCards;
    ArrayList<TempUpgradeCard> tempUpgradeCards;



    private static final String CARDSFOLDER = "cards";
    private static final String DEFAULTCARDS = "defaultCards";
    private static final String DATAFOLDER = "data";
    private static final String CARDSEQUENCE = "cardSequenceRequest";
    private static final String JSON_EXT = "json";

    public static CardLoader getInstance(){ //Singleton
        if (cardLoader == null){
            cardLoader = new CardLoader();
        }
        return cardLoader;
    }

    private CardLoader() {
        System.out.println("Created singleton class");

        ClassLoader classLoader;
        InputStream inputStream = null;

        JsonReader reader = null;
        try {
            classLoader = LoadBoard.class.getClassLoader();
            inputStream = classLoader.getResourceAsStream(CARDSFOLDER + "/" + DEFAULTCARDS + "." + JSON_EXT);

            if (inputStream == null) {
                // TODO: Handle error
                System.out.println("ERROR");
            }

            // WE BUILT THIS CITY.
            GsonBuilder simpleBuilder = new GsonBuilder()
                    .registerTypeAdapter(CardAction.class, new Adapter<CardAction<Card>>());
            Gson gson = simpleBuilder.create();

            // Read the JSON data for all card types
            reader = new JsonReader(new InputStreamReader(inputStream));
            CardDataTemplate cardData = gson.fromJson(reader, CardDataTemplate.class);

            // Extract and create programming cards
            this.programmingCards = new ArrayList<>(cardData.getProgrammingCards());
            for (ProgrammingCard card : programmingCards) {
                card.createAction();
                card.createCommand();
            }

            // Extract and create special Programming cards
            this.specialProgrammingCards = new ArrayList<>(cardData.getSpecialProgrammingCards());
            for (SpecialProgrammingCard card : specialProgrammingCards) {
                card.createAction();
            }

            // Extract and create damage cards
            this.damageCards = new ArrayList<>(cardData.getDamageCards());
            for (DamageCard card : damageCards) {
                card.createAction();
            }

            // Extract and create upgrade cards
            this.upgradeCards = new ArrayList<>(cardData.getUpgradeCards());
            for (UpgradeCard card : upgradeCards) {
                card.createAction();
            }

            // Extract and create temporary upgrade cards
            this.tempUpgradeCards = new ArrayList<>(cardData.getTempUpgradeCards());
            for (TempUpgradeCard card : tempUpgradeCards) {
                card.createAction();
            }

            // Close the reader
            reader.close();

        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                }
            }
        }
        System.out.println("Commandcards created");
    }

    public void sendCardSequenceRequest(List<Card> cards) {

        CardSequenceTemplate cardSequenceTemplate = new CardSequenceTemplate();

        for (ProgrammingCard card : programmingCards) {
            if (card != null) {
                cardSequenceTemplate.getProgrammingCards().add(card);
            }
        }

        String filename = DATAFOLDER + "/" + CARDSEQUENCE + "." + JSON_EXT;

        GsonBuilder simpleBuilder = new GsonBuilder().setPrettyPrinting();
        simpleBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equals("action");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            File directory = new File(DATAFOLDER);
            if (!directory.exists()) {
                directory.mkdirs(); // Create directories if they don't exist
            }

            File file = new File(filename);
            fileWriter = new FileWriter(file, false); // Set second argument to 'true' if you want to append to an existing file
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(cardSequenceTemplate, cardSequenceTemplate.getClass(), writer);
        } catch (IOException e1) {
            System.out.println("An exception occurred while creating the FileWriter:");
            e1.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e2) {
                System.out.println("An exception occurred while closing the writers:");
                e2.printStackTrace();
            }
        }
    }

    public ArrayList<ProgrammingCard> loadCardSequence(String name) {

        extractPlayerAndSaveToJson(name);

        InputStream inputStream = null;

        JsonReader reader = null;
        try {
            inputStream = new FileInputStream("data/cardSequenceRequestsHelper.json");

            // In simple cases, we can create a Gson object with new Gson():
            GsonBuilder simpleBuilder = new GsonBuilder()
                    .registerTypeAdapter(FieldAction.class, new Adapter<CardAction<ProgrammingCard>>());
            Gson gson = simpleBuilder.create();

            ArrayList<ProgrammingCard> result;

            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            CardSequenceTemplate template = gson.fromJson(reader, CardSequenceTemplate.class);

            result = new ArrayList<>(template.programmingCards);

            for (ProgrammingCard card : result) {
                card.createAction();
                card.createCommand();
            }

            reader.close();
            return result;

        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                }
            }
        }
        return null;
    }

    private void extractPlayerAndSaveToJson(String name) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        try (FileReader reader = new FileReader("data/cardSequenceRequests.json")) {
            JsonObject jsonData = parser.parse(reader).getAsJsonObject();

            // Access the player based on playerName
            JsonObject player = jsonData.getAsJsonObject(name);

            // Get the "programmingCards" array from the player object
            JsonArray programmingCards = player.getAsJsonArray("programmingCards");

            // Create a new JSON object to store the extracted "programmingCards" array
            JsonObject extractedData = new JsonObject();
            extractedData.add("programmingCards", programmingCards);

            // Save the extracted data to a new JSON file
            try (FileWriter writer = new FileWriter("data/cardSequenceRequestsHelper.json")) {
                gson.toJson(extractedData, writer);
            }

            System.out.println("Extraction completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public ArrayList<ProgrammingCard> getProgrammingCards() {
        return programmingCards;
    }

    public ArrayList<DamageCard> getDamageCards() {
        return damageCards;
    }

    public ArrayList<UpgradeCard> getUpgradeCards() {
        return upgradeCards;
    }

    public ArrayList<TempUpgradeCard> getTempUpgradeCards() {
        return tempUpgradeCards;
    }


}