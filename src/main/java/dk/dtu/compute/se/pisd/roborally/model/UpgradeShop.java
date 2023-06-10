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
    UpgradeCard placeHolderPermanentCard;
    TempUpgradeCard placeHolderTemporaryCard;

    public UpgradeShop() {
        currentPermanentCardCounter = 0;
        currentTemporaryCardCounter = 0;
        placeHolderPermanentCard = new UpgradeCard("PlaceHolder", "", 0, "hansi.png", "");
        placeHolderTemporaryCard = new TempUpgradeCard("PlaceHolder", "", 0, "hansi.png", "");
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
        if (permanentCards.size() != 0) {
            this.selectedPermanentCard = permanentCards.get(currentPermanentCardCounter);
        }
        notifyChange();
    }

    public void nextTemporaryCard() {
        currentTemporaryCardCounter += 1;
        if (currentTemporaryCardCounter == temporaryCards.size()) {
            currentTemporaryCardCounter = 0;
        }
        if (permanentCards.size() != 0) {
            this.selectedTemporaryCard = temporaryCards.get(currentTemporaryCardCounter);
            notifyChange();
        }
    }

    public UpgradeCard getSelectedPermanentCard() {
        if (this.permanentCards.size() == 0) {
            return placeHolderPermanentCard;
        }
        return this.selectedPermanentCard;
    }

    public TempUpgradeCard getSelectedTemporaryCard() {
        if (this.temporaryCards.size() == 0) {
            return placeHolderTemporaryCard;
        }
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
        if (permanentCards.size() == 0) {
            this.selectedPermanentCard = placeHolderPermanentCard;
        }
        else {
            currentPermanentCardCounter = 0;
            this.selectedPermanentCard = permanentCards.get(currentPermanentCardCounter);
        }
        notifyChange();
    }

    public void removeTemporaryUpgradeCard(TempUpgradeCard card) {
        temporaryCards.remove(card);
        if (this.temporaryCards.size() == 0) {
            this.selectedTemporaryCard = placeHolderTemporaryCard;
        }
        else {
            currentTemporaryCardCounter = 0;
            this.selectedTemporaryCard = temporaryCards.get(currentTemporaryCardCounter);
        }
        notifyChange();
    }


    public void removePermanentUpgradeCardByName(String cardName) {
        permanentCards.removeIf(card -> card.getName().equals(cardName));
        if (permanentCards.size() == 0) {
            this.selectedPermanentCard = placeHolderPermanentCard;
        }
        else {
            currentPermanentCardCounter = 0;
            this.selectedPermanentCard = permanentCards.get(currentPermanentCardCounter);
        }
        notifyChange();
    }

    public void removeTemporaryUpgradeCardByName(String cardName) {
        temporaryCards.removeIf(card -> card.getName().equals(cardName));
        if (this.temporaryCards.size() == 0) {
            this.selectedTemporaryCard = placeHolderTemporaryCard;
        }
        else {
            currentTemporaryCardCounter = 0;
            this.selectedTemporaryCard = temporaryCards.get(currentTemporaryCardCounter);
        }
        notifyChange();
    }


    public ArrayList<UpgradeCard> getAllPermanentUpgradeCards() {
        return this.permanentCards;
    }

    public ArrayList<TempUpgradeCard> getAllTemporaryUpgradeCards() {
        return this.temporaryCards;
    }


}
