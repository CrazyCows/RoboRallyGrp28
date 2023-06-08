package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;

public class EnergySpace extends FieldAction {

    public int getEnergyCubes() {
        return energyCubes;
    }

    public void setEnergyCubes(int energyCubes) {
        this.energyCubes = energyCubes;
    }

    private int energyCubes = 1;


    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (this.energyCubes > 0){ //TODO: Update view to show this
            this.energyCubes--;
            Player player = space.getPlayer();
            player.addEnergyCubes(1);
            System.out.println(player.getName() + " draws an energy cube from the Energy Space they landed on, and now have " + player.getEnergyCubes() + " energy cubes, leaving the energy space with " + this.energyCubes + " energy cubes");
        }
        if (this.energyCubes == 0){
            space.notifyChange();
        }
        return false;
    }
}
