package dk.dtu.compute.se.pisd.roborally.fileaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class JsonInterpreterTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getPlayerNamesAndReadyStates() {
        JsonInterpreter jsonInterpreter = new JsonInterpreter();

        ArrayList<String> names = jsonInterpreter.getPlayerNames();
        ArrayList<Boolean> readyStates = new ArrayList<>();
        System.out.println("All ready: " + jsonInterpreter.isAllReady());

        System.out.println("names: ");
        for (String name : names) {
            System.out.println("-  " + name);
        }
        System.out.println();

        for (String name : names) {
            readyStates.add(jsonInterpreter.isReady(name));
            System.out.println(name + " readyState: " + jsonInterpreter.isReady(name));
        }

        assertNotNull(names);
        assertNotEquals(names.size(), 0);
        assertNotEquals(readyStates.size(), 0);
    }

    @Test
    void isAnyReady() {
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        System.out.println(jsonInterpreter.isAnyReady());
    }

    @Test
    void getAllCardsFromPlayer() {
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        ArrayList<String> names = jsonInterpreter.getPlayerNames();
        jsonInterpreter.getAllCardsFromPlayer("Player 1", "handPile");
    }

    @Test
    void TestCheckReceivedCardSequence() {
        JsonInterpreter jsonInterpreter = new JsonInterpreter();
        System.out.println(jsonInterpreter.checkReceivedCardSequence("kitty"));
    }

}