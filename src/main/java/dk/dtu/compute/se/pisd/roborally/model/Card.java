package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class Card extends Subject {

    String name;
    int cost;
    String effect;

    String imagePath;
    String actionClassName;



    public String getName() {
        return this.name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost(){
        return this.cost;
    }

    public void setCost(){
        this.cost = cost;
    }

    public String getEffect(){
        return this.effect;
    }

    public void setEffect(){
        this.effect = effect;
    }


}
