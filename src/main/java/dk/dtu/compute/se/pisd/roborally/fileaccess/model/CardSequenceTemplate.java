package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.card.ProgrammingCard;

import java.util.ArrayList;
import java.util.List;

public class CardSequenceTemplate {

    public List<ProgrammingCard> programmingCards = new ArrayList<>();

    public List<ProgrammingCard> getProgrammingCards() {
        return programmingCards;
    }

}