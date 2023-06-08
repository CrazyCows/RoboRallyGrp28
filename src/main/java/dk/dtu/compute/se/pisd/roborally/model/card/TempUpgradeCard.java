package dk.dtu.compute.se.pisd.roborally.model.card;

import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;

import java.lang.reflect.InvocationTargetException;

public class TempUpgradeCard extends Card {

    CardAction<TempUpgradeCard> action;
    int cost;

    public TempUpgradeCard(String name, String effect, int cost, String imagePath, String actionClassName) {
        this.name = name;
        this.effect = effect;
        this.cost = cost;
        this.imagePath = imagePath;
        this.actionClassName = actionClassName;

    }

    @SuppressWarnings("unchecked")
    public void createAction() {
        try {
            Class<?> eventClass = Class.forName(this.actionClassName);
            this.action = (CardAction<TempUpgradeCard>) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.action = null;
        }
    }


    public void setAction(CardAction<TempUpgradeCard> action) {
        this.action = action;
    }

    public CardAction<TempUpgradeCard> getAction() {
        return this.action;
    }

    public int getCost() {
        return this.cost;
    }

}
