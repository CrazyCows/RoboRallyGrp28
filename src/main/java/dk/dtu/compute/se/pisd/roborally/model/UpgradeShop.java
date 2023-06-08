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

    public UpgradeShop() {
        currentPermanentCardCounter = 0;
        currentTemporaryCardCounter = 0;
        UpgradeCard placeHolderPermanentCard = new UpgradeCard("PlaceHolder", "", 0, "hansi.png", "");
        TempUpgradeCard placeHolderTemporaryCard = new TempUpgradeCard("PlaceHolder", "", 0, "hansi.png", "");
        this.selectedPermanentCard = placeHolderPermanentCard;
        this.selectedTemporaryCard = placeHolderTemporaryCard;

    }

    public void setPermanentUpgradeDeck(ArrayList<UpgradeCard> cards) {
        this.permanentCards = cards;
        this.selectedPermanentCard = permanentCards.get(0);
        notifyChange();
    }

    public void setTemporaryUpgradeDeck(ArrayList<TempUpgradeCard> cards) {
        this.temporaryCards = cards;
        this.selectedTemporaryCard = temporaryCards.get(0);
        notifyChange();
    }

    public void nextPermanentCard() {
        currentPermanentCardCounter += 1;
        if (currentPermanentCardCounter == permanentCards.size()) {
            currentPermanentCardCounter = 0;
        }
        this.selectedPermanentCard = permanentCards.get(currentPermanentCardCounter);
        notifyChange();
    }

    public void nextTemporaryCard() {
        currentTemporaryCardCounter += 1;
        if (currentTemporaryCardCounter == temporaryCards.size()) {
            currentTemporaryCardCounter = 0;
        }
        this.selectedTemporaryCard = temporaryCards.get(currentTemporaryCardCounter);
        notifyChange();
    }

    public UpgradeCard getSelectedPermanentCard() {
        return this.selectedPermanentCard;
    }

    public TempUpgradeCard getSelectedTemporaryCard() {
        return this.selectedTemporaryCard;
    }

    public String getSelectedPermanentCardImage() {
        return this.selectedPermanentCard.getImagePath();
    }

    public String getSelectedTemporaryCardImage() {
        return this.selectedTemporaryCard.getImagePath();
    }

    public int getSelectedPermanentCost() {
        return this.selectedPermanentCard.getCost();
    }

    public int getSelectedTemporaryCardCost() {
        return this.selectedTemporaryCard.getCost();
    }

    public void removePermanentUpgradeCard(UpgradeCard card) {
        permanentCards.remove(card);
        currentPermanentCardCounter = 0;
        notifyChange();
    }

    public void removeTemporaryUpgradeCard(TempUpgradeCard card) {
        temporaryCards.remove(card);
        currentTemporaryCardCounter = 0;
        notifyChange();
    }



}
