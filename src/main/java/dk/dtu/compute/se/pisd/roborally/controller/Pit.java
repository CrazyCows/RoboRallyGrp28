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
        Player currentPlayer = space.getPlayer();
        System.out.println(currentPlayer.getName() + " has fallen in a pit");
        currentPlayer.addSpamCardToDiscardPile(); //Draws two spam damage cards
        currentPlayer.addSpamCardToDiscardPile();
        currentPlayer.setSpace(currentPlayer.startSpace);
        currentPlayer.discardCurrentProgram();
        currentPlayer.setHeading(Heading.NORTH);



        /*
        System.out.println("THE CHECKPOINT");
        Board board = gameController.board;
        Player currentPlayer = space.getPlayer();
        if (currentPlayer.getCheckpointsCollected() == number){
            currentPlayer.iterateCheckpointsCollected(); //We could also just currentplayer.checkpointscolled = number
            if (currentPlayer.getCheckpointsCollected() == board.getNumberOfCheckpoints()){
                gameController.win(currentPlayer);
            }
        }
         */
        /*
        //Alternative to this is to let the choosing currentPlayer/client finish the entire game, and then push the new
        //game state to the server, which everyone would then receive. This would be easier but would mess up the
        //animations and such, because it would suddenly set the game to be somewhere else.
        if (gameController.localPlayer == currentPlayer){
            //TODO: ASK FOR ROBOT HEADING
            //Heading newHeading = askforheading;
            //currentPlayer.setHeading(newHeading);
            //TODO: PUSH HEADING SOMEWHERE (SPECIFIC FILE?)

        } else {
            //TODO: Repeatedly poll until direction has been pushed
            //currentPlayer.setHeading(newHeading); //newHeading from server
        }*/

        return false;
    }
}