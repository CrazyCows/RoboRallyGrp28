package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Pit extends FieldAction {
    private Heading heading;


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        System.out.println(player.getName() + " has fallen in a pit");
        player.addSpamCardToDiscardPile(); //Draws two spam damage cards
        player.addSpamCardToDiscardPile();
        player.setSpace(player.startSpace); //Order shouldn't really matter here
        player.discardCurrentProgram();
        player.setHeading(Heading.NORTH);


        /*
        //Alternative to this is to let the choosing player/client finish the entire game, and then push the new
        //game state to the server, which everyone would then receive. This would be easier but would mess up the
        //animations and such, because it would suddenly set the game to be somewhere else.
        if (gameController.localPlayer == player){
            //TODO: ASK FOR ROBOT HEADING
            //Heading newHeading = askforheading;
            //player.setHeading(newHeading);
            //TODO: PUSH HEADING SOMEWHERE (SPECIFIC FILE?)

        } else {
            //TODO: Repeatedly poll until direction has been pushed
            //player.setHeading(newHeading); //newHeading from server
        }*/

        return false;
    }
}