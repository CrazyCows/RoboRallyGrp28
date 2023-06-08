package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.card.TempUpgradeCard;
import dk.dtu.compute.se.pisd.roborally.model.card.UpgradeCard;

import java.util.ArrayList;

public class UpgradeShop extends Subject {

    ArrayList<UpgradeCard> permanentCards;
    ArrayList<TempUpgradeCard> temporaryCards;
    UpgradeCard selectedPermanentCard;
    TempUpgradeCard selectedTemporaryCard;
    int currentPermanentCardCounter;
    int currentTemporaryCardCounter;

    public UpgradeShop(ArrayList<UpgradeCard> permanentCards, ArrayList<TempUpgradeCard> temporaryCards) {
        this.permanentCards = permanentCards;
        this.temporaryCards = temporaryCards;
        this.selectedPermanentCard = permanentCards.get(0);
        this.selectedTemporaryCard = temporaryCards.get(0);
        currentPermanentCardCounter = 0;
        currentTemporaryCardCounter = 0;
    }

    public UpgradeCard nextPermanentCard() {
        currentPermanentCardCounter += 1;
        this.selectedPermanentCard = permanentCards.get(currentPermanentCardCounter);
        notifyChange();
        return this.selectedPermanentCard;
    }

    public UpgradeCard nextTemporaryCard() {
        currentTemporaryCardCounter += 1;
        this.selectedTemporaryCard = temporaryCards.get(currentTemporaryCardCounter);
        notifyChange();
        return this.selectedTemporaryCard;
    }

    public UpgradeCard getSelectedPermanentCard() {
        return this.selectedPermanentCard;
    }

    public UpgradeCard getSelectedTemporaryCard() {
        return this.selectedTemporaryCard;
    }

}
