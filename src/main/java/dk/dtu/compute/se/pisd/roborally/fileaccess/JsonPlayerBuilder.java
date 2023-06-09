package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.card.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonPlayerBuilder {
    private Player player;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Object> playerData = new HashMap<>();
    private Map<String, Object> program = new HashMap<>();
    private Map<String, Object> handPile = new HashMap<>();
    private Map<String, Object> drawPile = new HashMap<>();
    private Map<String, Object> discardPile = new HashMap<>();
    private Map<String, Object> permUpgradeCards = new HashMap<>();
    private Map<String, Object> tempUpgradeCards = new HashMap<>();

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
        playerData.put("master", player.isMaster());
        playerData.put("inGame", player.isInGame());
        playerData.put("heading", player.getHeading());
        playerData.put("isMaster", player.isMaster());
        playerData.put("checkpointsCollected", player.getCheckpointsCollected());

        /*
        playerData.put("startplace", player.getStartSpace());
        playerData.put("heading", player.getHeading());
         */
        file = new File("data" + "/playerData.json");

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
            playerData.put("posx", player.getSpace().getPosition()[0]);
            playerData.put("posy", player.getSpace().getPosition()[1]);
            playerData.put("readystate", player.isReady());
            playerData.put("master", player.isMaster());
            playerData.put("inGame", player.isInGame());
            playerData.put("color", player.getColor());
            playerData.put("message", player.getMessage());
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, playerData);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // setup for piles, decks or CommandCardFields containing Damage cards and / or Programming cards
    public HashMap<Integer, HashMap<String, Object>> playableCardsSetup(ArrayList<Card> cards) {
        HashMap<Integer, HashMap<String, Object>> objects = new HashMap<>();
        HashMap<String, Object> object = new HashMap<>();
        int count = 0;
        for (Card card : cards) {
            if (card instanceof ProgrammingCard programmingCard) {
                object.put("name", programmingCard.getName());
                object.put("imagePath", programmingCard.getImagePath());
                object.put("actionClassName", programmingCard.getActionClassName());
                object.put("commandName", programmingCard.getCommandName());
            }
            else if (card instanceof DamageCard damageCard) {
                object.put("name", damageCard.getName());
                object.put("effect", damageCard.getEffect());
                object.put("imagePath", damageCard.getImagePath());
                object.put("actionClassName", damageCard.getActionClassName());
            }
            else if (card instanceof UpgradeCard permanentUpgradeCard) {
                object.put("name", permanentUpgradeCard.getName());
                object.put("effect", permanentUpgradeCard.getEffect());
                object.put("cost", permanentUpgradeCard.getCost());
                object.put("imagePath", permanentUpgradeCard.getImagePath());
                object.put("actionClassName", permanentUpgradeCard.getActionClassName());
            }
            else if (card instanceof TempUpgradeCard temporaryUpgradeCard) {
                object.put("name", temporaryUpgradeCard.getName());
                object.put("effect", temporaryUpgradeCard.getEffect());
                object.put("cost", temporaryUpgradeCard.getCost());
                object.put("imagePath", temporaryUpgradeCard.getImagePath());
                object.put("actionClassName", temporaryUpgradeCard.getActionClassName());
            }
            objects.put(count, object);
            count += 1;
        }
        return objects;
    }

    // to be used as a means to save a player fully.
    public void createPlayerJSON(GameController gameController) {
        if (this.player == null) {
            System.out.println("Error: Player is null. Can't create JSON");
            return;
        }

        playerData.put("name", player.getName());
        playerData.put("color", player.getColor());
        playerData.put("readystate", player.isReady());
        playerData.put("playerisLead", player.isLeader());
        playerData.put("posx", player.getSpace().getPosition()[0]);
        playerData.put("posy", player.getSpace().getPosition()[1]);
        playerData.put("energycubes",player.getEnergyCubes());
        playerData.put("master", player.getMaster());
        playerData.put("inGame", player.isInGame());
        playerData.put("heading", player.getHeading());
        playerData.put("isMaster", player.isMaster());
        playerData.put("checkpointsCollected", player.getCheckpointsCollected());


        ArrayList<Card> cards = new ArrayList<>();
        for (CommandCardField commandCardField : player.getHandPile()) {
            cards.add(commandCardField.getCard());
        }
        playerData.put("handPile", playableCardsSetup(cards));

        cards = new ArrayList<>();
        for (CommandCardField commandCardField : player.getProgram()) {
            cards.add(commandCardField.getCard());
        }
        playerData.put("program", playableCardsSetup(cards));

        cards = new ArrayList<>(player.getDrawPile());
        playerData.put("drawPile", playableCardsSetup(cards));

        cards = new ArrayList<>(player.getDiscardPile());
        playerData.put("discardPile", playableCardsSetup(cards));

        cards = new ArrayList<>(player.getPermanentUpgradeCards());
        playerData.put("permanentUpgradeCards", playableCardsSetup(cards));

        cards = new ArrayList<>(player.getTemporaryUpgradeCards());
        playerData.put("temporaryUpgradeCards", playableCardsSetup(cards));

    }

    // TODO: Cards that players have will be removed from the upgradeshop

}
