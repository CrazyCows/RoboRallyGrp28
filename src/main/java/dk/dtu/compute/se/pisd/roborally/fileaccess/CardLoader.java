package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.card.*;

import java.io.*;
import java.util.*;


public class CardLoader {
    private static CardLoader cardLoader = null;
    Random rand = new Random();
    ArrayList<ProgrammingCard> programmingCards;
    ArrayList<SpecialProgrammingCard> specialProgrammingCards;
    ArrayList<DamageCard> damageCards;
    ArrayList<UpgradeCard> permUpgradeCards;
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
            this.permUpgradeCards = new ArrayList<>(cardData.getUpgradeCards());
            for (UpgradeCard card : permUpgradeCards) {
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

    public void sendCardSequenceRequest(List<ProgrammingCard> programmingCardsInput, String name) {

        boolean access;
        do {
            access = AccessDataFile.requestFileAccess(CARDSEQUENCE + "." + JSON_EXT);
            if (!access) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } while(!access);

        // Create a new list to hold the modified programming cards
        List<ProgrammingCard> modifiedProgrammingCards = new ArrayList<>();

        // Iterate over the programming cards input
        for (ProgrammingCard card : programmingCardsInput) {
            // Create a new ProgrammingCard object with the same properties, excluding the "action" field
            ProgrammingCard modifiedCard = new ProgrammingCard(
                    card.getName(),
                    card.getImagePath(),
                    card.getActionClassName(),
                    card.getCommandName()
            );

            // Add the modified card to the new list
            modifiedProgrammingCards.add(modifiedCard);
            //System.out.println("in modifiedProgrammingCards, adding " + card.getName()); //DONT REMOVE ATM
        }

        // Create the JSON structure with the modified programming cards
        Map<String, List<ProgrammingCard>> playerCardsMap = new HashMap<>();
        playerCardsMap.put("programmingCards", modifiedProgrammingCards);

        Map<String, Map<String, List<ProgrammingCard>>> wrapperMap = new HashMap<>();
        wrapperMap.put(name, playerCardsMap);

        String filename = DATAFOLDER + "/" + CARDSEQUENCE + "." + JSON_EXT;

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            // Write the JSON to file...
            fileWriter = new FileWriter(filename, false);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(wrapperMap, new TypeToken<Map<String, Map<String, List<ProgrammingCard>>>>() {}.getType(), writer);
            System.out.println("sendCardSequenceRequest finished writing json");
        } catch (IOException e1) {
            System.out.println("An exception occurred while creating the FileWriter:");
            e1.printStackTrace();
        } finally {
            try {
                AccessDataFile.releaseFileAccess(CARDSEQUENCE + "." + JSON_EXT);
                if (writer != null) {
                    writer.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e2) {
                AccessDataFile.releaseFileAccess(CARDSEQUENCE + "." + JSON_EXT);
                System.out.println("An exception occurred while closing the writers:");
                e2.printStackTrace();
            }
        }
    }

    public ArrayList<ProgrammingCard> loadCardSequence(String name) {

        boolean access;
        do {
            access = AccessDataFile.requestFileAccess("cardSequenceRequestsHelper.json");
            if (!access) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } while(!access);


        Adapter<CardAction> adapter = new Adapter<>();

        extractPlayerAndSaveToJson(name);
        //Saves the file, the immideatly opens it
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        InputStream inputStream = null;
        JsonReader reader = null;
        try {
            inputStream = new FileInputStream("data/cardSequenceRequestsHelper.json"); //I think we need to clear this badboy helper thing

            // In simple cases, we can create a Gson object with new Gson():
            GsonBuilder simpleBuilder = new GsonBuilder()
                    .registerTypeAdapter(CardAction.class, adapter);
            Gson gson = simpleBuilder.create();

            ArrayList<ProgrammingCard> result;

            reader = gson.newJsonReader(new InputStreamReader(inputStream)); //CardsequenceRequestHelper has the old cards sometimes instead of getting updated
            CardSequenceTemplate template = gson.fromJson(reader, CardSequenceTemplate.class); //This template is wrong


            result = new ArrayList<>(template.programmingCards);

            ArrayList<ProgrammingCard> temp = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                temp.add(result.get(result.size() - 5 + i ));
            }
            result = temp;

            for (ProgrammingCard card : result) {
                card.createAction();
                card.createCommand();
            }

            reader.close();

            AccessDataFile.releaseFileAccess("cardSequenceRequestsHelper.json");
            return result;

        } catch (IOException e1) {
            AccessDataFile.releaseFileAccess("cardSequenceRequestsHelper.json");
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

    private synchronized void extractPlayerAndSaveToJson(String name) {

        boolean access;
        do {
            access = AccessDataFile.requestFileAccess("cardSequenceRequests.json");
            if (!access) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } while(!access);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();

        try (FileReader reader = new FileReader("data/cardSequenceRequests.json")) {
            //System.out.println("The reader is: " + reader);
            JsonObject jsonData = parser.parse(reader).getAsJsonObject();

            // Access the player based on playerName
            JsonObject player = jsonData.getAsJsonObject(name);

            if (player == null) {
                System.out.println("BAD STUFF");
            }

            // Get the "programmingCards" array from the player object
            JsonArray programmingCards = player.getAsJsonArray("programmingCards"); //Already wrong here

            // Create a new JSON object to store the extracted "programmingCards" array
            JsonObject extractedData = new JsonObject();
            extractedData.add("programmingCards", programmingCards);

            // Save the extracted data to a new JSON file
            try (FileWriter writer = new FileWriter("data/cardSequenceRequestsHelper.json")) {
                gson.toJson(extractedData, writer);
                System.out.println("Wrote to cardSequenceRequestsHelper");
            }

            AccessDataFile.releaseFileAccess("cardSequenceRequests.json");

            System.out.println("Extraction completed successfully.");
        } catch (IOException e) {
            AccessDataFile.releaseFileAccess("cardSequenceRequests.json");
            e.printStackTrace();
        }
    }



    public ArrayList<ProgrammingCard> getProgrammingCards() {
        return programmingCards;
    }

    public ArrayList<DamageCard> getDamageCards() {
        return damageCards;
    }

    public ArrayList<UpgradeCard> getPermUpgradeCards() {
        return permUpgradeCards;
    }

    public ArrayList<TempUpgradeCard> getTempUpgradeCards() {
        return tempUpgradeCards;
    }


}