package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.controller.CardAction;
import dk.dtu.compute.se.pisd.roborally.controller.DamageAction;
import dk.dtu.compute.se.pisd.roborally.controller.UpgradeAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Adapter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class CardLoader {
    private static CardLoader cardLoader = null;
    Random rand = new Random();
    ArrayList<ProgrammingCard> programmingCards;
    ArrayList<DamageCard> damageCards;
    ArrayList<UpgradeCard> upgradeCards;
    ArrayList<TempUpgradeCard> tempUpgradeCards;



    private static final String CARDSFOLDER = "cards";
    private static final String DEFAULTCARDS = "defaultCards";
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


    public void loadCardsFromJsonFile(){
        ClassLoader classLoader;
        InputStream inputStream = null;

        JsonReader reader = null;
        Gson gson = new Gson();
        try{
            String json = new String(Files.readAllBytes(Paths.get("cards/cards.json")));




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