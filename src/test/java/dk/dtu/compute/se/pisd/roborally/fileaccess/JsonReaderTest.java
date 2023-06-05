package dk.dtu.compute.se.pisd.roborally.fileaccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getPlayerNamesAndReadyStates() {
        JsonReader jsonReader = new JsonReader();

        ArrayList<String> names = jsonReader.getPlayerNames();
        System.out.println("All ready: " + jsonReader.isAllReady());

        System.out.println("names: ");
        for (String name : names) {
            System.out.println(name);
        }
        for (String name : names) {
            System.out.println(name + " readyState: " + jsonReader.isReady(name));
        }


    }

}