package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.fileaccess.CardLoader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class CardLoaderTest {


    @Test
    void cardLoader() {
        CardLoader cardLoader = CardLoader.getInstance();

        cardLoader.loadCardSequence("b");

    }


}